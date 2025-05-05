package jpa.studentmanagementsystem;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jpa.studentmanagementsystem.dto.UserDto;
import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.repository.UserRepository;
import jpa.studentmanagementsystem.service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

public class TestUserServiceImpl {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPhoneNumber("+84987654321");
        user.setPassword("pass123");
        user.setLastname("Doe");
    }

    @Test
    void testFindByUserName_success() {
        when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        UserDto result = userService.findByUserName(Mockito.anyString());
        //assertEquals("john", result.getUsername());
    }

    @Test
    void testFindByUserName_notFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.findByUserName("unknown"));
    }

    @Test
    void testGetAllUser() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> users = userService.getAllUser();
        assertEquals(1, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void testDeleteByUserName_success() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteByUsername("john");
        userService.deleteByUserName("john");
        verify(userRepository).deleteByUsername("john");
    }

    @Test
    void testDeleteByUserName_notFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.deleteByUserName("unknown"));
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(user)).thenReturn(user);
        userService.createUser(user);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_success() {
        User updatedUser = new User();
        updatedUser.setEmail("new@example.com");
        updatedUser.setPhoneNumber("+84912345678");
        updatedUser.setPassword("newpass");
        updatedUser.setLastname("Smith");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("+84912345678")).thenReturn(false);
        when(passwordEncoder.encode("newpass")).thenReturn("encoded_pass");

        userService.updateUser("john", updatedUser);

        verify(userRepository).save(any(User.class));
        assertEquals("new@example.com", user.getEmail());
        assertEquals("+84912345678", user.getPhoneNumber());
        assertEquals("encoded_pass", user.getPassword());
        assertEquals("Smith", user.getLastname());
    }

    @Test
    void testUpdateUser_duplicateEmail() {
        User updatedUser = new User();
        updatedUser.setEmail("duplicate@example.com");
        updatedUser.setPhoneNumber("+84912345678");
        updatedUser.setPassword("pass");
        updatedUser.setLastname("Smith");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true); // giả định có email này rùi

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.updateUser("john", updatedUser);
        });

        assertTrue(thrown.getMessage().contains("Email is already in use!: duplicate@example.com"));
    }


    @Test
    void testUpdateUser_invalidPhone() {
        User updatedUser = new User();
        updatedUser.setEmail("valid@example.com");
        updatedUser.setPhoneNumber("123456789"); // invalid
        updatedUser.setPassword("pass");
        updatedUser.setLastname("Smith");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.updateUser("john", updatedUser);
        });

        assertTrue(thrown.getMessage().contains("Phone number is incorrect"));
    }

    @Test
    void testGetUsersByCriteria(){
        UserDto requestDto = new UserDto();
        requestDto.setEmail("kim@example.com");

        User mockUser = new User();
        mockUser.setUsername("kimjayoung");
        mockUser.setEmail("kim@example.com");

        // Giả lập Query trả kết quả
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString(), eq(User.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("email"), eq("kim@example.com"))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(List.of(mockUser));

        // When
        List<UserDto> result = userService.getUsersByCriteria(requestDto);

        // Then
        assertEquals(1, result.size());
        assertEquals("kimjayoung", result.get(0).getUsername());
        assertEquals("kim@example.com", result.get(0).getEmail());
    }
}
