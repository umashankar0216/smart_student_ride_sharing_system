package Student_ride_sharing.demand.dto;

import Student_ride_sharing.demand.entity.VehicleType;
import com.fasterxml.jackson.annotation.JsonFormat; // 🟢 IMPORT THIS
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateRideFromDemandDto {
    private List<String> selectedUsernames;
    private Long existingRideId;
    private String source;
    private String destination;
    private VehicleType vehicleType;

    // 🟢 FIXED: This tells Jackson to cleanly convert "2026-06-21 08:00" into a Java LocalDateTime
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime preferredTime;

    private int totalSeats;
    private BigDecimal price;
}