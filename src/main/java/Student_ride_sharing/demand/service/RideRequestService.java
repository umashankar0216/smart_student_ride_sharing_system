package Student_ride_sharing.demand.service;

import Student_ride_sharing.demand.dto.GroupedRequestDTO;
import Student_ride_sharing.demand.dto.RideRequestDetailedDto;
import Student_ride_sharing.demand.dto.RideRequestDto;

import java.util.List;

public interface RideRequestService {
    RideRequestDto createRequest(RideRequestDto dto);

    List<GroupedRequestDTO> getGroupedDemandsForDrivers();
    List<RideRequestDetailedDto> searchManualClusterRequests(
            String source, String destination, String preferredVehicle, String timeSlot);

}