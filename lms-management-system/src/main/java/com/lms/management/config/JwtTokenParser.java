package com.lms.management.config;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

@Component
public class JwtTokenParser {

    private Map<String, Object> getClaims(String token) {
        try {
            String payload = token.split("\\.")[1];
            String json = new String(Base64.getUrlDecoder().decode(payload));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token");
        }
    }

    public String getEmail(String token) {
        return (String) getClaims(token).get("sub");
    }

    public List<String> getRoles(String token) {
        return (List<String>) getClaims(token).get("roles");
    }

    public List<String> getPermissions(String token) {
        return (List<String>) getClaims(token).get("permissions");
    }
}