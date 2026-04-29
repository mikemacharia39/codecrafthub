package com.mikehenry.codecrafthub.enums;

import java.util.Arrays;

public enum CourseStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String value;
    CourseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CourseStatus fromValue(String value) {
        for (CourseStatus status : CourseStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value. Must be one of: " + Arrays.toString(CourseStatus.values()));
    }
}
