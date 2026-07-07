package Student_ride_sharing.demand.repository;

import Student_ride_sharing.demand.dto.GroupedRequestDTO;
import Student_ride_sharing.demand.entity.RequestStatus;
import Student_ride_sharing.demand.entity.Ride;
import Student_ride_sharing.demand.entity.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    @Query(value = "SELECT r.* FROM rides r " +
            "WHERE r.source = :source " +
            "AND r.destination = :destination " +
            "AND (:vehicleType = 'ANY' OR r.vehicle_type = :vehicleType) " +
            "AND r.total_seats > r.occupied_seats " +
            "AND CAST(r.departure_time AS DATE) = CAST(:preferredTime AS DATE) " + // 🟢 FIXED: Filters cleanly by day
            "ORDER BY r.departure_time ASC", nativeQuery = true)
    List<Ride> searchAvailableRides(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("vehicleType") String vehicleType,
            @Param("preferredTime") LocalDateTime preferredTime
    );


    @Query("SELECT r FROM Ride r WHERE r.driver.username = :username")
    List<Ride> findByDriverUsername(@Param("username") String username);
}