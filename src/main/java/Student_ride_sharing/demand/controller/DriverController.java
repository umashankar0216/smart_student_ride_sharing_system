package Student_ride_sharing.demand.controller;

import Student_ride_sharing.demand.dto.*;
import Student_ride_sharing.demand.service.RideRequestService;
import Student_ride_sharing.demand.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DriverController {

    private final RideRequestService rideRequestService;
    private final RideService rideService;

    // 1. Fetch the clustered demand groups for the Driver Dashboard
    @GetMapping("/dashboard/demand")
    public ResponseEntity<List<GroupedRequestDTO>> getDashboardDemand() {
        List<GroupedRequestDTO> dashboardData = rideRequestService.getGroupedDemandsForDrivers();
        return ResponseEntity.ok(dashboardData);
    }

    // 2. Driver clicks "Accept" on a group
    @PostMapping("/rides/create-from-demand")
    public ResponseEntity<RideResponseDto> createRideFromDemand(@RequestBody CreateRideFromDemandDto dto) {
        RideResponseDto response = rideService.acceptDemandAndCreateRide(dto);
        return ResponseEntity.ok(response);
    }

    // 3. Driver cancels their active ride
    @PutMapping("/rides/{rideId}/cancel")
    public ResponseEntity<String> cancelRide(@PathVariable Long rideId) {
        rideService.cancelRideByDriver(rideId);
        return ResponseEntity.ok("Ride cancelled successfully. Stranded students have been returned to the pending pool.");
    }

    // 4. Get grouped requests for a selected demand cluster
    @GetMapping("/demands/cluster-details")
    public ResponseEntity<List<RideRequestDetailedDto>> getClusterDetails(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam String preferredVehicle,
            @RequestParam String timeSlot) {

        List<RideRequestDetailedDto> details = rideService.getDetailedRequestsForCluster(source, destination, preferredVehicle, timeSlot);
        return ResponseEntity.ok(details);
    }

    // 5. Get rides owned by current driver
    @GetMapping("/rides")
    public ResponseEntity<List<RideResponseDto>> getDriverRides() {
        List<RideResponseDto> rides = rideService.getDriverRides();
        return ResponseEntity.ok(rides);
    }
    @GetMapping("/search-requests")
    public ResponseEntity<List<RideRequestDetailedDto>> searchStudentRequests(
            @RequestParam("source") String source,
            @RequestParam("destination") String destination,
            @RequestParam("preferredVehicle") String preferredVehicle,
            @RequestParam("date") String date) { // 🟢 Maps to incoming frontend URL variable frame cleanly

        // Route target arguments straight into your dynamic native cluster query layer
        List<RideRequestDetailedDto> matchedRequests = rideRequestService.searchManualClusterRequests(
                source,
                destination,
                preferredVehicle,
                date
        );
        return ResponseEntity.ok(matchedRequests);
    }

}