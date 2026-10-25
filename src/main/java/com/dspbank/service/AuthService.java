package com.dspbank.service;

import com.dspbank.exception.ApiException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/** Simple in-memory login sessions. Fine for a portfolio project; swap for JWT/Spring Security later. */
@Service
public class AuthService {
    public record Session(String role, Long accountNumber) {}
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String open(String role, Long accountNumber) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(role, accountNumber));
        return token;
    }
    public void close(String token) { if (token != null) sessions.remove(token); }

    public Session require(String token, String role) {
        Session s = token == null ? null : sessions.get(token);
        if (s == null || !s.role().equals(role))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please log in again.");
        return s;
    }
}
