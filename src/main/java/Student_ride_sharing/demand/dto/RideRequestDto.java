package Student_ride_sharing.demand.dto;

import Student_ride_sharing.demand.entity.VehicleType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RideRequestDto {
    private Long userId;
    private String source;
    private String destination;
    private LocalDateTime preferredTime;
    private VehicleType preferredVehicle;
    private BigDecimal price;
}