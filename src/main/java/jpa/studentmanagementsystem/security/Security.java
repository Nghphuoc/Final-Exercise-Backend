package jpa.studentmanagementsystem.security;

import jpa.studentmanagementsystem.entity.Role;
import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.repository.RoleRepository;
import jpa.studentmanagementsystem.repository.UserRepository;
import jpa.studentmanagementsystem.variable.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true,jsr250Enabled = true,securedEnabled = true) // Authorize phan quyền  Authentication xác thực
public class Security {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable); // Vô hiệu hóa CSRF

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Cho phép OPTIONS
                .requestMatchers("/api/auth/public/**").permitAll() // Cho phép truy cập công khai
                .requestMatchers("/api/email/**").permitAll()
                .requestMatchers("/api/user/**").hasRole("ADMIN")
                // Bất kỳ request nào khác cần xác thực
                .anyRequest().authenticated()
        );
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(withDefaults());
//        http.oauth2Login(oauth2 -> oauth2
//               // .successHandler() // Xử lý sau khi login thành công → tạo JWT
//        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(AuthTokenFilter filter) { // private
        return filter;  //////////////////fix here new JwtUtils
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // encoder by interface passwordEncoder
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder ) {
//        return args -> {
//            Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
//                    .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));
//
//            Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
//                    .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));
//
//            if (!userRepository.existsByUsername("user")) {
//                User user1 = new User("user",passwordEncoder.encode("123456"),"nguyen","user@gmail.com","+84178261764");
//                user1.setRole(userRole);
//                userRepository.save(user1);
//            }
//
//            if (!userRepository.existsByUsername("admin")) {
//                User admin = new User("admin",passwordEncoder.encode("admin"),"nguyen","admin@gmail.com","+84178226414");
//                admin.setRole(adminRole);
//                userRepository.save(admin);
//            }
//        };
//    }
}
