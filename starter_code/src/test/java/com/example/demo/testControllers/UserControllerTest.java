package com.example.demo.testControllers;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;


public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private PasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "passwordEncoder", passwordEncoder);
    }

    @Test
    public void testNotFoundUserId(){
        when(userRepository.findById(5l)).thenReturn(Optional.ofNullable(null));
        ResponseEntity<User> user = userController.findById(5l);

        Assertions.assertEquals(404, user.getStatusCodeValue());
    }

    @Test
    public void testFindUserByIdSuccess(){
        when(userRepository.findById(1l)).thenReturn(Optional.of(CartControllerTest.getUser()));
        ResponseEntity<User> user = userController.findById(1l);

        Assertions.assertEquals(200, user.getStatusCodeValue());
    }

    @Test
    public void testNotFoundUserName() {
        when(userRepository.findByUsername("username2")).thenReturn(null);
        ResponseEntity<User> user = userController.findByUserName("username2");

        Assertions.assertEquals(404, user.getStatusCodeValue());
    }

    @Test
    public void testFindUserByUserNameSuccess() {
        when(userRepository.findByUsername("usernameTest")).thenReturn(CartControllerTest.getUser());
        ResponseEntity<User> user = userController.findByUserName("usernameTest");

        Assertions.assertEquals(200, user.getStatusCodeValue());
    }

    @Test
    public void testCreateUserWithPasswordLessThan7Character() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("t");
        request.setConfirmPassword("t");
        ResponseEntity<?> response = userController.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }


    @Test
    public void testCreateUserWithConfirmPasswordNotMatch() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("passwordTest");
        request.setConfirmPassword("passwordTest2");
        ResponseEntity<?> response = userController.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testCreateUserSuccess() {
        when(passwordEncoder.encode("passwordTest")).thenReturn("hashedPasswordTest");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("usernameTest");
        request.setPassword("passwordTest");
        request.setConfirmPassword("passwordTest");
        ResponseEntity<User> response = (ResponseEntity<User>) userController.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        Assertions.assertNotNull(user);
        Assertions.assertEquals(0, user.getId());
        Assertions.assertEquals("usernameTest", user.getUsername());
        Assertions.assertEquals("hashedPasswordTest", user.getPassword());
    }
}
