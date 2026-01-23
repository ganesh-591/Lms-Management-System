package com.lms.management.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "topic_contents")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TopicContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    // VIDEO / PDF
    @Column(nullable = false)
    private String contentType;

    // FILE / URL
    @Column(nullable = false)
    private String contentSource;

    @Column(nullable = false)
    private String contentTitle;

    @Column(length = 1000)
    private String contentDescription;

    // FILE → /uploads/...
    // URL  → https://...
    @Column(nullable = true)
    private String fileUrl;

    private Integer contentOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnore
    private Topic topic;

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
