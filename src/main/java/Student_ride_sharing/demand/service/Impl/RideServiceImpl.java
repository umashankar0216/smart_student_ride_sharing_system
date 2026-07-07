package Student_ride_sharing.demand.service.Impl;

import Student_ride_sharing.demand.dto.CreateRideFromDemandDto;
import Student_ride_sharing.demand.dto.RideRequestDetailedDto;
import Student_ride_sharing.demand.dto.RideResponseDto;
import Student_ride_sharing.demand.entity.*;
import Student_ride_sharing.demand.repository.BookingRepository;
import Student_ride_sharing.demand.repository.RideRepository;
import Student_ride_sharing.demand.repository.RideRequestRepository;
import Student_ride_sharing.demand.repository.UserRepository;
import Student_ride_sharing.demand.service.BookingService;
import Student_ride_sharing.demand.service.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideRequestRepository rideRequestRepository;
    private final BookingService bookingService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public RideResponseDto acceptDemandAndCreateRide(CreateRideFromDemandDto dto) {

        List<RideRequest> matchingRequests = rideRequestRepository.findPendingRequestsByUsernamesAndCluster(
                dto.getSelectedUsernames(),
                dto.getSource(),
                dto.getDestination(),
                dto.getPreferredTime()
        );

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User driver = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated driver profile record not found"));

        Ride ride;

        if (dto.getExistingRideId() != null) {
            ride = rideRepository.findById(dto.getExistingRideId())
                    .orElseThrow(() -> new RuntimeException("Target active ride row not found"));

            if (ride.getOccupiedSeats() + matchingRequests.size() > ride.getTotalSeats()) {
                throw new RuntimeException("Cannot accept students. Selection exceeds total vehicle seat capacity.");
            }
        } else {
            ride = new Ride();
            ride.setDriver(driver);
            ride.setSource(dto.getSource());
            ride.setDestination(dto.getDestination());
            ride.setVehicleType(dto.getVehicleType());
            ride.setDepartureTime(dto.getPreferredTime());
            ride.setTotalSeats(dto.getTotalSeats());
            ride.setOccupiedSeats(0);

            ride.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);

            ride = rideRepository.save(ride);
        }

        int currentOccupied = ride.getOccupiedSeats();

        for (RideRequest request : matchingRequests) {
            request.setStatus(RequestStatus.FULFILLED);
            rideRequestRepository.save(request);

           bookingService.createAutomatedBookingForCluster(request.getStudent().getId(), ride);
            currentOccupied++;
        }

        ride.setOccupiedSeats(currentOccupied);
        Ride finalRide = rideRepository.save(ride);

        RideResponseDto responseDto = modelMapper.map(finalRide, RideResponseDto.class);
        responseDto.setAvailableSeats(finalRide.getTotalSeats() - finalRide.getOccupiedSeats());

        return responseDto;
    }

    @Override
    @Transactional
    public void cancelRideByDriver(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride record not found"));

        List<Booking> activeBookings = bookingRepository.findByRideIdAndStatus(rideId, BookingStatus.CONFIRMED);

        for (Booking booking : activeBookings) {
            User student = booking.getStudent();

            // 🟢 FIXED: Fetch using the route-matching query to safely find the requests
            List<RideRequest> fulfilledRequests = rideRequestRepository
                    .findFulfilledRequestsForCancellation(student.getId(), ride.getSource(), ride.getDestination());

            for (RideRequest request : fulfilledRequests) {
                request.setStatus(RequestStatus.PENDING); // Revert status cleanly back to pool
                rideRequestRepository.save(request);     // Commit update to database
            }

            // Delete the student's booking row
            bookingRepository.delete(booking);
        }

        // Delete the driver's ride row from the table
        rideRepository.delete(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponseDto> getDriverRides() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Ride> driverRides = rideRepository.findByDriverUsername(currentUsername);

        return driverRides.stream()
                .map(ride -> {
                    RideResponseDto dto = modelMapper.map(ride, RideResponseDto.class);
                    dto.setAvailableSeats(ride.getTotalSeats() - ride.getOccupiedSeats());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponseDto> searchRidesForStudent(String source, String destination, String vehicleType, LocalDateTime preferredTime) {
        List<Ride> availableRides = rideRepository.searchAvailableRides(
                source,
                destination,
                vehicleType,
                preferredTime
        );

        return availableRides.stream()
                .map(ride -> {
                    RideResponseDto dto = modelMapper.map(ride, RideResponseDto.class);
                    dto.setAvailableSeats(ride.getTotalSeats() - ride.getOccupiedSeats());
                    return dto;
                })
                .toList();
    }

    @Override
    public List<RideRequestDetailedDto> getDetailedRequestsForCluster(String source, String destination, String preferredVehicle, String timeSlot) {

        List<RideRequest> requests = rideRequestRepository.findDetailedRequestsInCluster(source, destination, preferredVehicle, timeSlot);

        return requests.stream()
                .map(request -> {
                    RideRequestDetailedDto dto = modelMapper.map(request, RideRequestDetailedDto.class);
                    // Manually populate fields from the User relationship object
                    if (request.getStudent() != null) {
                        dto.setStudentUsername(request.getStudent().getUsername());
                        dto.setStudentName(request.getStudent().getName());
                    }
                    return dto;
                })
                .toList();
    }
}