package jpa.studentmanagementsystem.configLogin.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


public class SignupRequest {
    @NotBlank
    @Size(min = 5, max = 50)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Setter
    @Getter
    private Set<String> role;

    @NotBlank
    @Size(min = 5, max = 40)
    private String password;

    @Setter
    @Getter
    @NotBlank
    @Size(min = 12, max = 12)
    private String phone;

    @Setter
    @Getter
    private String lastname;

    public @NotBlank @Size(min = 5, max = 50) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(min = 5, max = 50) String username) {
        this.username = username;
    }

    public @NotBlank @Size(max = 50) @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Size(max = 50) @Email String email) {
        this.email = email;
    }

    public @NotBlank @Size(min = 5, max = 40) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 5, max = 40) String password) {
        this.password = password;
    }

    public SignupRequest(String fullName, String email, Set<String> role, String password, String phone) {
        this.username = fullName;
        this.email = email;
        this.role = role;
        this.password = password;
        this.phone = phone;
    }

    public @NotBlank @Size(min = 3, max = 50) String getFullName() {
        return username;
    }
}