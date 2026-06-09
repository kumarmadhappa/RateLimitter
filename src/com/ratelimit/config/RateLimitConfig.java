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

    public int getWindowSizeInSeconds() {
        return windowSizeInSeconds;
    }

    private String userId;
    private RateLimitType type;

    private int initialCapacity;
    private int fillRate;
    private int windowSizeInSeconds;

    private RateLimitConfig(RateLimitType type, String userId) {
        this.type = type;
        this.userId = userId;
    }

    public RateLimitConfig(Builder builder) {
        this.type = builder.type;
        this.userId = builder.userId;
        this.initialCapacity = builder.initialCapacity;
        this.fillRate = builder.fillRate;
        this.windowSizeInSeconds = builder.windowSizeInSeconds;
    }


    public static class Builder{
        private final String userId;
        private final RateLimitType type;

        private int initialCapacity;
        private int fillRate;
        private int windowSizeInSeconds;

        public Builder(String userId, RateLimitType type, int fillRate, int initialCapacity, int windowSizeInSeconds){
            this.userId=userId;
            this.type= type;
            this.initialCapacity=initialCapacity;
            this.fillRate=fillRate;
            this.windowSizeInSeconds = windowSizeInSeconds;
        }

        public Builder initialCapacity(int initialCapacity){
            this.initialCapacity=initialCapacity;
            return this;
        }

        public Builder fillRate(int fillRate){
            this.fillRate=fillRate;
            return this;
        }

        public Builder windowSizeInSeconds(int windowSizeInSeconds){
            this.windowSizeInSeconds=windowSizeInSeconds;
            return this;
        }

        public RateLimitConfig build(){
            return new RateLimitConfig(this);
        }

    }




}
