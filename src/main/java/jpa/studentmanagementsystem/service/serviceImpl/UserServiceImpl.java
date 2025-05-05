package jpa.studentmanagementsystem.service.serviceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jpa.studentmanagementsystem.dto.UserDto;
import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.exception.DuplicationException;
import jpa.studentmanagementsystem.mapper.UserMapper;
import jpa.studentmanagementsystem.repository.UserRepository;
import jpa.studentmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        userRepository.findByUsername(userName)
                .orElseThrow(()->new RuntimeException("cannot found: `" + userName));
        userRepository.deleteByUsername(userName);
    }

    @Override
    @Transactional
    public void createUser(User student) {
        userRepository.save(student);
    }

    @Override
    @Transactional
    public void updateUser(String studentName, User student) {
        try {
            User existingUser = userRepository.findByUsername(studentName)
                    .orElseThrow(() -> new RuntimeException("User not found : " + studentName));

            //check email
            if (!student.getEmail().equals(existingUser.getEmail())) {
                if (userRepository.existsByEmail(student.getEmail())) {
                    throw new DuplicationException("Email is already in use!: " + student.getEmail());
                }
                existingUser.setEmail(student.getEmail());
            }

            if(student.getPhoneNumber().matches("^\\+84\\d{9}$")){
                if (!student.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
                    if (userRepository.existsByPhoneNumber(student.getPhoneNumber())) {
                        throw new DuplicationException("Phone number is already in use!: " + student.getPhoneNumber());
                    }
                    existingUser.setPhoneNumber(student.getPhoneNumber());
                }
            } else{
                throw new DuplicationException("Phone number is incorrect!: " + student.getPhoneNumber());
            }

            if( student.getPassword() != null && !student.getPassword().isEmpty()){
                existingUser.setPassword(passwordEncoder.encode(student.getPassword()));
            }

            existingUser.setLastname(student.getLastname());
            userRepository.save(existingUser);

        } catch (DuplicationException e) {
            // Xử lý nếu dữ liệu bị trùng (email hoặc phone)
            System.err.println("Lỗi trùng dữ liệu: " + e.getMessage());
            throw new RuntimeException("Update failed: " + e.getMessage());
        }
    }

    @Override
    public List<UserDto> getUsersByCriteria(UserDto userDto) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM user WHERE 1=1 ");
        Map<String, Object> parameters = new HashMap<>();

        // check data input
        if (userDto.getUsername() != null && !userDto.getUsername().isEmpty()) {
            queryBuilder.append("AND user_name = :username ");
            parameters.put("username", userDto.getUsername());
        }
        if (userDto.getLastname() != null && !userDto.getLastname().isEmpty()) {
            queryBuilder.append("AND last_name = :lastname ");
            parameters.put("lastname", userDto.getLastname());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            queryBuilder.append("AND email = :email ");
            parameters.put("email", userDto.getEmail());
        }

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), User.class);
        parameters.forEach(query::setParameter);

        List<User> users = query.getResultList();
        return users.stream().map(UserMapper::mapUser).collect(Collectors.toList());
    }
}
