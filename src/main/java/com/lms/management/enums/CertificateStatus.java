package com.lms.management.enums;

public enum CertificateStatus {

    ACTIVE(1),
    EXPIRED(2),
    REVOKED(3);

    private final int value;

    CertificateStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CertificateStatus fromValue(int value) {
        for (CertificateStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid CertificateStatus value: " + value);
    }
}
