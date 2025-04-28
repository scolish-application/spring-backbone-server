package com.github.simaodiazz.schola.backbone.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Classroom;
import com.github.simaodiazz.schola.backbone.server.classroom.data.service.ClassroomService;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Address;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Gender;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Guardian;
import com.github.simaodiazz.schola.backbone.server.entity.data.service.EstudianteService;
import com.github.simaodiazz.schola.backbone.server.router.controller.EstudianteController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EstudianteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EstudianteService estudianteService;

    @Mock
    private ClassroomService classroomService;

    @InjectMocks
    private EstudianteController estudianteController;

    private ObjectMapper objectMapper;
    private Estudiante estudiante;
    private EstudianteRequest estudianteRequest;
    private Classroom classroom;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(estudianteController)
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDate serialization

        // Setup classroom
        classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Turma A");

        // Setup estudiante
        estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setName("João Silva");
        estudiante.setNif("123456789");
        estudiante.setDateOfBirth(LocalDate.of(2000, 1, 1));
        estudiante.setPhone("987654321");
        estudiante.setGender(Gender.MALE);
        estudiante.setClassroom(classroom);
        estudiante.setSpecial(false);
        estudiante.setMedicalInformation("No allergies");

        // Setup address
        Address address = new Address();
        address.setId(1L);
        address.setStreet("Rua Principal");
        address.setCity("Lisboa");
        address.setPostalCode("1000-100");
        address.setCountry("Portugal");
        estudiante.setAddress(address);

        // Setup guardian
        Guardian guardian = new Guardian();
        guardian.setId(1L);
        guardian.setName("Maria Silva");
        guardian.setRelationship("Mãe");
        guardian.setNif("987654321");
        guardian.setPhone("123456789");
        guardian.setOccupation("Teacher");
        guardian.setWorkPhone("123123123");
        guardian.setPrimaryGuardian(true);
        guardian.setAddress(address);

        List<Guardian> guardians = new ArrayList<>();
        guardians.add(guardian);
        estudiante.setGuardians(guardians);

        // Setup request DTO
        AddressRequest addressDTO = new AddressRequest("Rua Principal", "Lisboa", "1000-100", "Portugal");

        List<GuardianRequest> guardianRequests = getGuardianRequests(addressDTO);

        estudianteRequest = new EstudianteRequest();
        estudianteRequest.setName("João Silva");
        estudianteRequest.setNif("123456789");
        estudianteRequest.setDateOfBirth(LocalDate.of(2000, 1, 1));
        estudianteRequest.setPhone("987654321");
        estudianteRequest.setGender(Gender.MALE);
        estudianteRequest.setClassroomId(1L);
        estudianteRequest.setSpecial(false);
        estudianteRequest.setMedicalInformation("No allergies");
        estudianteRequest.setAddress(addressDTO);
        estudianteRequest.setGuardians(guardianRequests);
    }

    private static @NotNull List<GuardianRequest> getGuardianRequests(AddressRequest addressDTO) {
        List<GuardianRequest> guardianRequests = new ArrayList<>();
        GuardianRequest guardianRequest = new GuardianRequest();
        guardianRequest.setName("Maria Silva");
        guardianRequest.setRelationship("Mãe");
        guardianRequest.setNif("987654321");
        guardianRequest.setPhone("123456789");
        guardianRequest.setOccupation("Teacher");
        guardianRequest.setWorkPhone("123123123");
        guardianRequest.setPrimaryGuardian(true);
        guardianRequest.setAddress(addressDTO);
        guardianRequests.add(guardianRequest);
        return guardianRequests;
    }

    @Test
    void getAllStudents_ShouldReturnListOfEstudiantes() throws Exception {
        // Arrange
        List<Estudiante> estudiantes = List.of(estudiante);
        when(estudianteService.getAllStudents()).thenReturn(estudiantes);

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("João Silva")))
                .andExpect(jsonPath("$[0].nif", is("123456789")));

        verify(estudianteService).getAllStudents();
    }

    @Test
    void getStudentById_WithValidId_ShouldReturnEstudiante() throws Exception {
        // Arrange
        when(estudianteService.getStudentById(1L)).thenReturn(Optional.of(estudiante));

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")));

        verify(estudianteService).getStudentById(1L);
    }

    @Test
    void getStudentById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(estudianteService.getStudentById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/999"))
                .andExpect(status().isNotFound());

        verify(estudianteService).getStudentById(999L);
    }

    @Test
    void getStudentByNif_WithValidNif_ShouldReturnEstudiante() throws Exception {
        // Arrange
        when(estudianteService.getStudentByNif("123456789")).thenReturn(Optional.of(estudiante));

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/nif/123456789"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")));

        verify(estudianteService).getStudentByNif("123456789");
    }

    @Test
    void searchStudentsByName_ShouldReturnListOfEstudiantes() throws Exception {
        // Arrange
        List<Estudiante> estudiantes = List.of(estudiante);
        when(estudianteService.searchStudentsByName("João")).thenReturn(estudiantes);

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/search")
                        .param("name", "João"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("João Silva")));

        verify(estudianteService).searchStudentsByName("João");
    }

    @Test
    void createStudent_WithValidRequest_ShouldReturnCreatedEstudiante() throws Exception {
        // Arrange
        when(estudianteService.isNifUnique(anyString())).thenReturn(true);
        when(classroomService.getClassroomById(anyLong())).thenReturn(classroom);
        when(estudianteService.saveStudent(any(Estudiante.class))).thenReturn(estudiante);

        // Act & Assert
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estudianteRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")));

        verify(estudianteService).isNifUnique(estudianteRequest.getNif());
        verify(estudianteService).saveStudent(any(Estudiante.class));
    }

    @Test
    void createStudent_WithDuplicateNif_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(estudianteService.isNifUnique("123456789")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estudianteRequest)))
                .andExpect(status().isBadRequest());

        verify(estudianteService).isNifUnique("123456789");
        verify(estudianteService, never()).saveStudent(any(Estudiante.class));
    }

    @Test
    void deleteStudent_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        when(estudianteService.getStudentById(1L)).thenReturn(Optional.of(estudiante));
        doNothing().when(estudianteService).deleteStudent(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/estudiantes/1"))
                .andExpect(status().isNoContent());

        verify(estudianteService).getStudentById(1L);
        verify(estudianteService).deleteStudent(1L);
    }
}