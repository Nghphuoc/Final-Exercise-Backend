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
    @Email(message = "Email không đúng định dạng")
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
    @Size(min = 10, max = 10)
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

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public @NotBlank @Size(min = 10, max = 10) String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank @Size(min = 10, max = 10) String phone) {
        this.phone = phone;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public @NotBlank @Size(min = 3, max = 50) String getFullName() {
        return username;
    }

    public SignupRequest(String fullName, String email, Set<String> role, String password, String phone) {
        this.username = fullName;
        this.email = email;
        this.role = role;
        this.password = password;
        this.phone = phone;
    }

    public SignupRequest(String username, String email, Set<String> role, String password, String phone, String lastname) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
        this.phone = phone;
        this.lastname = lastname;
    }
    public SignupRequest() {

    }

}