package Student_ride_sharing.demand.service;


import Student_ride_sharing.demand.dto.CreateRideFromDemandDto;
import Student_ride_sharing.demand.dto.RideRequestDetailedDto;
import Student_ride_sharing.demand.dto.RideResponseDto;
import Student_ride_sharing.demand.entity.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

public interface RideService {
    RideResponseDto acceptDemandAndCreateRide(CreateRideFromDemandDto dto);
    void cancelRideByDriver(Long rideId);
    List<RideResponseDto> getDriverRides();
    List<RideResponseDto> searchRidesForStudent(String source, String destination, String vehicleType, LocalDateTime preferredTime);
    List<RideRequestDetailedDto> getDetailedRequestsForCluster(String source, String destination, String preferredVehicle, String timeSlot);
}
