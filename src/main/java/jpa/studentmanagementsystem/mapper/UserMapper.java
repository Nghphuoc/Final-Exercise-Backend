package jpa.studentmanagementsystem.mapper;

import jpa.studentmanagementsystem.dto.UserDto;
import jpa.studentmanagementsystem.entity.User;

public class UserMapper {

    public static User mapUserDto(UserDto userDto){
        return new User(
                userDto.getUsername(),
                userDto.getPassword(),
                userDto.getLastname(),
                userDto.getEmail(),
                userDto.getPhoneNumber(),
                userDto.getRole()
        );
    }

    public static UserDto mapUser(User user){
        return new UserDto(
                user.getLastname(),
                user.getPassword(),
                user.getLastname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }
}
