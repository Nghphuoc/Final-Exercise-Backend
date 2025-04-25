package jpa.studentmanagementsystem.service;

import jpa.studentmanagementsystem.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserService {

    User findByUserName(String studentName);

    List<User> getAllUser();

    void deleteByUserName(String userName);

    void createUser(User student);

    void updateUser(String studentName, User student);
}
