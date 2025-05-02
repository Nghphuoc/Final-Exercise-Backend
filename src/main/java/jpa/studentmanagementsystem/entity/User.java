package jpa.studentmanagementsystem.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "userName",unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "lastName")
    private String lastname;

    @Column(name = "email", unique = true)
    @Email
    @NotBlank
    private String email;

    @Column(name = "phoneNumber", unique = true)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.MERGE)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

    public User(String userName, String password, String lastName, String email, String phoneNumber) {
        this.username = userName;
        this.password = password;
        this.lastname = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
