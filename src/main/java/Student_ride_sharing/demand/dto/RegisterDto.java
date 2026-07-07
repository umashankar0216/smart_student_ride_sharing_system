package Student_ride_sharing.demand.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class RegisterDto {
    private String userName;
    private  String name;
    private String email;
    private String password;
    private String role;
}