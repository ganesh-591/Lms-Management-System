package com.lms.management.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "evaluationExecutor")
    public Executor evaluationExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);     // 2 parallel evaluations minimum
        executor.setMaxPoolSize(5);      // max 5 parallel
        executor.setQueueCapacity(20);   // queue limit
        executor.setThreadNamePrefix("Evaluation-");

        executor.initialize();
        return executor;
    }
}
