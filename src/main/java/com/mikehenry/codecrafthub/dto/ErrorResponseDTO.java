package com.mikehenry.codecrafthub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standard error response envelope returned for all API error responses.
 */
@Getter
@AllArgsConstructor
@Schema(description = "Standard error response envelope")
public class ErrorResponseDTO {

    @Schema(description = "Error details")
    private final ErrorDetail error;

    public static ErrorResponseDTO of(String message, String type, int code) {
        return new ErrorResponseDTO(new ErrorDetail(message, type, code));
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "Detailed error information")
    public static class ErrorDetail {

        @Schema(description = "Human-readable description of the error", example = "Course not found with id: 99")
        private final String message;

        @Schema(
                description = "Machine-readable error type",
                example = "not_found_error",
                allowableValues = {"not_found_error", "validation_error", "bad_request_error", "internal_server_error"}
        )
        private final String type;

        @Schema(description = "HTTP status code", example = "404")
        private final int code;
    }
}
