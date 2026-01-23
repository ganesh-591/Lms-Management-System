package com.lms.management.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lms.management.model.Session;
import com.lms.management.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionScheduler {

    private final SessionRepository sessionRepository;

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    public void updateSessionStatuses() {

        List<Session> sessions = sessionRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (Session session : sessions) {

            LocalDateTime startDateTime =
                    LocalDateTime.of(
                            session.getStartDate(),
                            session.getStartTime()
                    );

            LocalDateTime endDateTime =
                    startDateTime.plusMinutes(session.getDurationMinutes());

            String currentStatus = session.getStatus();

            if (now.isBefore(startDateTime)) {
                if (!"Upcoming".equals(currentStatus)) {
                    session.setStatus("Upcoming");
                }
            } 
            else if (now.isAfter(startDateTime) && now.isBefore(endDateTime)) {
                if (!"Running".equals(currentStatus)) {
                    session.setStatus("Running");
                }
            } 
            else {
                if (!"Completed".equals(currentStatus)) {
                    session.setStatus("Completed");
                }
            }
        }

        sessionRepository.saveAll(sessions);
    }
}
