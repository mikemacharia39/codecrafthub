package com.mikehenry.codecrafthub.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a course with all required fields.
 * Jackson annotations map Java fields to JSON properties.
 */
@Setter
@Getter
@NoArgsConstructor
public class Course {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Target date is required")
    @JsonProperty("target_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDate;

    @NotBlank(message = "Status is required")
    private String status;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
