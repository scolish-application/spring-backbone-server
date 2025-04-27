package com.github.simaodiazz.schola.backbone.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Teacher;
import com.github.simaodiazz.schola.backbone.server.entity.data.service.TeacherService;
import com.github.simaodiazz.schola.backbone.server.router.controller.TeacherController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.TeacherRequest;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private TeacherController teacherController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getTeacherById_WhenTeacherExists_ShouldReturnTeacher() throws Exception {
        // Arrange
        Teacher teacher = createSampleTeacher();
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));

        // Act & Assert
        mockMvc.perform(get("/api/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(teacherService, times(1)).getTeacherById(1L);
    }

    @Test
    void getTeacherById_WhenTeacherDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(teacherService.getTeacherById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/teachers/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(teacherService, times(1)).getTeacherById(99L);
    }

    @Test
    void getTeacherByNif_WhenTeacherExists_ShouldReturnTeacher() throws Exception {
        // Arrange
        Teacher teacher = createSampleTeacher();
        when(teacherService.getTeacherByNif("123456789")).thenReturn(Optional.of(teacher));

        // Act & Assert
        mockMvc.perform(get("/api/teachers/nif")
                        .param("nif", "123456789")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")));

        verify(teacherService, times(1)).getTeacherByNif("123456789");
    }

    @Test
    void getTeacherByUserId_WhenTeacherExists_ShouldReturnTeacher() throws Exception {
        // Arrange
        Teacher teacher = createSampleTeacher();
        when(teacherService.getTeacherByUserId(1L)).thenReturn(Optional.of(teacher));

        // Act & Assert
        mockMvc.perform(get("/api/teachers/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")));

        verify(teacherService, times(1)).getTeacherByUserId(1L);
    }

    @Test
    void searchTeachersByName_ShouldReturnMatchingTeachers() throws Exception {
        // Arrange
        List<Teacher> teachers = Arrays.asList(createSampleTeacher());
        when(teacherService.getTeacherByName("João Silva")).thenReturn(teachers);

        // Act & Assert
        mockMvc.perform(get("/api/teachers/name")
                        .param("name", "João Silva")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("João Silva")));

        verify(teacherService, times(1)).getTeacherByName("João Silva");
    }

    @Test
    void searchTeachersByName_WhenNoTeachersFound_ShouldReturnNoContent() throws Exception {
        when(teacherService.getTeacherByName("NonExistent")).thenReturn(List.of());
        mockMvc.perform(get("/api/teachers/name")
                        .param("name", "NonExistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(teacherService, times(1)).getTeacherByName("NonExistent");
    }

    @Test
    void getAllTeachers_ShouldReturnAllTeachers() throws Exception {
        // Arrange
        List<Teacher> teachers = Arrays.asList(createSampleTeacher());
        when(teacherService.getAllTeachers()).thenReturn(teachers);

        // Act & Assert
        mockMvc.perform(get("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("João Silva")));

        verify(teacherService, times(1)).getAllTeachers();
    }

    @Test
    void getAllTeachers_WhenNoTeachersFound_ShouldReturnNoContent() throws Exception {
        when(teacherService.getAllTeachers()).thenReturn(List.of());
        mockMvc.perform(get("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(teacherService, times(1)).getAllTeachers();
    }

    @Test
    void createTeacher_ShouldReturnCreatedTeacher() throws Exception {
        // Arrange
        Teacher teacher = createSampleTeacher();
        TeacherRequest request = createSampleTeacherRequest();

        when(teacherService.isNifUnique("123456789")).thenReturn(true);
        when(teacherService.saveTeacher(any(Teacher.class))).thenReturn(teacher);

        // Act & Assert
        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.nif", is("123456789")));

        verify(teacherService, times(1)).isNifUnique("123456789");
        verify(teacherService, times(1)).saveTeacher(any(Teacher.class));
    }

    @Test
    void createTeacher_WithDuplicateNif_ShouldReturnBadRequest() throws Exception {
        // Arrange
        TeacherRequest request = createSampleTeacherRequest();

        when(teacherService.isNifUnique("123456789")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(teacherService, times(1)).isNifUnique("123456789");
        verify(teacherService, never()).saveTeacher(any(Teacher.class));
    }

    @Test
    void updateTeacher_WhenTeacherExists_ShouldReturnUpdatedTeacher() throws Exception {
        // Arrange
        Teacher existingTeacher = createSampleTeacher();
        Teacher updatedTeacher = createSampleTeacher();
        updatedTeacher.setName("João Updated");

        TeacherRequest request = createSampleTeacherRequest();
        request.setName("João Updated");

        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(existingTeacher));
        when(teacherService.saveTeacher(any(Teacher.class))).thenReturn(updatedTeacher);

        // Act & Assert
        mockMvc.perform(put("/api/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("João Updated")));

        verify(teacherService, times(1)).getTeacherById(1L);
        verify(teacherService, times(1)).saveTeacher(any(Teacher.class));
    }

    @Test
    void updateTeacher_WhenTeacherDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        TeacherRequest request = createSampleTeacherRequest();

        when(teacherService.getTeacherById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/teachers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(teacherService, times(1)).getTeacherById(99L);
        verify(teacherService, never()).saveTeacher(any(Teacher.class));
    }

    @Test
    void deleteTeacher_WhenTeacherExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        Teacher teacher = createSampleTeacher();

        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));
        doNothing().when(teacherService).deleteTeacher(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(teacherService, times(1)).getTeacherById(1L);
        verify(teacherService, times(1)).deleteTeacher(1L);
    }

    @Test
    void deleteTeacher_WhenTeacherDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(teacherService.getTeacherById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/teachers/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(teacherService, times(1)).getTeacherById(99L);
        verify(teacherService, never()).deleteTeacher(anyLong());
    }

    @Test
    void checkNifUniqueness_ShouldReturnResult() throws Exception {
        // Arrange
        when(teacherService.isNifUnique("123456789")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/teachers/check-nif")
                        .param("nif", "123456789")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(teacherService, times(1)).isNifUnique("123456789");
    }

    private Teacher createSampleTeacher() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("João Silva");
        teacher.setNif("123456789");

        User user = new User();
        user.setId(1L);
        teacher.setUser(user);

        return teacher;
    }

    private TeacherRequest createSampleTeacherRequest() {
        return new TeacherRequest("João Silva", "123456789", 1L);
    }
}