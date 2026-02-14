package com.lms.management.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(nullable = false, unique = true)
    private String courseName;

    private String description;
    private String duration;
    private String toolsCovered;
    private Double courseFee;

    private Boolean certificateProvided;

    // ✅ ACTIVE by default
    private String status = "ACTIVE";

    private Boolean showValidity;
    private Integer validityInDays;

    private Boolean allowOfflineMobile;
    private Boolean allowBookmark;

    // ❌ REMOVED: enableContentAccess

    private String courseImageUrl;

    // 🔗 SHARE FEATURE (stored)
    @Column(unique = true)
    private String shareCode;

    private Boolean shareEnabled = true;

    // 🔗 SHARE LINK (computed, not stored)
    @Transient
    private String shareLink;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    private List<Topic> topics;

    // ===============================
    // LIFECYCLE
    // ===============================
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===============================
    // EXPLICIT METHODS
    // ===============================
    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }
}