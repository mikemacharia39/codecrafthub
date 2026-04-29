package com.mikehenry.codecrafthub.controller;

import com.mikehenry.codecrafthub.dto.CourseRequestDTO;
import com.mikehenry.codecrafthub.dto.CourseResponseDTO;
import com.mikehenry.codecrafthub.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing courses.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Add a new course.
     */
    @PostMapping
    public ResponseEntity<CourseResponseDTO> addCourse(@Valid @RequestBody CourseRequestDTO dto) {
        CourseResponseDTO created = courseService.addCourse(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get all courses.
     */
    @GetMapping
    public List<CourseResponseDTO> getAllCourses() {
        return courseService.getAllCourses();
    }

    /**
     * Get a specific course by id.
     */
    @GetMapping("/{id}")
    public CourseResponseDTO getCourse(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    /**
     * Update a course by id.
     */
    @PutMapping("/{id}")
    public CourseResponseDTO updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequestDTO dto) {
        return courseService.updateCourse(id, dto);
    }

    /**
     * Delete a course by id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
