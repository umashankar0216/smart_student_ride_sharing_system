package Student_ride_sharing.demand.service.Impl;

import Student_ride_sharing.demand.dto.LoginDto;
import Student_ride_sharing.demand.dto.RegisterDto;
import Student_ride_sharing.demand.entity.Roles;
import Student_ride_sharing.demand.entity.User;
import Student_ride_sharing.demand.repository.RoleRepository;
import Student_ride_sharing.demand.repository.UserRepository;
import Student_ride_sharing.demand.security.JwtTokenProvider;
import Student_ride_sharing.demand.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;


//
//        @Override
//        public String register(RegisterDto dto) {
//            // Enforce uniqueness constraints
//            if (userRepository.existsByUsername(dto.getUserName())) {
//                throw new RuntimeException("Username is already taken!");
//            }
//            if (userRepository.existsByEmail(dto.getEmail())) {
//                throw new RuntimeException("Email is already registered!");
//            }
//
//            User user = new User();
//            user.setName(dto.getName());
//            user.setUsername(dto.getUserName());
//            user.setEmail(dto.getEmail());
//            user.setPassword(passwordEncoder.encode(dto.getPassword())); // Safely encrypt password string
//
//            // 🟢 Dynamic Role Assignment: Evaluates if registering as a DRIVER or passenger
//            Set<Roles> roles = new HashSet<>();
//            if (dto.getRole() != null && dto.getRole().equalsIgnoreCase("DRIVER")) {
//                Roles driverRole = roleRepository.findByName("ROLE_DRIVER")
//                        .orElseThrow(() -> new RuntimeException("ROLE_DRIVER not initialized in database."));
//                roles.add(driverRole);
//            } else {
//                // Default to STUDENT role
//                Roles studentRole = roleRepository.findByName("ROLE_STUDENT")
//                        .orElseThrow(() -> new RuntimeException("ROLE_STUDENT not initialized in database."));
//                roles.add(studentRole);
//            }
//            user.setRoles(roles);
//
//            User savedUser = userRepository.save(user);
//            return "the user registered succefully";
//        }


//    @Override
//    public String register(RegisterDto registerDto) {
//
//
//
//        User user = new User();
//        user.setUsername(registerDto.getUserName());
//        user.setName(registerDto.getName());
//        user.setEmail(registerDto.getEmail());
//        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
//
//        Set<Roles> roles = new HashSet<>();
//
//        Roles userrole = roleRepository.findByName("ROLE_STUDENT");
//        roles.add(userrole);
//
//        user.setRoles(roles);
//        userRepository.save(user);
//
//
//
//
//        return " the user registered succefully";
//    }

        @Override
        public String register(RegisterDto dto) {
            // Enforce uniqueness constraints
            if (userRepository.existsByUsername(dto.getUserName())) {
                throw new RuntimeException("Username is already taken!");
            }
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email is already registered!");
            }

            User user = new User();
            user.setName(dto.getName());
            user.setUsername(dto.getUserName());
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword())); // Safely encrypt password string

            // 🟢 Dynamic Role Assignment: Evaluates if registering as a DRIVER or passenger
            Set<Roles> roles = new HashSet<>();
            if (dto.getRole() != null && dto.getRole().equalsIgnoreCase("DRIVER")) {
                Roles driverRole = roleRepository.findByName("ROLE_DRIVER");
                if (driverRole != null) {
                    roles.add(driverRole);
                } else {
                    throw new RuntimeException("ROLE_DRIVER not initialized in database.");
                }
            } else {
                // Default to STUDENT role
                Roles studentRole = roleRepository.findByName("ROLE_STUDENT");
                if (studentRole != null) {
                    roles.add(studentRole);
                } else {
                    throw new RuntimeException("ROLE_STUDENT not initialized in database.");
                }
            }
            user.setRoles(roles);

            User savedUser = userRepository.save(user);
            return "the user registered successfully";
        }

        @Override
        public String login(LoginDto loginDto) {


            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUserNameOrEmail(),
                    loginDto.getPassword()
            ));


            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);
            return token;

        }
    }
