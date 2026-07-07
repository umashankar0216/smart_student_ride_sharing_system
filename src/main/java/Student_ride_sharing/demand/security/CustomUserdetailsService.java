package Student_ride_sharing.demand.security;


import Student_ride_sharing.demand.entity.User;
import Student_ride_sharing.demand.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserdetailsService implements UserDetailsService {

    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String usernameoremail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameoremail,usernameoremail).get();

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map((auth) -> new SimpleGrantedAuthority(auth.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(usernameoremail,user.getPassword(),authorities);
    }
}
