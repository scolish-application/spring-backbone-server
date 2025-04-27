package com.github.simaodiazz.schola.backbone.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Address;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Gender;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Guardian;
import com.github.simaodiazz.schola.backbone.server.entity.data.service.EstudianteService;
import com.github.simaodiazz.schola.backbone.server.router.controller.EstudianteController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.AddressRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.EstudianteRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.GuardianRequest;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @InjectMocks
    private EstudianteController estudianteController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(estudianteController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllStudents_ShouldReturnListOfEstudianteResponses() throws Exception {
        // Arrange
        List<Estudiante> estudiantes = createSampleEstudianteList();
        when(estudianteService.getAllStudents()).thenReturn(estudiantes);

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("João Silva")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Maria Santos")));

        verify(estudianteService, times(1)).getAllStudents();
    }

    @Test
    void getStudentById_WhenStudentExists_ShouldReturnEstudianteResponse() throws Exception {
        // Arrange
        Estudiante estudiante = createSampleEstudiante();
        when(estudianteService.getStudentById(1L)).thenReturn(Optional.of(estudiante));

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")))
                .andExpect(jsonPath("$.schoolClass", is("12A")));

        verify(estudianteService, times(1)).getStudentById(1L);
    }

    @Test
    void getStudentById_WhenStudentDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(estudianteService.getStudentById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(estudianteService, times(1)).getStudentById(99L);
    }

    @Test
    void getStudentByNif_WhenStudentExists_ShouldReturnEstudianteResponse() throws Exception {
        // Arrange
        Estudiante estudiante = createSampleEstudiante();
        when(estudianteService.getStudentByNif("123456789")).thenReturn(Optional.of(estudiante));

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/nif/123456789")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")));

        verify(estudianteService, times(1)).getStudentByNif("123456789");
    }

    @Test
    void getStudentsByClass_ShouldReturnListOfEstudianteResponses() throws Exception {
        // Arrange
        List<Estudiante> estudiantes = List.of(createSampleEstudiante());
        when(estudianteService.getStudentsBySchoolClass("12A")).thenReturn(estudiantes);

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/class/12A")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].schoolClass", is("12A")));

        verify(estudianteService, times(1)).getStudentsBySchoolClass("12A");
    }

    @Test
    void searchStudentsByName_ShouldReturnMatchingStudents() throws Exception {
        // Arrange
        List<Estudiante> estudiantes = List.of(createSampleEstudiante());
        when(estudianteService.searchStudentsByName("João")).thenReturn(estudiantes);

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/search")
                        .param("name", "João")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("João Silva")));

        verify(estudianteService, times(1)).searchStudentsByName("João");
    }

    @Test
    void getStudentsByGuardianName_ShouldReturnMatchingStudents() throws Exception {
        // Arrange
        List<Estudiante> estudiantes = List.of(createSampleEstudiante());
        when(estudianteService.getStudentsByGuardianName("Ana")).thenReturn(estudiantes);

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/guardian/name")
                        .param("name", "Ana")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(estudianteService, times(1)).getStudentsByGuardianName("Ana");
    }

    @Test
    void getStudentsByGuardianNif_ShouldReturnMatchingStudents() throws Exception {
        // Arrange
        List<Estudiante> estudiantes = List.of(createSampleEstudiante());
        when(estudianteService.getStudentsByGuardianNif("987654321")).thenReturn(estudiantes);

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/guardian/nif")
                        .param("nif", "987654321")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(estudianteService, times(1)).getStudentsByGuardianNif("987654321");
    }

    @Test
    void getStudentByUserId_WhenStudentExists_ShouldReturnEstudianteResponse() throws Exception {
        // Arrange
        Estudiante estudiante = createSampleEstudiante();
        when(estudianteService.getStudentByUserId(1L)).thenReturn(Optional.of(estudiante));

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("João Silva")));

        verify(estudianteService, times(1)).getStudentByUserId(1L);
    }

    @Test
    void createStudent_WithValidData_ShouldReturnCreatedEstudianteResponse() throws Exception {
        // Arrange
        EstudianteRequest request = createSampleEstudianteRequest();
        Estudiante estudiante = createSampleEstudiante();

        when(estudianteService.isNifUnique(anyString())).thenReturn(true);
        when(estudianteService.saveStudent(any())).thenReturn(estudiante);

        // Act & Assert
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")));

        verify(estudianteService, times(1)).isNifUnique(request.getNif());
        verify(estudianteService, times(1)).saveStudent(any());
    }

    @Test
    void createStudent_WithNonUniqueNif_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EstudianteRequest request = createSampleEstudianteRequest();

        when(estudianteService.isNifUnique(anyString())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(estudianteService, times(1)).isNifUnique(request.getNif());
        verify(estudianteService, never()).saveStudent(any());
    }

    @Test
    void updateStudent_WithValidData_ShouldReturnUpdatedEstudianteResponse() throws Exception {
        // Arrange
        EstudianteRequest request = createSampleEstudianteRequest();
        Estudiante existingStudent = createSampleEstudiante();
        Estudiante updatedStudent = createSampleEstudiante();
        updatedStudent.setName("João Silva Atualizado");

        when(estudianteService.getStudentById(1L)).thenReturn(Optional.of(existingStudent));
        lenient().when(estudianteService.isNifUnique(anyString())).thenReturn(true);
        when(estudianteService.saveStudent(any())).thenReturn(updatedStudent);

        // Act & Assert
        mockMvc.perform(put("/api/estudiantes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("João Silva Atualizado")));

        verify(estudianteService, times(1)).getStudentById(1L);
        verify(estudianteService, times(1)).saveStudent(any());
    }

    @Test
    void updateStudent_WhenStudentDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        EstudianteRequest request = createSampleEstudianteRequest();

        when(estudianteService.getStudentById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/estudiantes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(estudianteService, times(1)).getStudentById(99L);
        verify(estudianteService, never()).saveStudent(any());
    }

    @Test
    void deleteStudent_WhenStudentExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        Estudiante estudiante = createSampleEstudiante();
        when(estudianteService.getStudentById(1L)).thenReturn(Optional.of(estudiante));
        doNothing().when(estudianteService).deleteStudent(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/estudiantes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(estudianteService, times(1)).getStudentById(1L);
        verify(estudianteService, times(1)).deleteStudent(1L);
    }

    @Test
    void deleteStudent_WhenStudentDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(estudianteService.getStudentById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/estudiantes/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(estudianteService, times(1)).getStudentById(99L);
        verify(estudianteService, never()).deleteStudent(anyLong());
    }

    @Test
    void checkNifUniqueness_WhenNifIsUnique_ShouldReturnTrue() throws Exception {
        // Arrange
        when(estudianteService.isNifUnique("123456789")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/estudiantes/check-nif")
                        .param("nif", "123456789")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(estudianteService, times(1)).isNifUnique("123456789");
    }

    // Helper methods to create test data

    private Estudiante createSampleEstudiante() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setName("João Silva");
        estudiante.setNif("123456789");
        estudiante.setDateOfBirth(LocalDate.of(2005, 5, 15));
        estudiante.setPhone("912345678");
        estudiante.setGender(Gender.MALE);
        estudiante.setSchoolClass("12A");
        estudiante.setEmergencyContactName("Ana Silva");
        estudiante.setEmergencyContactPhone("965432178");
        estudiante.setSpecialNeeds(false);
        estudiante.setMedicalInformation("No allergies");

        Address address = new Address();
        address.setId(1L);
        address.setStreet("Rua Principal, 123");
        address.setCity("Lisboa");
        address.setPostalCode("1000-001");
        address.setCountry("Portugal");
        estudiante.setAddress(address);

        Guardian guardian = new Guardian();
        guardian.setId(1L);
        guardian.setName("Ana Silva");
        guardian.setRelationship("Mãe");
        guardian.setNif("987654321");
        guardian.setPhone("965432178");
        guardian.setOccupation("Professora");
        guardian.setWorkPhone("210123456");
        guardian.setPrimaryGuardian(true);

        Address guardianAddress = new Address();
        guardianAddress.setId(2L);
        guardianAddress.setStreet("Rua Principal, 123");
        guardianAddress.setCity("Lisboa");
        guardianAddress.setPostalCode("1000-001");
        guardianAddress.setCountry("Portugal");
        guardian.setAddress(guardianAddress);

        User user = new User();
        user.setId(1L);
        guardian.setUser(user);

        List<Guardian> guardians = new ArrayList<>();
        guardians.add(guardian);
        estudiante.setGuardians(guardians);

        User studentUser = new User();
        studentUser.setId(2L);
        estudiante.setUser(studentUser);

        return estudiante;
    }

    private List<Estudiante> createSampleEstudianteList() {
        Estudiante estudiante1 = createSampleEstudiante();

        Estudiante estudiante2 = new Estudiante();
        estudiante2.setId(2L);
        estudiante2.setName("Maria Santos");
        estudiante2.setNif("234567890");
        estudiante2.setDateOfBirth(LocalDate.of(2006, 7, 20));
        estudiante2.setPhone("923456789");
        estudiante2.setGender(Gender.FEMALE);
        estudiante2.setSchoolClass("11B");

        return List.of(estudiante1, estudiante2);
    }

    private EstudianteRequest createSampleEstudianteRequest() {
        EstudianteRequest request = new EstudianteRequest();
        request.setName("João Silva");
        request.setNif("123456789");
        request.setDateOfBirth(LocalDate.of(2005, 5, 15));
        request.setPhone("912345678");
        request.setGender(Gender.MALE);
        request.setSchoolClass("12A");
        request.setEmergencyContactName("Ana Silva");
        request.setEmergencyContactPhone("965432178");
        request.setSpecialNeeds(false);
        request.setMedicalInformation("No allergies");

        AddressRequest addressRequest = new AddressRequest(
                "Rua Principal, 123",
                "Lisboa",
                "1000-001",
                "Portugal"
        );
        request.setAddress(addressRequest);

        GuardianRequest guardianRequest = new GuardianRequest();
        guardianRequest.setName("Ana Silva");
        guardianRequest.setRelationship("Mãe");
        guardianRequest.setNif("987654321");
        guardianRequest.setPhone("965432178");
        guardianRequest.setOccupation("Professora");
        guardianRequest.setWorkPhone("210123456");
        guardianRequest.setPrimaryGuardian(true);
        guardianRequest.setAddress(addressRequest);
        guardianRequest.setUserId(1L);

        List<GuardianRequest> guardians = new ArrayList<>();
        guardians.add(guardianRequest);
        request.setGuardians(guardians);

        return request;
    }
}