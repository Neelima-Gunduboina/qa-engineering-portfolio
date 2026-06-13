package com.neelima.qa.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Lightweight JSON utilities for reading test data from classpath resources.
 */
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<Map<String, Object>> readListFromResource(String resourcePath) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new RuntimeException("Resource not found: " + resourcePath);
            return MAPPER.readValue(is, new TypeReference<List<Map<String, Object>>>(){});
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON resource: " + resourcePath, e);
        }
    }
}
