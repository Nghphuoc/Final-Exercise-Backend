package jpa.studentmanagementsystem.service.serviceImpl;

import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.exception.DuplicationException;
import jpa.studentmanagementsystem.repository.UserRepository;
import jpa.studentmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUserName(String studentName) {
        return userRepository.findByUsername(studentName)
                .orElseThrow(()->new RuntimeException("cannot find: `"+studentName));
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteByUserName(String userName) {
        userRepository.findByUsername(userName)
                .orElseThrow(()->new RuntimeException("cannot found: `"+userName));
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
                    throw new DuplicationException("Email number is already in use!: " + student.getEmail());
                }
                existingUser.setEmail(student.getEmail());
            }
            // check phone
            if(student.getPhoneNumber().matches("^\\+84\\d{9}$")){
                if (!student.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
                    if (userRepository.existsByPhoneNumber(student.getPhoneNumber())) {
                        throw new DuplicationException("Phone number is already in use!: " + student.getPhoneNumber());
                    }
                    existingUser.setPhoneNumber(student.getPhoneNumber());
                }
            }else{
                throw new DuplicationException("Phone number is incorrect!: " + student.getPhoneNumber());
            }

            existingUser.setPassword(passwordEncoder.encode(student.getPassword()));
            existingUser.setLastname(student.getLastname());

            userRepository.save(existingUser);
        } catch (DuplicationException e) {
            // Xử lý nếu dữ liệu bị trùng (email hoặc phone)
            System.err.println("Lỗi trùng dữ liệu: " + e.getMessage());
            throw new RuntimeException("Update failed: " + e.getMessage());
        }
    }
}
