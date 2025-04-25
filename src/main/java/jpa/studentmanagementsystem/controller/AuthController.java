package jpa.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import jpa.studentmanagementsystem.configLogin.SignupRequest;
import jpa.studentmanagementsystem.dto.UserDto;
import jpa.studentmanagementsystem.entity.Role;
import jpa.studentmanagementsystem.mapper.UserMapper;
import jpa.studentmanagementsystem.repository.RoleRepository;
import jpa.studentmanagementsystem.repository.UserRepository;
import jpa.studentmanagementsystem.security.JwtUtils;
import jpa.studentmanagementsystem.configLogin.LoginRequest;
import jpa.studentmanagementsystem.configLogin.LoginResponse;
import jpa.studentmanagementsystem.service.UserService;
import jpa.studentmanagementsystem.variable.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/public/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Bad credentials");
            errorResponse.put("status", false);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Kiểm tra authentication principal có phải là UserDetails không
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Tạo JWT token
        String jwtToken = jwtUtils.generateTokenFromUsername(loginRequest);
        // Lấy danh sách quyền (roles)
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        // Tạo response
        LoginResponse response = new LoginResponse(loginRequest.getUsername(), roles, jwtToken);

        return ResponseEntity.ok(response);
    }

    // custom user because so many field null
    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUsers(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        if(userRepository.existsByPhoneNumber(signUpRequest.getPhone()) && !signUpRequest.getPhone().matches("^\\+84\\d{10}$")){
            return ResponseEntity.badRequest().body("Error: Phone number is already in use!");
        }
        // Create new user's account
        UserDto user = new UserDto(signUpRequest.getUsername(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getLastname(),
                signUpRequest.getEmail(),
                signUpRequest.getPhone());
        Set<String> strRoles = signUpRequest.getRole();
        Role roles = null;
        if (strRoles == null || strRoles.isEmpty()) {
            roles = (roleRepository.findByRoleName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
        } else {
            for (String roleStr : strRoles) {
                if (roleStr.equalsIgnoreCase("admin")) {
                    roles = (roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                } else {
                    roles = (roleRepository.findByRoleName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                }
            }
        }
        user.setRole(roles);
        userService.createUser(UserMapper.mapUserDto(user));
        return ResponseEntity.ok("User registered successfully!");
    }
}
