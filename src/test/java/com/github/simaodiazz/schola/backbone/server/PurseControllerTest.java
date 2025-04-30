package com.github.simaodiazz.schola.backbone.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.simaodiazz.schola.backbone.server.economy.data.model.Purse;
import com.github.simaodiazz.schola.backbone.server.economy.data.model.Transaction;
import com.github.simaodiazz.schola.backbone.server.economy.data.model.TransactionMovement;
import com.github.simaodiazz.schola.backbone.server.economy.data.service.PurseService;
import com.github.simaodiazz.schola.backbone.server.router.controller.PurseController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PurseCreateRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PurseRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.TransactionCreateRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.TransactionRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.mapper.PurseMapper;
import com.github.simaodiazz.schola.backbone.server.router.controller.mapper.TransactionMapper;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PurseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PurseService purseService;

    @Mock
    private PurseMapper purseMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private PurseController purseController;

    private ObjectMapper objectMapper;
    private Purse purse;
    private PurseRequest purseRequest;
    private PurseCreateRequest purseCreateRequest;
    private Transaction transaction;
    private TransactionCreateRequest transactionCreateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(purseController)
                .build();
        objectMapper = new ObjectMapper();

        // Setup test data
        User user = new User();
        user.setId(1L);

        List<Transaction> transactions = new ArrayList<>();
        List<TransactionRequest> transactionRequests = new ArrayList<>();

        purse = new Purse();
        purse.setId(1L);
        purse.setAmount(1000.0);
        purse.setUser(user);
        purse.setTransactions(transactions);

        purseRequest = new PurseRequest(1L, 1000.0, 1L, transactionRequests);
        purseCreateRequest = new PurseCreateRequest(1000.0, 1L);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setLocation("Test Location");
        transaction.setCause("Test Cause");
        transaction.setAmount(100.0);
        transaction.setMovement(TransactionMovement.IN);

        transactionCreateRequest = new TransactionCreateRequest(
                "Test Location",
                "Test Cause",
                100.0,
                TransactionMovement.IN
        );
    }

    @Test
    void getPurseById_Success() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.of(purse));
        when(purseMapper.request(any(Purse.class))).thenReturn(purseRequest);

        mockMvc.perform(get("/api/purses/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.purse").value(1000.0))
                .andExpect(jsonPath("$.userId").value(1));

        verify(purseService, times(1)).getPurseById(1L);
        verify(purseMapper, times(1)).request(purse);
    }

    @Test
    void getPurseById_NotFound() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/purses/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(purseService, times(1)).getPurseById(999L);
    }

    @Test
    void getPurseByUserId_Success() throws Exception {
        when(purseService.getPurseByUserId(anyLong())).thenReturn(Optional.of(purse));
        when(purseMapper.request(any(Purse.class))).thenReturn(purseRequest);

        mockMvc.perform(get("/api/purses/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.purse").value(1000.0))
                .andExpect(jsonPath("$.userId").value(1));

        verify(purseService, times(1)).getPurseByUserId(1L);
        verify(purseMapper, times(1)).request(purse);
    }

    @Test
    void getPurseByUserId_NotFound() throws Exception {
        when(purseService.getPurseByUserId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/purses/user/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(purseService, times(1)).getPurseByUserId(999L);
    }

    @Test
    void createPurse_Success() throws Exception {
        when(purseMapper.createRequest(any(PurseCreateRequest.class))).thenReturn(purse);
        when(purseService.savePurse(any(Purse.class))).thenReturn(purse);
        when(purseMapper.request(any(Purse.class))).thenReturn(purseRequest);

        mockMvc.perform(post("/api/purses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purseCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.purse").value(1000.0))
                .andExpect(jsonPath("$.userId").value(1));

        verify(purseMapper, times(1)).createRequest(any(PurseCreateRequest.class));
        verify(purseService, times(1)).savePurse(purse);
        verify(purseMapper, times(1)).request(purse);
    }

    @Test
    void createPurse_BadRequest() throws Exception {
        when(purseMapper.createRequest(any(PurseCreateRequest.class))).thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(post("/api/purses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purseCreateRequest)))
                .andExpect(status().isBadRequest());

        verify(purseMapper, times(1)).createRequest(any(PurseCreateRequest.class));
        verify(purseService, never()).savePurse(any(Purse.class));
    }

    @Test
    void updatePurse_Success() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.of(purse));
        doNothing().when(purseMapper).updateEntityFromDTO(any(PurseCreateRequest.class), any(Purse.class));
        when(purseService.savePurse(any(Purse.class))).thenReturn(purse);
        when(purseMapper.request(any(Purse.class))).thenReturn(purseRequest);

        mockMvc.perform(put("/api/purses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purseCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.purse").value(1000.0))
                .andExpect(jsonPath("$.userId").value(1));

        verify(purseService, times(1)).getPurseById(1L);
        verify(purseMapper, times(1)).updateEntityFromDTO(any(PurseCreateRequest.class), eq(purse));
        verify(purseService, times(1)).savePurse(purse);
        verify(purseMapper, times(1)).request(purse);
    }

    @Test
    void updatePurse_NotFound() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/purses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purseCreateRequest)))
                .andExpect(status().isNotFound());

        verify(purseService, times(1)).getPurseById(999L);
        verify(purseMapper, never()).updateEntityFromDTO(any(PurseCreateRequest.class), any(Purse.class));
        verify(purseService, never()).savePurse(any(Purse.class));
    }

    @Test
    void updatePurse_BadRequest() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.of(purse));
        doThrow(new IllegalArgumentException("User not found")).when(purseMapper).updateEntityFromDTO(any(PurseCreateRequest.class), any(Purse.class));

        mockMvc.perform(put("/api/purses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purseCreateRequest)))
                .andExpect(status().isBadRequest());

        verify(purseService, times(1)).getPurseById(1L);
        verify(purseMapper, times(1)).updateEntityFromDTO(any(PurseCreateRequest.class), eq(purse));
        verify(purseService, never()).savePurse(any(Purse.class));
    }

    @Test
    void addTransaction_Success() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.of(purse));
        when(transactionMapper.createRequest(any(TransactionCreateRequest.class))).thenReturn(transaction);
        when(purseService.savePurse(any(Purse.class))).thenReturn(purse);
        when(purseMapper.request(any(Purse.class))).thenReturn(purseRequest);

        mockMvc.perform(post("/api/purses/1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.purse").value(1000.0))
                .andExpect(jsonPath("$.userId").value(1));

        verify(purseService, times(1)).getPurseById(1L);
        verify(transactionMapper, times(1)).createRequest(any(TransactionCreateRequest.class));
        verify(purseService, times(1)).savePurse(purse);
        verify(purseMapper, times(1)).request(purse);
    }

    @Test
    void addTransaction_PurseNotFound() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/purses/999/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionCreateRequest)))
                .andExpect(status().isNotFound());

        verify(purseService, times(1)).getPurseById(999L);
        verify(transactionMapper, never()).createRequest(any(TransactionCreateRequest.class));
        verify(purseService, never()).savePurse(any(Purse.class));
    }

    @Test
    void addTransaction_InsufficientFunds() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.of(purse));

        Transaction outTransaction = new Transaction();
        outTransaction.setId(2L);
        outTransaction.setLocation("Test Location");
        outTransaction.setCause("Test Cause");
        outTransaction.setAmount(2000.0); // More than purse balance
        outTransaction.setMovement(TransactionMovement.OUT);

        TransactionCreateRequest outRequest = new TransactionCreateRequest(
                "Test Location",
                "Test Cause",
                2000.0,
                TransactionMovement.OUT
        );

        when(transactionMapper.createRequest(any(TransactionCreateRequest.class))).thenReturn(outTransaction);

        mockMvc.perform(post("/api/purses/1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(outRequest)))
                .andExpect(status().isBadRequest());

        verify(purseService, times(1)).getPurseById(1L);
        verify(transactionMapper, times(1)).createRequest(any(TransactionCreateRequest.class));
        verify(purseService, never()).savePurse(any(Purse.class));
    }

    @Test
    void deletePurse_Success() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.of(purse));
        doNothing().when(purseService).deletePurse(anyLong());

        mockMvc.perform(delete("/api/purses/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(purseService, times(1)).getPurseById(1L);
        verify(purseService, times(1)).deletePurse(1L);
    }

    @Test
    void deletePurse_NotFound() throws Exception {
        when(purseService.getPurseById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/purses/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(purseService, times(1)).getPurseById(999L);
        verify(purseService, never()).deletePurse(anyLong());
    }
}