package com.github.simaodiazz.schola.backbone.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Course;
import com.github.simaodiazz.schola.backbone.server.course.data.service.CourseService;
import com.github.simaodiazz.schola.backbone.server.router.controller.CourseController;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllCourses() throws Exception {
        // Create test courses
        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Computer Science");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Mathematics");

        List<Course> courses = Arrays.asList(course1, course2);

        when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Computer Science"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Mathematics"));

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void testGetCourseById() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setName("Computer Science");

        when(courseService.getCourseById(1L)).thenReturn(Optional.of(course));

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Computer Science"));

        verify(courseService, times(1)).getCourseById(1L);
    }

    @Test
    void testGetCourseByIdNotFound() throws Exception {
        when(courseService.getCourseById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/courses/99"))
                .andExpect(status().isNotFound());

        verify(courseService, times(1)).getCourseById(99L);
    }

    @Test
    void testSearchCoursesByName() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setName("Computer Science");

        List<Course> courses = List.of(course);

        when(courseService.searchCoursesByName("Computer")).thenReturn(courses);

        mockMvc.perform(get("/api/courses/search")
                        .param("name", "Computer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Computer Science"));

        verify(courseService, times(1)).searchCoursesByName("Computer");
    }

    @Test
    void testCreateCourse() throws Exception {
        Course courseToCreate = new Course();
        courseToCreate.setName("Physics");

        Course createdCourse = new Course();
        createdCourse.setId(3L);
        createdCourse.setName("Physics");

        when(courseService.saveCourse(any(Course.class))).thenReturn(createdCourse);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Physics"));

        verify(courseService, times(1)).saveCourse(any(Course.class));
    }

    @Test
    void testDeleteCourse() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());

        verify(courseService, times(1)).deleteCourse(1L);
    }

    @Test
    void testEmptySearchResult() throws Exception {
        when(courseService.searchCoursesByName(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/courses/search")
                        .param("name", "NonExistentCourse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(courseService, times(1)).searchCoursesByName("NonExistentCourse");
    }
}