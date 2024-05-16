package com.project.studylink.service;

import java.util.Optional;

public interface RedisService {
    void save(String key, String value, int minute);

    Optional<String> findValue(String key);

    boolean validateValue(String key, String value);

    void deleteKey(String key);
}
