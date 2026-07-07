package Student_ride_sharing.demand.dto;

import Student_ride_sharing.demand.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDto {
    private Long id;
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private Integer totalSeats;
    private Integer occupiedSeats;
    private Integer availableSeats;
    private VehicleType vehicleType;
    private BigDecimal price;
}