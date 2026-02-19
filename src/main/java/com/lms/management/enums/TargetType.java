package com.lms.management.enums;

public enum TargetType {

    COURSE(1),
    EXAM(2),
    QUIZ(3),
    WEBINAR(4),
    HACKATHON(5);

    private final int value;

    TargetType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TargetType fromValue(int value) {
        for (TargetType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TargetType value: " + value);
    }
}
