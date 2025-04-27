package com.github.simaodiazz.schola.backbone.server;

import com.github.simaodiazz.schola.backbone.server.router.controller.AuthenticationController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.AuthenticationRequest;
import com.github.simaodiazz.schola.backbone.server.router.event.AuthenticationRegisterRouteInvokeEvent;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private UserDataService userDataService;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationController authenticationController;

    private AuthenticationRequest validAuthRequest;

    @BeforeEach
    void setUp() {
        validAuthRequest = new AuthenticationRequest("testuser", "password123");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void login_ShouldReturnOk_WhenCredentialsAreValid() {
        // Arrange
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Act
        ResponseEntity<String> response = authenticationController.login(validAuthRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful.", response.getBody());
        verify(securityContext).setAuthentication(authentication);
    }

    @Test
    void login_ShouldReturnBadRequest_WhenAuthenticationFails() {
        // Arrange
        String errorMessage = "Bad credentials";
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException(errorMessage));

        // Act
        ResponseEntity<String> response = authenticationController.login(validAuthRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void register_ShouldReturnOk_WhenRegistrationIsSuccessful() {
        // Arrange
        User mockUser = mock(User.class);
        ArgumentCaptor<AuthenticationRegisterRouteInvokeEvent> eventCaptor =
                ArgumentCaptor.forClass(AuthenticationRegisterRouteInvokeEvent.class);

        // Act
        ResponseEntity<String> response = authenticationController.register(validAuthRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account created, please login to continue.", response.getBody());
        verify(userDataService).save(any(User.class));
        verify(publisher).publishEvent(eventCaptor.capture());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenRegistrationFails() {
        // Arrange
        String errorMessage = "Username already exists";
        doThrow(new RuntimeException(errorMessage)).when(userDataService).save(any(User.class));

        // Act
        ResponseEntity<String> response = authenticationController.register(validAuthRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }
}