package Student_ride_sharing.demand.service.Impl;

import Student_ride_sharing.demand.dto.BookingResponseDto;
import Student_ride_sharing.demand.dto.UserResponseDto;
import Student_ride_sharing.demand.entity.*;
import Student_ride_sharing.demand.repository.BookingRepository;
import Student_ride_sharing.demand.repository.RideRepository;
import Student_ride_sharing.demand.repository.RideRequestRepository;
import Student_ride_sharing.demand.repository.UserRepository;
import Student_ride_sharing.demand.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final RideRequestRepository rideRequestRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long studentId, Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (ride.getOccupiedSeats() >= ride.getTotalSeats()) {
            throw new RuntimeException("This ride is completely full!");
        }

        RideRequest studentRequest = rideRequestRepository.findByStudentIdAndSourceAndDestinationAndStatus(
                studentId, ride.getSource(), ride.getDestination(), RequestStatus.PENDING
        );

        if (studentRequest != null) {
            if (studentRequest.getPreferredVehicle() != VehicleType.ANY && studentRequest.getPreferredVehicle() != ride.getVehicleType()) {
                throw new RuntimeException("Vehicle type mismatch! Your request prefers " + studentRequest.getPreferredVehicle()
                        + " but this ride is operating with a " + ride.getVehicleType());
            }

            studentRequest.setStatus(RequestStatus.FULFILLED);
            rideRequestRepository.save(studentRequest);
        }

        ride.setOccupiedSeats(ride.getOccupiedSeats() + 1);
        Ride updatedRide = rideRepository.save(ride);

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setRide(updatedRide);
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);

        BookingResponseDto response = modelMapper.map(savedBooking, BookingResponseDto.class);
        response.getRide().setTotalSeats(updatedRide.getTotalSeats());
        response.getRide().setOccupiedSeats(updatedRide.getOccupiedSeats());
        response.getRide().setAvailableSeats(updatedRide.getTotalSeats() - updatedRide.getOccupiedSeats());

        return response;
    }

    @Override
    @Transactional
    public void createAutomatedBookingForCluster(Long studentId, Ride ride) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Match the user's specific request within the cluster
        RideRequest studentRequest = rideRequestRepository.findByStudentIdAndSourceAndDestinationAndStatus(
                studentId,
                ride.getSource(),
                ride.getDestination(),
                RequestStatus.PENDING
        );

        if (studentRequest != null) {
            if (studentRequest.getPreferredVehicle() != VehicleType.ANY && studentRequest.getPreferredVehicle() != ride.getVehicleType()) {
                throw new RuntimeException("Vehicle type mismatch! Student prefers " + studentRequest.getPreferredVehicle()
                        + " but this ride is a " + ride.getVehicleType());
            }

            // Sync request state so it drops out of the driver pending metrics map
            studentRequest.setStatus(RequestStatus.FULFILLED);
            rideRequestRepository.save(studentRequest);
        }

        // 🟢 FIXED: Writes and commits the missing relationship data row straight to PostgreSQL!
        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setRide(ride);
        booking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking record not found"));

        booking.setStatus(BookingStatus.CANCELLED);

        Ride ride = booking.getRide();
        if (ride.getOccupiedSeats() > 0) {
            ride.setOccupiedSeats(ride.getOccupiedSeats() - 1);
            rideRepository.save(ride);
        }
//
//        RideRequest fulfilledRequest = rideRequestRepository.findByStudentIdAndSourceAndDestinationAndStatus(
//                booking.getStudent().getId(),
//                ride.getSource(),
//                ride.getDestination(),
//                RequestStatus.FULFILLED
//        );
//
//        if (fulfilledRequest != null) {
//            fulfilledRequest.setStatus(RequestStatus.PENDING);
//            rideRequestRepository.save(fulfilledRequest);
//        }

        bookingRepository.delete(booking);
    }

    // Inside BookingServiceImpl.java -> getStudentBookings() method

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getStudentBookings() {
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        List<Booking> bookings = bookingRepository.findStudentActiveBookings(currentUsername, BookingStatus.CONFIRMED);

        return bookings.stream()
                .map(booking -> {
                    BookingResponseDto dto = modelMapper.map(booking, BookingResponseDto.class);

                    if (booking.getRide() != null && dto.getRide() != null) {
                        // Calculate dynamic available seats
                        dto.getRide().setAvailableSeats(booking.getRide().getTotalSeats() - booking.getRide().getOccupiedSeats());
                    }

                    // 🟢 SAFE FALLBACK: If you don't have driverName in your Ride DTO,
                    // put the Driver's UserResponseDto inside the parent 'user' field cleanly:
                    if (booking.getRide() != null && booking.getRide().getDriver() != null) {
                        UserResponseDto driverDto = modelMapper.map(booking.getRide().getDriver(), UserResponseDto.class);
                        dto.setUser(driverDto); // Overwrites the root user node with the active driver details
                    }

                    return dto;
                })
                .toList();
    }
}