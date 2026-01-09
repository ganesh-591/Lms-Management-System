package com.lms.management.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    private String status;

    private Boolean showValidity;
    private Integer validityInDays;

    private Boolean allowOfflineMobile;
    private Boolean allowBookmark;
    private Boolean enableContentAccess;

    private String courseImageUrl;

    // 🔗 SHARE FEATURE (stored)
    @Column(unique = true)
    private String shareCode;

    private Boolean shareEnabled = true;

    // ❌ NOT stored in DB (computed every GET)
    @Transient
    private String shareLink;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Topic> topicRelation;

    @Transient
    private List<Topic> topics;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
