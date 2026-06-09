package com.ratelimit.intf;

import com.ratelimit.config.RateLimitConfig;
import com.ratelimit.config.RateLimitType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TokenBucket implements RateLimiter {
    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();
    private final Map<String, Long> lastRefillTime = new ConcurrentHashMap<>();
    private final Map<String, RateLimitConfig> config = new ConcurrentHashMap<>();

    public TokenBucket(Map<String, RateLimitConfig> rateConfig) {

        Map<String, RateLimitConfig> tokenConfig = getTokenConfig(rateConfig);
        config.putAll(tokenConfig);

        for (Map.Entry<String, RateLimitConfig> entry: rateConfig.entrySet()){
            tokens.put(entry.getKey(), entry.getValue().getInitialCapacity());
            lastRefillTime.put(entry.getKey(), System.nanoTime());
        }
    }


    @Override
    public boolean allowed(String userId) {
        AtomicBoolean allowed = new AtomicBoolean(false);
        Long now = System.nanoTime();
        refillTokens(userId, now); //1) Refill
        tokens.computeIfPresent(userId, (id, currentTokens)-> {
            //2) Check current tokens
            //3) return allowed as true if tokens available
            if(currentTokens > 0){
                allowed.set(true);
                return currentTokens-1;
            }
            else{
                return currentTokens;
            }
        });
        tokens.computeIfAbsent(userId, k -> {
            allowed.set(true);
            return config.get("All").getInitialCapacity();
        });

        return allowed.get();
    }

    private void refillTokens(String userId, Long now) {
        long lastRefill = lastRefillTime.getOrDefault(userId, now);
        lastRefillTime.put(userId, lastRefill);
        long elapsedTime = (now - lastRefill)/1_000_000_000;

        RateLimitConfig conf = config.getOrDefault(userId, config.get("All"));
        float fillRate = (float) conf.getWindowSizeInSeconds() /conf.getFillRate();

        int refillTokens = (int) (elapsedTime/fillRate);
        int maxTokens = conf.getInitialCapacity();

        if(refillTokens>0){
            lastRefillTime.put(userId, now);
            tokens.compute(userId, (key,value) ->
                    (value == null) ? maxTokens : Math.min(maxTokens, value + refillTokens)
            );
        }
    }

    private Map<String, RateLimitConfig> getTokenConfig(Map<String, RateLimitConfig> rateConfig) {
        return rateConfig.entrySet().stream()
                .filter(e -> e.getValue().getType().compareTo(RateLimitType.TOKEN)==0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
