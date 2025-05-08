package jpa.studentmanagementsystem.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.repository.UserRepository;
import jpa.studentmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Autowired
    public CustomOAuth2SuccessHandler(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // ✅ Nếu user chưa tồn tại trong hệ thống → tạo mới
//        User user = userService.findByEmail(email)
//                .orElseGet(() -> userService.registerOAuth2User(email, name));
//
//        // ✅ Tạo JWT
//        String token = jwtUtils.generateToken(user.getUsername());
//
//        // ✅ Trả về cho frontend qua redirect (nếu frontend là SPA)
//        String redirectUrl = "http://localhost:4200/oauth2-success?token=" + token;
//        response.sendRedirect(redirectUrl);

        // ❗ Nếu dùng full REST API không redirect, thì:
        // response.setContentType("application/json");
        // response.getWriter().write("{\"token\": \"" + token + "\"}");
    }
}
