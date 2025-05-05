package jpa.studentmanagementsystem.service;

import jpa.studentmanagementsystem.dto.UserDto;
import jpa.studentmanagementsystem.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserService {

    UserDto findByUserName(String studentName);

    List<UserDto> getAllUser();

    void deleteByUserName(String userName);

    void createUser(User student);

    void updateUser(String studentName, User student);

    List<UserDto> getUsersByCriteria(UserDto userDto);
}
