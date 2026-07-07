package Student_ride_sharing.demand.repository;

import Student_ride_sharing.demand.entity.Booking;
import Student_ride_sharing.demand.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
   List<Booking> findByRideIdAndStatus(Long rideId, BookingStatus BookingStatus);
    @Query("SELECT b FROM Booking b JOIN FETCH b.ride r JOIN FETCH r.driver d WHERE b.student.username = :username AND b.status = :status")
    List<Booking> findStudentActiveBookings(
            @Param("username") String username,
            @Param("status") BookingStatus status

    );

}
