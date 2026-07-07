package Student_ride_sharing.demand.controller;

import Student_ride_sharing.demand.dto.BookingResponseDto;
import Student_ride_sharing.demand.dto.RideRequestDto;
import Student_ride_sharing.demand.dto.RideResponseDto;
import Student_ride_sharing.demand.service.BookingService;
import Student_ride_sharing.demand.service.RideRequestService;
import Student_ride_sharing.demand.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*") // Allows your React frontend to communicate with this API
@RequiredArgsConstructor
public class StudentController {

    private final RideRequestService rideRequestService;
    private final BookingService bookingService;
    private final RideService rideService;

    // 1. Submit a new ride request to the demand pool

    @PostMapping("/requests")
    public ResponseEntity<RideRequestDto> createRideRequest(@RequestBody RideRequestDto dto) {
        RideRequestDto response = rideRequestService.createRequest(dto);
        return ResponseEntity.ok(response);
    }

    // 2. Manually book a single seat on an existing active ride
    @PostMapping("/{studentId}/bookings/{rideId}")
    public ResponseEntity<BookingResponseDto> bookRide(
            @PathVariable Long studentId,
            @PathVariable Long rideId) {
        BookingResponseDto response = bookingService.createBooking(studentId, rideId);
        return ResponseEntity.ok(response);
    }

    // 3. Cancel an existing booking
    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking successfully cancelled. The seat has been restored.");
    }
    @GetMapping("/rides/search")
    public ResponseEntity<List<RideResponseDto>> searchAvailableRides(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam String vehicleType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime preferredTime) {

        List<RideResponseDto> availableRides = rideService.searchRidesForStudent(source, destination, vehicleType, preferredTime);
        return ResponseEntity.ok(availableRides);
    }
        @GetMapping("/bookings")
        public ResponseEntity<List<BookingResponseDto>> getMyBookings() {
            List<BookingResponseDto> studentHistory = bookingService.getStudentBookings();
            return ResponseEntity.ok(studentHistory);
        }
}