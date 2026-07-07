package Student_ride_sharing.demand.service;

import Student_ride_sharing.demand.dto.BookingResponseDto;
import Student_ride_sharing.demand.entity.Ride;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Long studentId, Long rideId);

    void createAutomatedBookingForCluster(Long studentId, Ride ride);

    void cancelBooking(Long bookingId);
    List<BookingResponseDto> getStudentBookings();
}