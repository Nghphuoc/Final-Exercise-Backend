package jpa.studentmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpa.studentmanagementsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;

    @JsonIgnore
    private String password;

    private String lastname;

    private String email;

    private String phoneNumber;

    private Role role;

    public UserDto(String userName, String password, String lastName, String email, String phoneNumber) {
        this.username = userName;
        this.password = password;
        this.lastname = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}
