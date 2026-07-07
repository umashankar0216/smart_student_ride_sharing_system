package Student_ride_sharing.demand.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String name;
    private String email;
    private String password;
    private String roleName; // e.g., "ROLE_STUDENT" or "ROLE_DRIVER"
}