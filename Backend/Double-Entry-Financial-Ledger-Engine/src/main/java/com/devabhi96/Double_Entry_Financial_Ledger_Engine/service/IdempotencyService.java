package com.devabhi96.Double_Entry_Financial_Ledger_Engine.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

@Service
public class IdempotencyService {

    private static final Duration TTL = Duration.ofHours(24);
    private final StringRedisTemplate redis;

    public IdempotencyService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public String hash(String payload) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    public boolean tryClaim(String key, String requestHash) {
        return Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(key, "PROCESSING|" + requestHash, TTL));
    }

    public String get(String key) {
        return redis.opsForValue().get(key);
    }

    public void complete(String key, String record) {
        redis.opsForValue().set(key, record, TTL);
    }

    public void release(String key) {
        redis.delete(key);
    }
}