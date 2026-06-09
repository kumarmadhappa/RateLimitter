package com.ratelimit.service;

import com.ratelimit.config.RateLimitConfig;
import com.ratelimit.config.RateLimitType;
import com.ratelimit.intf.RateLimiter;
import com.ratelimit.intf.TokenBucket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ratelimit.config.RateLimitType.TOKEN;

public class RateLimiterService {

    private final Map<RateLimitType, RateLimiter> rateLimiters = new ConcurrentHashMap<>();
    private final Map<String, RateLimitConfig> rateConfig =  new ConcurrentHashMap<>();

    public RateLimiterService(List<RateLimitConfig> config) {
        loadRateLimitersFromConfig(config);
    }

    private void loadRateLimitersFromConfig(List<RateLimitConfig> config) {
        //Read Config from file or DB
        for(RateLimitConfig cfg: config){
            rateConfig.put(cfg.getUserId(), cfg);
        }

        rateLimiters.put(TOKEN, new TokenBucket(rateConfig));

    }

    public boolean allowRequest(String userId){

        RateLimitConfig config = rateConfig.getOrDefault(userId, rateConfig.get("All"));
        RateLimiter rateLimiter = rateLimiters.get(config.getType());
        return rateLimiter.allowed(userId);
    }


}
