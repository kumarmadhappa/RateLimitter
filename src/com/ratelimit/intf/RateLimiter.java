package com.ratelimit.intf;

import com.ratelimit.config.RateLimitConfig;

public interface RateLimiter {
    public boolean allowed(String userId);
}