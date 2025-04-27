package com.github.simaodiazz.schola.backbone.server;

import com.github.simaodiazz.schola.backbone.server.mail.data.model.Courier;
import com.github.simaodiazz.schola.backbone.server.mail.data.model.Message;
import com.github.simaodiazz.schola.backbone.server.mail.data.service.CourierService;
import com.github.simaodiazz.schola.backbone.server.router.controller.CourierController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.MessageRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PageResponse;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierControllerTest {

    @Mock
    private CourierService courierService;

    @Mock
    private UserDataService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CourierController courierController;

    private User testUser;
    private Courier testCourier;
    private Message testMessage;
    private Page<Message> messagePage;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("testuser");
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testCourier = new Courier(testUser);
        testCourier.setId(1L);
        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setContent("Test message content");
        testMessage.setAuthor(testCourier);
        testMessage.setCouriers(new HashSet<>(Set.of(testCourier)));
        List<Message> messages = List.of(testMessage);
        messagePage = new PageImpl<>(messages, PageRequest.of(0, 10), 1);
        lenient().when(userService.username(anyString())).thenReturn(Optional.of(testUser));
    }

    @Test
    void getInbox_WhenCourierExists_ShouldReturnMessages() {
        when(courierService.user(any(User.class))).thenReturn(testCourier);
        when(courierService.received(anyLong(), any(Pageable.class))).thenReturn(messagePage);
        ResponseEntity<PageResponse<Message>> response = courierController.getInbox(0, 10, "id", "desc");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        assertEquals("Test message content", response.getBody().content().getFirst().getContent());
        verify(courierService).received(eq(1L), any(Pageable.class));
    }

    @Test
    void getInbox_WhenCourierDoesNotExist_ShouldReturnNotFound() {
        when(courierService.user(any(User.class))).thenReturn(null);
        ResponseEntity<PageResponse<Message>> response = courierController.getInbox(0, 10, "id", "desc");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getSentMessages_WhenCourierExists_ShouldReturnMessages() {
        when(courierService.user(any(User.class))).thenReturn(testCourier);
        when(courierService.sent(anyLong(), any(Pageable.class))).thenReturn(messagePage);
        ResponseEntity<PageResponse<Message>> response = courierController.getSentMessages(0, 10, "id", "desc");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        verify(courierService).sent(eq(1L), any(Pageable.class));
    }

    @Test
    void getSentMessages_WhenCourierDoesNotExist_ShouldReturnNotFound() {
        when(courierService.user(any(User.class))).thenReturn(null);
        ResponseEntity<PageResponse<Message>> response = courierController.getSentMessages(0, 10, "id", "desc");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getMessage_WhenAuthorized_ShouldReturnMessage() {
        when(courierService.message(anyLong())).thenReturn(testMessage);
        when(courierService.user(any(User.class))).thenReturn(testCourier);
        ResponseEntity<Message> response = courierController.getMessage(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test message content", response.getBody().getContent());
    }

    @Test
    void getMessage_WhenNotAuthorized_ShouldReturnForbidden() {
        Courier otherCourier = new Courier();
        otherCourier.setId(2L);
        Message unauthorizedMessage = new Message();
        unauthorizedMessage.setId(2L);
        unauthorizedMessage.setContent("Not for you");
        unauthorizedMessage.setAuthor(otherCourier);
        unauthorizedMessage.setCouriers(new HashSet<>(Set.of(otherCourier)));
        when(courierService.message(anyLong())).thenReturn(unauthorizedMessage);
        when(courierService.user(any(User.class))).thenReturn(testCourier);
        ResponseEntity<Message> response = courierController.getMessage(2L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getMessage_WhenMessageNotFound_ShouldReturnNotFound() {
        when(courierService.message(anyLong())).thenReturn(null);
        ResponseEntity<Message> response = courierController.getMessage(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void sendMessage_Success() {
        when(courierService.user(any(User.class))).thenReturn(testCourier);
        User targetUser = new User();
        targetUser.setId(2L);
        Set<Long> targetIds = Set.of(2L);
        when(userService.id(anyLong())).thenReturn(Optional.of(targetUser));
        MessageRequest request = new MessageRequest("Test message", testCourier.getId(), targetIds);
        when(courierService.sendMessage(anyString(), any(Courier.class), anySet()))
                .thenReturn(testMessage);
        ResponseEntity<Message> response = courierController.sendMessage(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(courierService).sendMessage(eq("Test message"), eq(testCourier), anySet());
    }

    @Test
    void sendMessage_WhenCourierDoesNotExist_CreatesNewCourier() {
        when(courierService.user(any(User.class))).thenReturn(null).thenReturn(testCourier);
        when(courierService.save(any(User.class))).thenReturn(testCourier);
        User targetUser = new User();
        targetUser.setId(2L);
        Set<Long> targetIds = Set.of(2L);
        when(userService.id(anyLong())).thenReturn(Optional.of(targetUser));
        MessageRequest request = new MessageRequest("Test message", testCourier.getId(), targetIds);
        when(courierService.sendMessage(anyString(), any(Courier.class), anySet()))
                .thenReturn(testMessage);
        ResponseEntity<Message> response = courierController.sendMessage(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(courierService).save(testUser);
    }

    @Test
    void sendMessage_WhenTargetsAreEmpty_ReturnsBadRequest() {
        when(courierService.user(any(User.class))).thenReturn(testCourier);
        Set<Long> emptyTargets = Set.of();
        MessageRequest request = new MessageRequest("Test message", testCourier.getId(), emptyTargets);
        ResponseEntity<Message> response = courierController.sendMessage(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoMoreInteractions(courierService);
    }

    @Test
    void deleteMessage_WhenAuthorized_ShouldDelete() {
        when(courierService.message(anyLong())).thenReturn(testMessage);
        when(courierService.user(any(User.class))).thenReturn(testCourier);
        doNothing().when(courierService).deleteMessage(anyLong());
        ResponseEntity<Void> response = courierController.deleteMessage(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(courierService).deleteMessage(1L);
    }

    @Test
    void deleteMessage_WhenNotAuthorized_ShouldReturnForbidden() {
        Courier otherCourier = new Courier();
        otherCourier.setId(2L);
        Message unauthorizedMessage = new Message();
        unauthorizedMessage.setId(2L);
        unauthorizedMessage.setAuthor(otherCourier);
        when(courierService.message(anyLong())).thenReturn(unauthorizedMessage);
        when(courierService.user(any(User.class))).thenReturn(testCourier);
        ResponseEntity<Void> response = courierController.deleteMessage(2L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(courierService, never()).deleteMessage(anyLong());
    }

    @Test
    void searchMessages_ShouldFilterByAuthorAndRecipient() {
        when(courierService.user(any(User.class))).thenReturn(testCourier);
        when(courierService.searchMessages(anyString(), any(Pageable.class))).thenReturn(messagePage);
        ResponseEntity<PageResponse<Message>> response = courierController.searchMessages(
                "test", 0, 10, "id", "desc");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        assertEquals("Test message content", response.getBody().content().getFirst().getContent());
    }

    @Test
    void searchMessages_WhenCourierDoesNotExist_ShouldReturnNotFound() {
        when(courierService.user(any(User.class))).thenReturn(null);
        ResponseEntity<PageResponse<Message>> response = courierController.searchMessages(
                "test", 0, 10, "id", "desc");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}