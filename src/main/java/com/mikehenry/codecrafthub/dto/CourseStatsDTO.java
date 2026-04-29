package com.mikehenry.codecrafthub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO carrying aggregate statistics about courses.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Aggregate statistics about courses")
public class CourseStatsDTO {

    @Schema(description = "Total number of courses", example = "5")
    private long total;

    @JsonProperty("by_status")
    @Schema(description = "Breakdown of courses by status")
    private StatusBreakdown byStatus;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Course count per status")
    public static class StatusBreakdown {

        @JsonProperty("not_started")
        @Schema(description = "Courses with status 'Not Started'", example = "2")
        private long notStarted;

        @JsonProperty("in_progress")
        @Schema(description = "Courses with status 'In Progress'", example = "2")
        private long inProgress;

        @Schema(description = "Courses with status 'Completed'", example = "1")
        private long completed;
    }
}
