package Student_ride_sharing.demand.dto;

import Student_ride_sharing.demand.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;
    private BookingStatus status;

    private UserResponseDto user;
    private RideResponseDto ride;
}