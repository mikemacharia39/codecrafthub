package com.mikehenry.codecrafthub.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikehenry.codecrafthub.dto.CourseRequestDTO;
import com.mikehenry.codecrafthub.dto.CourseResponseDTO;
import com.mikehenry.codecrafthub.enums.CourseStatus;
import com.mikehenry.codecrafthub.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for managing courses and handling file I/O.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseService {
    private final ObjectMapper objectMapper;

    private final AtomicLong idGenerator = new AtomicLong(1);
    private static final String FILE_PATH = "/tmp/courses.json";
    private List<Course> courses = new ArrayList<>();

    public List<CourseResponseDTO> getAllCourses() {
        return courses.stream().map(this::toResponseDTO).toList();
    }

    public CourseResponseDTO getCourseById(Long id) {
        Course course = findCourseById(id);
        return toResponseDTO(course);
    }

    public CourseResponseDTO addCourse(CourseRequestDTO dto) {
        CourseStatus.fromValue(dto.getStatus()); // Validate status
        Course course = new Course();
        course.setId(idGenerator.getAndIncrement());
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setTargetDate(dto.getTargetDate());
        course.setStatus(dto.getStatus());
        course.setCreatedAt(LocalDateTime.now());
        courses.add(course);
        saveCourses();
        return toResponseDTO(course);
    }

    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO dto) {
        CourseStatus.fromValue(dto.getStatus()); // Validate status
        Course course = findCourseById(id);
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setTargetDate(dto.getTargetDate());
        course.setStatus(dto.getStatus());
        saveCourses();
        return toResponseDTO(course);
    }

    public void deleteCourse(Long id) {
        Course course = findCourseById(id);
        courses.remove(course);
        saveCourses();
    }

    private Course findCourseById(Long id) {
        return courses.stream().filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id));
    }

    private void saveCourses() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), courses);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save courses to file", e);
        }
    }

    private CourseResponseDTO toResponseDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setTargetDate(course.getTargetDate());
        dto.setStatus(course.getStatus());
        dto.setCreatedAt(course.getCreatedAt());
        return dto;
    }

    @PostConstruct
    public void init() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                // Ensure parent directories exist
                file.getParentFile().mkdirs();
                // Create the file
                file.createNewFile();
                // Save initial empty courses list
                saveCourses();
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initialize courses file", e);
            }
        } else {
            try {
                courses = objectMapper.readValue(file, new TypeReference<>() {});
                long maxId = courses.stream().mapToLong(Course::getId).max().orElse(0L);
                idGenerator.set(maxId + 1);
            } catch (IOException e) {
                courses = new ArrayList<>();
            }
        }
    }
}
