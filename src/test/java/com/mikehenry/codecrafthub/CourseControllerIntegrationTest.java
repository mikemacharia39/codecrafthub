package com.mikehenry.codecrafthub;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikehenry.codecrafthub.dto.CourseRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddAndGetCourse() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setName("Java 101");
        request.setDescription("Intro to Java");
        request.setTargetDate(LocalDate.of(2026, 5, 1));
        request.setStatus("Not Started");

        // Add course
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Java 101"))
                .andReturn().getResponse().getContentAsString();

        // Get all courses
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java 101"));
    }

    @Test
    void testValidationError() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setName(""); // Invalid
        request.setDescription(""); // Invalid
        request.setTargetDate(null); // Invalid
        request.setStatus("Invalid"); // Invalid

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCourseNotFound() throws Exception {
        mockMvc.perform(get("/api/courses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateAndDeleteCourse() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setName("Spring Boot");
        request.setDescription("Spring Boot Course");
        request.setTargetDate(LocalDate.of(2026, 6, 1));
        request.setStatus("Not Started");

        String response = mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(response).get("id").asLong();

        // Update
        request.setStatus("Completed");
        mockMvc.perform(put("/api/courses/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Completed"));

        // Delete
        mockMvc.perform(delete("/api/courses/" + id))
                .andExpect(status().isNoContent());
    }
}
