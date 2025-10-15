package com.qqqqqq.template21.frame.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * @author qmf
 */
@Configuration
@RequiredArgsConstructor
public class JacksonConfig {

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void configuration() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
    }
}