package com.ratelimit.intf;

import com.ratelimit.config.RateLimitConfig;
import com.ratelimit.config.RateLimitType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TokenBucket implements RateLimiter {


    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();
    private final Map<String, Long> lastRefillTime = new ConcurrentHashMap<>();
    private final Map<String, RateLimitConfig> config = new ConcurrentHashMap<>();

    public TokenBucket(Map<String, RateLimitConfig> rateConfig) {
        config.putAll(rateConfig); //Todo to filter all the token bucket config

        for (Map.Entry<String, RateLimitConfig> entry: rateConfig.entrySet()){
            tokens.put(entry.getKey(), entry.getValue().getInitialCapacity());
            lastRefillTime.put(entry.getKey(), System.nanoTime());
        }
    }


    @Override
    public boolean allowed(String userId) {
        AtomicBoolean allowed = new AtomicBoolean(false);
        Long now = System.nanoTime();
        tokens.compute(userId, (id, availableTokens)-> {
            //1) Refill
            //2) Get current tokens
            //3) Check
            //4) return allowed as true if tokens available
            refillToken(userId, now);
            int currentTokens = tokens.get(userId);
            if(currentTokens > 0){
                allowed.set(true);
                return currentTokens-1;
            }
            else{
                return currentTokens;
            }
        });
        return allowed.get();
    }

    private void refillToken(String userId, Long now) {
        long lastRefill = lastRefillTime.getOrDefault(userId, now);
        long elapsedTime = (now - lastRefill)/1000;

        int fillRate = config.get(userId) == null ? 1: config.get(userId).getFillRate();
        int refillTokens = (int) elapsedTime/fillRate;

        if(refillTokens>0){
            lastRefillTime.put(userId, now);
            tokens.compute(userId, (key,value) ->
                    (value == null) ? config.get(userId).getInitialCapacity()
                            : Math.min(config.get(userId).getInitialCapacity(),
                            value + refillTokens)
            );
        }
    }
}
