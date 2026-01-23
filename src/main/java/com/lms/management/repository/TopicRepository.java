package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByCourseCourseId(Long courseId);

    List<Topic> findByCourseCourseIdAndStatus(Long courseId, String status);

    // 🔥 HARD DELETE SUPPORT
    @Modifying
    @Transactional
    @Query("""
        delete from Topic t
        where t.course.courseId = :courseId
    """)
    void deleteByCourseId(@Param("courseId") Long courseId);
}
