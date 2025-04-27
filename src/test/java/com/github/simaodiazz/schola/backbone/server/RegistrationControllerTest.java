package com.github.simaodiazz.schola.backbone.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.Carte;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.CarteColor;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.Registration;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.RegistrationDirection;
import com.github.simaodiazz.schola.backbone.server.registry.data.service.CarteService;
import com.github.simaodiazz.schola.backbone.server.registry.data.service.RegistrationService;
import com.github.simaodiazz.schola.backbone.server.router.controller.RegistrationController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.CarteRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.RegistrationRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.mapper.CarteMapper;
import com.github.simaodiazz.schola.backbone.server.router.controller.mapper.RegistrationMapper;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private CarteService carteService;

    @Mock
    private RegistrationMapper registrationMapper;

    @Mock
    private CarteMapper carteMapper;

    @InjectMocks
    private RegistrationController registrationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper.findAndRegisterModules(); // For LocalDateTime serialization
    }

    @Test
    public void testCreateRegistration() throws Exception {
        RegistrationRequest registrationDTO = createTestRegistrationDTO();
        Registration registration = createTestRegistration();

        // Mock service and mapper calls
        when(registrationMapper.toEntity(any(RegistrationRequest.class))).thenReturn(registration);
        when(registrationService.saveRegistration(any(Registration.class))).thenReturn(registration);
        when(registrationMapper.toRequest(any(Registration.class))).thenReturn(registrationDTO);

        // Perform the request and validate
        mockMvc.perform(post("/api/v1/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(registrationDTO.getId()))
                .andExpect(jsonPath("$.direction").value(registrationDTO.getDirection().toString()));
    }

    @Test
    public void testGetRegistration() throws Exception {
        // Prepare test data
        Long id = 1L;
        RegistrationRequest registrationDTO = createTestRegistrationDTO();
        Registration registration = createTestRegistration();

        // Mock service and mapper calls
        when(registrationService.getRegistration(id)).thenReturn(Optional.of(registration));
        when(registrationMapper.toRequest(registration)).thenReturn(registrationDTO);

        // Perform the request and validate
        mockMvc.perform(get("/api/v1/registration/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registrationDTO.getId()))
                .andExpect(jsonPath("$.direction").value(registrationDTO.getDirection().toString()));
    }

    @Test
    public void testGetRegistrationNotFound() throws Exception {
        // Prepare test data
        Long id = 1L;

        // Mock service call to return empty
        when(registrationService.getRegistration(id)).thenReturn(Optional.empty());

        // Perform the request and validate
        mockMvc.perform(get("/api/v1/registration/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllRegistrations() throws Exception {
        // Prepare test data
        Pageable pageable = PageRequest.of(0, 10);
        List<Registration> registrations = List.of(createTestRegistration());
        Page<Registration> registrationPage = new PageImpl<>(registrations, pageable, registrations.size());

        RegistrationRequest registrationDTO = createTestRegistrationDTO();
        Page<RegistrationRequest> dtoPage = new PageImpl<>(Collections.singletonList(registrationDTO), pageable, 1);

        // Mock service and mapper calls
        when(registrationService.getAllRegistrations(any(Pageable.class))).thenReturn(registrationPage);
        when(registrationMapper.toRequest(any(Registration.class))).thenReturn(registrationDTO);

        // Perform the request and validate
        mockMvc.perform(get("/api/v1/registration")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(registrationDTO.getId()));
    }

    @Test
    public void testUpdateRegistration() throws Exception {
        // Prepare test data
        Long id = 1L;
        RegistrationRequest registrationDTO = createTestRegistrationDTO();
        Registration registration = createTestRegistration();

        // Mock service and mapper calls
        when(registrationService.existsById(id)).thenReturn(true);
        when(registrationMapper.toEntity(any(RegistrationRequest.class))).thenReturn(registration);
        when(registrationService.saveRegistration(any(Registration.class))).thenReturn(registration);
        when(registrationMapper.toRequest(any(Registration.class))).thenReturn(registrationDTO);

        // Perform the request and validate
        mockMvc.perform(put("/api/v1/registration/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registrationDTO.getId()));
    }

    @Test
    public void testUpdateRegistrationNotFound() throws Exception {
        // Prepare test data
        Long id = 1L;
        RegistrationRequest registrationDTO = createTestRegistrationDTO();

        // Mock service call to return false for exists check
        when(registrationService.existsById(id)).thenReturn(false);

        // Perform the request and validate
        mockMvc.perform(put("/api/v1/registration/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRegistration() throws Exception {
        // Prepare test data
        Long id = 1L;

        // Mock service calls
        when(registrationService.existsById(id)).thenReturn(true);
        doNothing().when(registrationService).deleteRegistration(id);

        // Perform the request and validate
        mockMvc.perform(delete("/api/v1/registration/{id}", id))
                .andExpect(status().isNoContent());

        // Verify delete was called
        verify(registrationService).deleteRegistration(id);
    }

    @Test
    public void testDeleteRegistrationNotFound() throws Exception {
        // Prepare test data
        Long id = 1L;

        // Mock service call to return false for exists check
        when(registrationService.existsById(id)).thenReturn(false);

        // Perform the request and validate
        mockMvc.perform(delete("/api/v1/registration/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCarte() throws Exception {
        // Prepare test data
        CarteRequest carteDTO = createTestCarteDTO();
        Carte carte = createTestCarte();

        // Mock service and mapper calls
        when(carteService.existsByCode(carteDTO.getCode())).thenReturn(false);
        when(carteMapper.toEntity(any(CarteRequest.class))).thenReturn(carte);
        when(carteService.saveCarte(any(Carte.class))).thenReturn(carte);
        when(carteMapper.toRequest(any(Carte.class))).thenReturn(carteDTO);

        // Perform the request and validate
        mockMvc.perform(post("/api/v1/registration/carte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(carteDTO.getId()))
                .andExpect(jsonPath("$.code").value(carteDTO.getCode()));
    }

    @Test
    public void testCreateCarteAlreadyExists() throws Exception {
        CarteRequest carteDTO = createTestCarteDTO();

        when(carteService.existsByCode(carteDTO.getCode())).thenReturn(true);

        mockMvc.perform(post("/api/v1/registration/carte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carteDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetCarte() throws Exception {
        String code = "ABC123";
        CarteRequest carteDTO = createTestCarteDTO();
        Carte carte = createTestCarte();

        // Mock service and mapper calls
        when(carteService.getCarte(code)).thenReturn(Optional.of(carte));
        when(carteMapper.toRequest(carte)).thenReturn(carteDTO);

        // Perform the request and validate
        mockMvc.perform(get("/api/v1/registration/carte/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carteDTO.getId()))
                .andExpect(jsonPath("$.code").value(carteDTO.getCode()));
    }

    @Test
    public void testGetCarteNotFound() throws Exception {
        String code = "ABC123";
        when(carteService.getCarte(code)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/registration/carte/{code}", code))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllCartes() throws Exception {
        // Prepare test data
        List<Carte> cartes = List.of(createTestCarte());
        List<CarteRequest> carteDTOs = Collections.singletonList(createTestCarteDTO());

        // Mock service and mapper calls
        when(carteService.getAllCartes()).thenReturn(cartes);
        when(carteMapper.toRequests(cartes)).thenReturn(carteDTOs);

        // Perform the request and validate
        mockMvc.perform(get("/api/v1/registration/carte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(carteDTOs.get(0).getId()));
    }

    @Test
    public void testUpdateCarte() throws Exception {
        // Prepare test data
        String code = "ABC123";
        CarteRequest carteDTO = createTestCarteDTO();
        Carte carte = createTestCarte();

        // Mock service and mapper calls
        when(carteService.existsByCode(code)).thenReturn(true);
        when(carteMapper.toEntity(any(CarteRequest.class))).thenReturn(carte);
        when(carteService.saveCarte(any(Carte.class))).thenReturn(carte);
        when(carteMapper.toRequest(any(Carte.class))).thenReturn(carteDTO);

        // Perform the request and validate
        mockMvc.perform(put("/api/v1/registration/carte/{code}", code)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carteDTO.getId()));
    }

    @Test
    public void testDeleteCarte() throws Exception {
        // Prepare test data
        String code = "ABC123";

        // Mock service calls
        when(carteService.existsByCode(code)).thenReturn(true);
        doNothing().when(carteService).deleteCarte(code);

        // Perform the request and validate
        mockMvc.perform(delete("/api/v1/registration/carte/{code}", code))
                .andExpect(status().isNoContent());

        // Verify delete was called
        verify(carteService).deleteCarte(code);
    }

    @Test
    public void testAssociateCarteToRegistration() throws Exception {
        // Prepare test data
        Long registrationId = 1L;
        String carteCode = "ABC123";

        Registration registration = createTestRegistration();
        Carte carte = createTestCarte();
        RegistrationRequest registrationDTO = createTestRegistrationDTO();

        // Mock service and mapper calls
        when(registrationService.getRegistration(registrationId)).thenReturn(Optional.of(registration));
        when(carteService.getCarte(carteCode)).thenReturn(Optional.of(carte));
        when(registrationService.saveRegistration(any(Registration.class))).thenReturn(registration);
        when(registrationMapper.toRequest(any(Registration.class))).thenReturn(registrationDTO);

        // Perform the request and validate
        mockMvc.perform(put("/api/v1/registration/{registrationId}/carte/{code}", registrationId, carteCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registrationDTO.getId()));
    }

    @Test
    public void testAssociateCarteToRegistrationNotFound() throws Exception {
        // Prepare test data
        Long registrationId = 1L;
        String carteCode = "ABC123";

        // Mock service call to return empty registration
        when(registrationService.getRegistration(registrationId)).thenReturn(Optional.empty());

        // Perform the request and validate
        mockMvc.perform(put("/api/v1/registration/{registrationId}/carte/{code}", registrationId, carteCode))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAssociateCarteToRegistrationCarteNotFound() throws Exception {
        // Prepare test data
        Long registrationId = 1L;
        String carteCode = "ABC123";
        Registration registration = createTestRegistration();

        // Mock service calls
        when(registrationService.getRegistration(registrationId)).thenReturn(Optional.of(registration));
        when(carteService.getCarte(carteCode)).thenReturn(Optional.empty());

        // Perform the request and validate
        mockMvc.perform(put("/api/v1/registration/{registrationId}/carte/{code}", registrationId, carteCode))
                .andExpect(status().isNotFound());
    }

    // Helper methods to create test data
    private Registration createTestRegistration() {
        Registration registration = new Registration();
        registration.setId(1L);
        registration.setDirection(RegistrationDirection.ENTRY);
        registration.setUser(createTestUser());
        registration.setCreated(LocalDateTime.now());
        return registration;
    }

    private RegistrationRequest createTestRegistrationDTO() {
        return RegistrationRequest.builder()
                .id(1L)
                .direction(RegistrationDirection.ENTRY)
                .userId(createTestUser().getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Carte createTestCarte() {
        Carte carte = new Carte();
        carte.setId(1L);
        carte.setCode("ABC123");
        carte.setColor(CarteColor.GREEN);
        carte.setUser(createTestUser());
        carte.setCreated(LocalDateTime.now());
        return carte;
    }

    private CarteRequest createTestCarteDTO() {
        return CarteRequest.builder()
                .id(1L)
                .code("ABC123")
                .color(CarteColor.GREEN)
                .userId(createTestUser().getId())
                .build();
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Test User");
        return user;
    }
}