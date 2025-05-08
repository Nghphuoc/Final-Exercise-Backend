package jpa.studentmanagementsystem.service.serviceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jpa.studentmanagementsystem.dto.UserDto;
import jpa.studentmanagementsystem.entity.Role;
import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.exception.DuplicationException;
import jpa.studentmanagementsystem.mapper.UserMapper;
import jpa.studentmanagementsystem.repository.UserRepository;
import jpa.studentmanagementsystem.service.UserService;
import jpa.studentmanagementsystem.variable.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class title
 *
 * @author
 * @version
 * @function_id
 * @screen_id
 * @since 1.0
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserDto findByUserName(String studentName) {
        User user = userRepository.findByUsername(studentName)
                .orElseThrow(()->new RuntimeException("cannot find: `" + studentName));
        return UserMapper.mapUser(user);
    }

    @Override
    public List<UserDto> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper :: mapUser).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByUserName(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(()->new RuntimeException("cannot found: `" + userName));
        userRepository.deleteByUsername(userName);
    }

    @Override
    @Transactional
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(String studentName, User student) {
        try {
            User existingUser = getExistingUser(studentName);
            validateAndUpdateEmail(existingUser, student.getEmail());
            validateAndUpdatePhoneNumber(existingUser, student.getPhoneNumber());
            updatePasswordIfPresent(existingUser, student.getPassword());
            existingUser.setLastname(student.getLastname());

            userRepository.save(existingUser);

        } catch (DuplicationException e) {
            System.err.println("Lỗi trùng dữ liệu: " + e.getMessage());
            throw new RuntimeException("Update failed: " + e.getMessage());
        }
    }

    @Override
    public List<UserDto> getUsersByCriteria(UserDto userDto) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM user");
        Map<String, Object> parameters = new HashMap<>();
        List<String> conditions = new ArrayList<>();

        if (userDto.getUsername() != null && !userDto.getUsername().isEmpty()) {
            conditions.add("user_name = :username");
            parameters.put("username", userDto.getUsername());
        }
        if (userDto.getLastname() != null && !userDto.getLastname().isEmpty()) {
            conditions.add("last_name = :lastname");
            parameters.put("lastname", userDto.getLastname());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            conditions.add("email = :email");
            parameters.put("email", userDto.getEmail());
        }

        if (!conditions.isEmpty()) {
            queryBuilder.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), User.class);
        parameters.forEach(query::setParameter);

        List<User> users = query.getResultList();
        return users.stream().map(UserMapper::mapUser).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean forgotPassword(String username, String email, String password) {
        User user = getExistingUser(username);
        if(user.getEmail().equals(email)){
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User registerOAuth2User(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(email); // Hoặc tùy chỉnh
        user.setLastname(name);
        //user.setRole(RoleName.ROLE_USER); // Gán quyền mặc định
        return userRepository.save(user);
    }

    private User getExistingUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    private void validateAndUpdateEmail(User existingUser, String newEmail) throws DuplicationException {
        if (!newEmail.equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new DuplicationException("Email is already in use!: " + newEmail);
            }
            existingUser.setEmail(newEmail);
        }
    }

    private void validateAndUpdatePhoneNumber(User existingUser, String newPhone) throws DuplicationException {
        if (!newPhone.matches("^0[0-9]{9}$")) {
            throw new DuplicationException("Phone number is incorrect!: " + newPhone);
        }

        if (!newPhone.equals(existingUser.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(newPhone)) {
                throw new DuplicationException("Phone number is already in use!: " + newPhone);
            }
            existingUser.setPhoneNumber(newPhone);
        }
    }

    private void updatePasswordIfPresent(User existingUser, String newPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
        }
    }

}
