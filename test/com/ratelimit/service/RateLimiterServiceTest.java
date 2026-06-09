package com.ratelimit.service;

import com.ratelimit.config.RateLimitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static com.ratelimit.config.RateLimitType.TOKEN;
import static org.junit.jupiter.api.Assertions.*;

public class RateLimiterServiceTest {

    RateLimiterService service ;
    List<RateLimitConfig> config = new ArrayList<>();

    @BeforeEach
    void setUp() {
        config = new ArrayList<>();
        config.add(new RateLimitConfig.Builder("All", TOKEN, 1, 1, 100).build());
        config.add(new RateLimitConfig.Builder("K123", TOKEN, 3,3, 100).build());

        service =  new RateLimiterService(config);
    }

    @Test
    void allowRequestWhenTokenIsAvailable() {
        boolean allowed = service.allowRequest("K123");
        assertTrue(allowed);
    }

    @Test
    void rejectRequestWhenTokenIsNotAvailable() {

        //Use up all the tokens in the bucket
        for (int i=0; i<config.get(1).getInitialCapacity(); i++){
            service.allowRequest("K123");
        }

        boolean allowed = service.allowRequest("K123");
        assertFalse(allowed);
    }
}