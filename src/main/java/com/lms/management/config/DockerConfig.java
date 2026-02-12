package com.lms.management.config;

import java.net.URI;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

@Configuration
public class DockerConfig {

    @Bean
    public DockerClient dockerClient() {

        DockerHttpClient httpClient =
                new ApacheDockerHttpClient.Builder()
                        .dockerHost(URI.create("npipe:////./pipe/docker_engine")) // ✅ FIXED
                        .connectionTimeout(Duration.ofSeconds(30))
                        .responseTimeout(Duration.ofSeconds(45))
                        .build();

        return DockerClientBuilder.getInstance()
                .withDockerHttpClient(httpClient)
                .build();
    }
}
