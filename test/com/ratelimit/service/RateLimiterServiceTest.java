package com.ratelimit.service;

import com.ratelimit.config.RateLimitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        config.add(new RateLimitConfig.Builder("K123", TOKEN, 3,5, 10).build());

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
            assertTrue(service.allowRequest("K123"));
        }

        assertFalse(service.allowRequest("K123"));
    }

    @Test
    void allowRequestWhenTokenIsRefilled() throws InterruptedException {

        //Use up all the tokens in the bucket
        for (int i=0; i<config.get(1).getInitialCapacity(); i++){
            assertTrue(service.allowRequest("K123"));
        }

        //Waiting for Refill time as per user Config
        Thread.sleep(config.get(1).getWindowSizeInSeconds()* 1000L);


        assertTrue(service.allowRequest("K123"));
    }

    @Test
    void allowRequestWhenTokenIsRefilledAsPerRefillRate() throws InterruptedException {

        //Use up all the tokens in the bucket
        for (int i=0; i<config.get(1).getInitialCapacity(); i++){
            assertTrue(service.allowRequest("K123"));
        }

        //Waiting for Refill time as per user Config
        Thread.sleep(config.get(1).getWindowSizeInSeconds()* 1000L);

        //Request after refill
        for (int i=0; i<config.get(1).getFillRate(); i++){
            assertTrue(service.allowRequest("K123"));
        }
    }

    @Test
    void rejectRequestWhenTokenIsNotAvailableAfterRefill() throws InterruptedException {

        //Use up all the tokens in the bucket
        for (int i=0; i<config.get(1).getInitialCapacity(); i++){
            assertTrue(service.allowRequest("K123"));
        }

        //Waiting for Refill time as per user Config
        Thread.sleep(config.get(1).getWindowSizeInSeconds()* 1000L);

        //Use up all the tokens in the bucket after refill
        for (int i=0; i<config.get(1).getFillRate(); i++){
            assertTrue(service.allowRequest("K123"));
        }

        assertFalse(service.allowRequest("K123"));
    }
}