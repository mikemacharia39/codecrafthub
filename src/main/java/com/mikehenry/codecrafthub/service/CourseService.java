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
 * Service for managing courses and handling file-based persistence.
 *
 * <p>Courses are stored in-memory as a {@link List} and persisted to a JSON file at
 * {@code /tmp/courses.json}. The file is loaded on startup via {@link #init()} and
 * updated on every write operation.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CourseService {
    private final ObjectMapper objectMapper;

    private final AtomicLong idGenerator = new AtomicLong(1);
    private static final String FILE_PATH = "/tmp/courses.json";
    private List<Course> courses = new ArrayList<>();

    /**
     * Returns all courses.
     *
     * @return list of all courses as {@link CourseResponseDTO}
     */
    public List<CourseResponseDTO> getAllCourses() {
        return courses.stream().map(this::toResponseDTO).toList();
    }

    /**
     * Returns a single course by its ID.
     *
     * @param id the course ID
     * @return the matching course as {@link CourseResponseDTO}
     * @throws org.springframework.web.server.ResponseStatusException with 404 if not found
     */
    public CourseResponseDTO getCourseById(Long id) {
        Course course = findCourseById(id);
        return toResponseDTO(course);
    }

    /**
     * Creates a new course and persists it to the JSON file.
     *
     * <p>Validates the status value before saving. The ID is auto-incremented and
     * {@code createdAt} is set to the current timestamp.
     *
     * @param dto the request payload containing course details
     * @return the created course as {@link CourseResponseDTO}
     * @throws org.springframework.web.server.ResponseStatusException with 400 if status is invalid
     */
    public CourseResponseDTO addCourse(CourseRequestDTO dto) {
        CourseStatus.fromValue(dto.getStatus());
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

    /**
     * Updates an existing course identified by its ID and persists the changes.
     *
     * <p>Validates the new status value before applying any changes. The {@code createdAt}
     * timestamp is not modified.
     *
     * @param id  the ID of the course to update
     * @param dto the request payload with updated course details
     * @return the updated course as {@link CourseResponseDTO}
     * @throws org.springframework.web.server.ResponseStatusException with 404 if not found,
     *         or 400 if status is invalid
     */
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO dto) {
        CourseStatus.fromValue(dto.getStatus());
        Course course = findCourseById(id);
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setTargetDate(dto.getTargetDate());
        course.setStatus(dto.getStatus());
        saveCourses();
        return toResponseDTO(course);
    }

    /**
     * Deletes a course by its ID and persists the updated list.
     *
     * @param id the ID of the course to delete
     * @throws org.springframework.web.server.ResponseStatusException with 404 if not found
     */
    public void deleteCourse(Long id) {
        Course course = findCourseById(id);
        courses.remove(course);
        saveCourses();
    }

    /**
     * Looks up a course in the in-memory list by ID.
     *
     * @param id the course ID
     * @return the matching {@link Course}
     * @throws org.springframework.web.server.ResponseStatusException with 404 if not found
     */
    private Course findCourseById(Long id) {
        return courses.stream().filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id));
    }

    /**
     * Serializes the current in-memory course list to the JSON file.
     *
     * @throws org.springframework.web.server.ResponseStatusException with 500 on I/O failure
     */
    private void saveCourses() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), courses);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save courses to file", e);
        }
    }

    /**
     * Maps a {@link Course} entity to a {@link CourseResponseDTO}.
     *
     * @param course the course entity
     * @return the response DTO
     */
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

    /**
     * Initializes the service on startup.
     *
     * <p>If the JSON file does not exist, it is created with an empty course list.
     * If it exists, courses are loaded from it and the ID generator is seeded to one
     * above the highest existing ID to prevent collisions.
     */
    @PostConstruct
    public void init() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
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
