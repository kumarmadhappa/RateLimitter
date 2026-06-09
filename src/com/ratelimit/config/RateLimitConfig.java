package com.ratelimit.config;

public class RateLimitConfig {

    public String getUserId() {
        return userId;
    }

    public RateLimitType getType() {
        return type;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public int getFillRate() {
        return fillRate;
        //fill rate as  token per second/Window time
    }

    private String userId;
    private RateLimitType type;

    private int initialCapacity;
    private int fillRate;

    public RateLimitConfig(RateLimitType type, String userId) {
        this.type = type;
        this.userId = userId;
    }


    public void setInitialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    public void setFillRate(int fillRate) {
        this.fillRate = fillRate;
    }

    /*public RateLimitConfig builder(){

    }*/




}
