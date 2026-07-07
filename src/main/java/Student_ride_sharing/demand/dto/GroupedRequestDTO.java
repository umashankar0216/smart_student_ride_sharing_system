package Student_ride_sharing.demand.dto;

import Student_ride_sharing.demand.entity.VehicleType;

public interface GroupedRequestDTO {
    String getSource();
    String getDestination();
    VehicleType getPreferredVehicle();
    String getTimeSlot(); // Combined rounded time block (e.g., "2026-06-20 17:30")
    Long getStudentCount();
}