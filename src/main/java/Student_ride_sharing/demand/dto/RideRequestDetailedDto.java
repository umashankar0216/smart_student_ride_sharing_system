package Student_ride_sharing.demand.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RideRequestDetailedDto {
    private Long id;
    private String studentUsername;
    private String studentName;
    private String source;
    private String destination;
    private LocalDateTime preferredTime;
    private String preferredVehicle;
    private String status;
    private BigDecimal price; }