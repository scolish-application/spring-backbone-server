package com.github.simaodiazz.schola.backbone.server;

import com.github.simaodiazz.schola.backbone.server.router.controller.UserController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.UserRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.UserResponse;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserDataService userDataService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserRequest testUserRequest;

    @BeforeEach
    void setUp() {
        // Create test data
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encoded_password");
        testUser.setAuthorities(authorities);

        testUserRequest = new UserRequest(
                "testuser",
                "password123",
                List.of("ROLE_USER", "ROLE_ADMIN")
        );
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        when(userDataService.findAll()).thenReturn(List.of(testUser));

        // Act
        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testUser.getId(), response.getBody().get(0).id());
        assertEquals(testUser.getUsername(), response.getBody().get(0).username());
        assertEquals(2, response.getBody().get(0).authorities().size());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userDataService.id(1L)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.getId(), response.getBody().id());
        assertEquals(testUser.getUsername(), response.getBody().username());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        when(userDataService.id(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<UserResponse> response = userController.getUserById(99L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userDataService.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        ResponseEntity<UserResponse> response = userController.createUser(testUserRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals(testUserRequest.username(), response.getBody().username());
        verify(passwordEncoder).encode(testUserRequest.password());
        verify(userDataService).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateAndReturnSuccess() {
        // Arrange
        when(userDataService.id(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("new_encoded_password");
        when(userDataService.save(any(User.class))).thenReturn(testUser);

        UserRequest updateRequest = new UserRequest("updated_username", "new_password", List.of("ROLE_USER"));

        // Act
        ResponseEntity<String> response = userController.updateUser(1L, updateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated successfully", response.getBody());
        verify(userDataService).save(argThat(user ->
                "updated_username".equals(user.getUsername()) &&
                        "new_encoded_password".equals(user.getPassword())
        ));
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        when(userDataService.id(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = userController.updateUser(99L, testUserRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userDataService, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteAndReturnSuccess() {
        // Arrange
        when(userDataService.id(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userDataService).delete(1L);

        // Act
        ResponseEntity<String> response = userController.deleteUser(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        verify(userDataService).delete(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        when(userDataService.id(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = userController.deleteUser(99L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userDataService, never()).delete(anyLong());
    }
}