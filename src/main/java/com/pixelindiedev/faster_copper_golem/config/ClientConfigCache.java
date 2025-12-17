package com.pixelindiedev.faster_copper_golem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConfigCache {
    private static final Logger LOGGER = LoggerFactory.getLogger("TEsting");
    public static float speedMultiplier = 1.0f;
    public static float movementSpeed = 1.0f;
    public static int interactionTime = 60;
    public static int cooldownTime = 140;
    public static int maxStackSize = 16;
    public static int maxChestsRemembered = 10;
    public static int horizontalSearchRadius = 32;
    public static int verticalSearchRadius = 8;

    public static void update(float speedMultiplier, float movementSpeed, int interactionTime, int cooldownTime, int maxStackSize, int maxChestsRemembered, int horizontalSearchRadius, int verticalSearchRadius) {
        ClientConfigCache.speedMultiplier = speedMultiplier;
        ClientConfigCache.movementSpeed = movementSpeed;
        ClientConfigCache.interactionTime = interactionTime;
        ClientConfigCache.cooldownTime = cooldownTime;
        ClientConfigCache.maxStackSize = maxStackSize;
        ClientConfigCache.maxChestsRemembered = maxChestsRemembered;
        ClientConfigCache.horizontalSearchRadius = horizontalSearchRadius;
        ClientConfigCache.verticalSearchRadius = verticalSearchRadius;
    }
}
