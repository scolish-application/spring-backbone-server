package com.github.simaodiazz.schola.backbone.server;

import com.github.simaodiazz.schola.backbone.server.router.controller.AuthenticationController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.AuthenticationRequest;
import com.github.simaodiazz.schola.backbone.server.router.event.AuthenticationRegisterRouteInvokeEvent;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private UserDataService userDataService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController(publisher, userDataService, authenticationProvider);
    }

    @Test
    void testLoginSuccess() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "password123");
        doNothing().when(httpServletRequest).login(anyString(), anyString());
        ResponseEntity<String> response = authenticationController.login(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful.", response.getBody());
        verify(httpServletRequest).login("testuser", "password123");
    }

    @Test
    void testRegisterSuccess() {
        AuthenticationRequest request = new AuthenticationRequest("newuser", "password123");
        doNothing().when(userDataService).save(any(User.class));
        ResponseEntity<String> response = authenticationController.register(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account created, please login to continue.", response.getBody());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDataService).save(userCaptor.capture());
        ArgumentCaptor<AuthenticationRegisterRouteInvokeEvent> eventCaptor =
                ArgumentCaptor.forClass(AuthenticationRegisterRouteInvokeEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());
        assertSame(userCaptor.getValue(), eventCaptor.getValue().user());
    }

    @Test
    void testRegisterFailure() {
        AuthenticationRequest request = new AuthenticationRequest("existing user", "password123");
        doThrow(new RuntimeException("Username already exists")).when(userDataService).save(any(User.class));
        ResponseEntity<String> response = authenticationController.register(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
        verify(userDataService).save(any(User.class));
        verify(publisher, never()).publishEvent(any());
    }
}