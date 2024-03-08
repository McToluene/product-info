package com.mctoluene.productinformationmanagement.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisUtility {
    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String defaultKey = "pim:";

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(this.getKey(key), objectMapper.convertValue(value, Map.class));
    }

    public void setValue(String key, Object value, long expiry) {
        redisTemplate.opsForValue().set(this.getKey(key), objectMapper.convertValue(value, Map.class),
                Duration.ofSeconds(expiry));
    }

    public Object getValue(String key) {
        try {
            return redisTemplate.opsForValue().get(this.getKey(key));
        } catch (Exception e) {
            log.error("Redis timeout : {}", e.getMessage());
            return null;
        }
    }

    public void removeValue(String key) {
        redisTemplate.delete(this.getKey(key));
    }

    private String getKey(String key) {
        return activeProfile + ":" + defaultKey + key;
    }

}
