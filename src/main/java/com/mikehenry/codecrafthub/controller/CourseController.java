package com.mikehenry.codecrafthub.controller;

import com.mikehenry.codecrafthub.dto.CourseRequestDTO;
import com.mikehenry.codecrafthub.dto.CourseResponseDTO;
import com.mikehenry.codecrafthub.dto.CourseStatsDTO;
import com.mikehenry.codecrafthub.dto.ErrorResponseDTO;
import com.mikehenry.codecrafthub.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing courses.
 */
@Tag(name = "Courses", description = "CRUD operations for managing learning courses")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Operation(
            summary = "Create a new course",
            description = "Creates a new course and persists it to storage. " +
                          "The `status` field must be one of: `Not Started`, `In Progress`, `Completed`."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CourseRequestDTO.class),
                    examples = @ExampleObject(
                            name = "New course",
                            value = """
                                    {
                                      "name": "Spring Boot Fundamentals",
                                      "description": "Learn the core concepts of Spring Boot",
                                      "target_date": "2026-12-31",
                                      "status": "Not Started"
                                    }"""
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Course created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed — one or more required fields are missing or `status` is invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "error": {
                                        "message": "Name is required, Status is required",
                                        "type": "validation_error",
                                        "code": 400
                                      }
                                    }""")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Failed to persist course to file",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "error": {
                                        "message": "Internal server error: Failed to save courses to file",
                                        "type": "internal_server_error",
                                        "code": 500
                                      }
                                    }""")
                    )
            )
    })
    @PostMapping
    public ResponseEntity<CourseResponseDTO> addCourse(@Valid @org.springframework.web.bind.annotation.RequestBody CourseRequestDTO dto) {
        CourseResponseDTO created = courseService.addCourse(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(
            summary = "List all courses",
            description = "Returns all courses currently stored in the system. Returns an empty array when no courses exist."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Courses retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CourseResponseDTO.class))
                    )
            )
    })
    @GetMapping
    public List<CourseResponseDTO> getAllCourses() {
        return courseService.getAllCourses();
    }

    @Operation(
            summary = "Get course statistics",
            description = "Returns the total number of courses and a breakdown of courses by status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistics retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseStatsDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "total": 5,
                                      "by_status": {
                                        "not_started": 2,
                                        "in_progress": 2,
                                        "completed": 1
                                      }
                                    }""")
                    )
            )
    })
    @GetMapping("/stats")
    public CourseStatsDTO getStats() {
        return courseService.getStats();
    }

    @Operation(
            summary = "Get a course by ID",
            description = "Returns the course matching the given ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Course found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No course found for the given ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "error": {
                                        "message": "Course not found with id: 99",
                                        "type": "not_found_error",
                                        "code": 404
                                      }
                                    }""")
                    )
            )
    })
    @GetMapping("/{id}")
    public CourseResponseDTO getCourse(
            @Parameter(description = "ID of the course to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @Operation(
            summary = "Update a course",
            description = "Replaces all fields of the course identified by the given ID. " +
                          "The `status` field must be one of: `Not Started`, `In Progress`, `Completed`. " +
                          "The `created_at` timestamp is not modified."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CourseRequestDTO.class),
                    examples = @ExampleObject(
                            name = "Update course",
                            value = """
                                    {
                                      "name": "Spring Boot Fundamentals",
                                      "description": "Learn the core concepts of Spring Boot",
                                      "target_date": "2026-12-31",
                                      "status": "In Progress"
                                    }"""
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Course updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed — one or more required fields are missing or `status` is invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "error": {
                                        "message": "Invalid status value. Must be one of: [NOT_STARTED, IN_PROGRESS, COMPLETED]",
                                        "type": "bad_request_error",
                                        "code": 400
                                      }
                                    }""")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No course found for the given ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "error": {
                                        "message": "Course not found with id: 99",
                                        "type": "not_found_error",
                                        "code": 404
                                      }
                                    }""")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Failed to persist changes to file",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "error": {
                                        "message": "Internal server error: Failed to save courses to file",
                                        "type": "internal_server_error",
                                        "code": 500
                                      }
                                    }""")
                    )
            )
    })
    @PutMapping("/{id}")
    public CourseResponseDTO updateCourse(
            @Parameter(description = "ID of the course to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody CourseRequestDTO dto) {
        return courseService.updateCourse(id, dto);
    }

    @Operation(
            summary = "Delete a course",
            description = "Permanently removes the course identified by the given ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "No course found for the given ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "error": {
                                        "message": "Course not found with id: 99",
                                        "type": "not_found_error",
                                        "code": 404
                                      }
                                    }""")
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "ID of the course to delete", required = true, example = "1")
            @PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
