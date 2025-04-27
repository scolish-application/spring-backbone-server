package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.mail.data.model.Courier;
import com.github.simaodiazz.schola.backbone.server.mail.data.model.Message;
import com.github.simaodiazz.schola.backbone.server.mail.data.service.CourierService;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.MessageRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PageResponse;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mail")
public class CourierController {

    private final CourierService courierService;
    private final UserDataService userService;

    @Autowired
    public CourierController(CourierService courierService, UserDataService userService) {
        this.courierService = courierService;
        this.userService = userService;
    }

    // Helper method to get the current authenticated user
    private User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String username = authentication.getName();
        return userService.username(username).orElseThrow(() ->
                new IllegalStateException("User not found"));
    }

    // Helper method to create a Pageable object from request parameters
    private @NotNull Pageable createPageable(int page, int size, String sortBy, @NotNull String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }

    @GetMapping("/inbox")
    public ResponseEntity<PageResponse<Message>> getInbox(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        final User user = getCurrentUser();
        final Courier courier = courierService.user(user);

        if (courier == null) {
            return ResponseEntity.notFound().build();
        }

        final long id = courier.getId();

        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Message> messagePage = courierService.received(id, pageable);

        PageResponse<Message> response = new PageResponse<>(
                messagePage.getContent(),
                messagePage.getNumber(),
                messagePage.getSize(),
                messagePage.getTotalElements(),
                messagePage.getTotalPages(),
                messagePage.isFirst(),
                messagePage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<PageResponse<Message>> getSentMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        final User user = getCurrentUser();
        final Courier courier = courierService.user(user);

        if (courier == null) {
            return ResponseEntity.notFound().build();
        }

        final long id = courier.getId();

        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Message> messagePage = courierService.sent(id, pageable);

        PageResponse<Message> response = new PageResponse<>(
                messagePage.getContent(),
                messagePage.getNumber(),
                messagePage.getSize(),
                messagePage.getTotalElements(),
                messagePage.getTotalPages(),
                messagePage.isFirst(),
                messagePage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable Long id) {
        Message message = courierService.message(id);
        if (message == null)
            return ResponseEntity.notFound().build();

        final User user = getCurrentUser();
        final Courier courier = courierService.user(user);

        if (courier == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isAuthor = message.getAuthor().getId() == courier.getId();
        boolean isRecipient = message.getCouriers().stream()
                .anyMatch(c -> c.getId() == courier.getId());

        if (!isAuthor && !isRecipient)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(message);
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody @NotNull MessageRequest request) {
        final User user = getCurrentUser();
        Courier courier = courierService.user(user);

        if (courier == null) {
            // Create a courier for the user if it doesn't exist
            courierService.save(user);
            courier = courierService.user(user);

            if (courier == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }
        }

        Set<User> targets = request.targets().stream()
                .map(userService::id)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        if (targets.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Message message = courierService.sendMessage(
                request.content(),
                courier,
                targets
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        Message message = courierService.message(id);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }

        final User user = getCurrentUser();
        final Courier courier = courierService.user(user);

        if (courier == null) {
            return ResponseEntity.notFound().build();
        }

        if (message.getAuthor().getId() == courier.getId()) {
            courierService.deleteMessage(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<Message>> searchMessages(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        final User user = getCurrentUser();
        final Courier courier = courierService.user(user);

        if (courier == null) {
            return ResponseEntity.notFound().build();
        }

        final long courierId = courier.getId();

        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Message> messagePage = courierService.searchMessages(keyword, pageable);

        List<Message> filteredContent = messagePage.getContent().stream()
                .filter(message -> {
                    boolean isAuthor = message.getAuthor().getId() == courierId;
                    boolean isRecipient = message.getCouriers().stream()
                            .anyMatch(c -> c.getId() == courierId);
                    return isAuthor || isRecipient;
                })
                .collect(Collectors.toList());

        // Note: This is a simplified approach. In a production environment,
        // filtering should be done at the database level for better performance.

        PageResponse<Message> response = new PageResponse<>(
                filteredContent,
                messagePage.getNumber(),
                messagePage.getSize(),
                filteredContent.size(), // More accurate count after filtering
                (int) Math.ceil((double) filteredContent.size() / messagePage.getSize()), // More accurate page count
                messagePage.isFirst(),
                messagePage.isLast()
        );

        return ResponseEntity.ok(response);
    }
}