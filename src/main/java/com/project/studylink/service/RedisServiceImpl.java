package com.project.studylink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService{

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String key, String value, int minute) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        Duration duration = Duration.ofMinutes(minute);
        vop.set(key, value, duration);
    }

    @Override
    public Optional<String> findValue(String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String result = vop.get(key);
        return Optional.ofNullable(result);
    }

    @Override
    public boolean validateValue(String key, String value) {
        Optional<String> findValue = findValue(key);
        return findValue.isPresent() && findValue.get().equals(value);
    }

    @Override
    public void deleteKey(String key){
        redisTemplate.delete(key);
    }
}