package Student_ride_sharing.demand.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ride_requests")
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime preferredTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    // 🟢 FIXED: Removed raw columnDefinition string to eliminate PostgreSQL syntax errors
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_vehicle", nullable = false)
    private VehicleType preferredVehicle = VehicleType.ANY;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 🟢 SYNCHRONIZED: Maps Java property 'price' to the database column cleanly
    @Column(name = "price", nullable = false)
    private BigDecimal price;
}