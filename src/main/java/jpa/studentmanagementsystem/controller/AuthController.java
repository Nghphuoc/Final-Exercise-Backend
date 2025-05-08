package jpa.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import jpa.studentmanagementsystem.configLogin.request.SignupRequest;
import jpa.studentmanagementsystem.dto.UserDto;
import jpa.studentmanagementsystem.entity.Role;
import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.exception.ErrorResponse;
import jpa.studentmanagementsystem.mapper.UserMapper;
import jpa.studentmanagementsystem.repository.RoleRepository;
import jpa.studentmanagementsystem.repository.UserRepository;
import jpa.studentmanagementsystem.security.JwtUtils;
import jpa.studentmanagementsystem.configLogin.request.LoginRequest;
import jpa.studentmanagementsystem.configLogin.request.LoginResponse;
import jpa.studentmanagementsystem.service.RoleService;
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
    private RoleService roleService;

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
            ErrorResponse errorResponse = new ErrorResponse("Login fail", "User or Password is incorrect");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Kiểm tra authentication principal có phải là UserDetails không
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Tạo JWT token
        String jwtToken = jwtUtils.generateTokenFromUsername(loginRequest.getUsername());
        // Lấy danh sách quyền (roles)
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        // Tạo response
        LoginResponse response = new LoginResponse(loginRequest.getUsername(), roles, jwtToken);

        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    // custom user because so many field null
    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUsers(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            validateSignUpRequest(signUpRequest);
            UserDto user = buildUserDto(signUpRequest);
            //set role
            Role role = roleService.resolveUserRole(signUpRequest.getRole());
            user.setRole(role);

            userService.createUser(UserMapper.mapUserDto(user));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ErrorResponse("Success", "Create user successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Error", e.getMessage()));
        }
    }

    @PutMapping("/public/forgot-password")
    public ResponseEntity<?> setNewPassword(@RequestBody User user) {
        if(userService.forgotPassword(user.getUsername(), user.getEmail(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new ErrorResponse("Success", "Password changed successfully!"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Error", "Invalid username or email!"));
    }

    private void validateSignUpRequest(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        if (!request.getPhone().matches("^0[0-9]{9}$")) {
            throw new RuntimeException("Phone format is incorrect!");
        }

        if (userRepository.existsByPhoneNumber(request.getPhone())) {
            throw new RuntimeException("Phone number is already in use!");
        }
    }

    private UserDto buildUserDto(SignupRequest request) {
        return new UserDto(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getLastname(),
                request.getEmail(),
                request.getPhone(),
                (Role) request.getRole()
        );
    }

}
