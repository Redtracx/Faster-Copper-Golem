package com.pixelindiedev.faster_copper_golem;

import com.pixelindiedev.faster_copper_golem.config.ConfigSyncPayload;
import com.pixelindiedev.faster_copper_golem.config.ModModConfig;
import com.pixelindiedev.faster_copper_golem.mixin.MoveItemsTaskAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class Faster_copper_golem implements ModInitializer {
    private static final Set<MoveItemsTask> loadedMoveItemTasks = Collections.newSetFromMap(new WeakHashMap<>());
    public static ModModConfig CONFIG;

    private static int interactionTimeCache = -1;
    private static int cooldownTimeCache = -1;

    public static void onServerTick(MinecraftServer server) {
        if (CONFIG.hasExternalChange()) {
            CONFIG = ModModConfig.load();
            clearCache();
            UpdateTasks();

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) sendConfigToPlayer(player);
        }
    }

    private static void clearCache() {
        interactionTimeCache = -1;
        cooldownTimeCache = -1;
    }

    public static float getSpeedMultiplier() {
        return switch (CONFIG.gollemInteractionTime) {
            case Fast -> 0.5f;
            case Faster -> 0.25f;
            case Fastest -> 0.05f;
            case null, default -> 1;
        };
    }

    public static int getInteractionTime() {
        if (interactionTimeCache == -1) interactionTimeCache = Math.round(60 * getSpeedMultiplier());
        return interactionTimeCache;
    }

    public static int getCooldownTime() {
        if (cooldownTimeCache == -1) cooldownTimeCache = Math.round(140 * getSpeedMultiplier());
        return cooldownTimeCache;
    }

    public static float getMovementSpeed() {
        return switch (CONFIG.gollemMovingSpeed) {
            case Fast -> 1.2f;
            case Faster -> 1.5f;
            case Fastest -> 2.0f;
            case null, default -> 1.0f;
        };
    }

    public static int getMaxStackSize() {
        return CONFIG.gollemMaxStackSize;
    }

    public static int getMaxChestsRemembered() {
        return switch (CONFIG.gollemAmountChestRemembered) {
            case Many -> 25;
            case More -> 50;
            case Most -> 120;
            case Extreme -> 1024;
            case null, default -> 10;
        };
    }

    public static int getHorizontalSearchRadius() {
        return switch (CONFIG.gollemSearchRadius) {
            case Large -> 64;
            case Larger -> 80;
            case Largest -> 100;
            case Extreme -> 500;
            case null, default -> 32;
        };
    }

    public static int getVerticalSearchRadius() {
        return switch (CONFIG.gollemSearchRadius) {
            case Large -> 16;
            case Larger -> 25;
            case Largest -> 50;
            case Extreme -> 100;
            case null, default -> 8;
        };
    }

    public static void UpdateTasks() {
        int hori = getHorizontalSearchRadius();
        int verti = getVerticalSearchRadius();
        float speed = getMovementSpeed();

        for (MoveItemsTask task : loadedMoveItemTasks) {
            ((MoveItemsTaskAccessor) task).setHorizontalRange(hori);
            ((MoveItemsTaskAccessor) task).setVerticalRange(verti);
            ((MoveItemsTaskAccessor) task).setSpeed(speed);

            AddTask(task);
        }
    }

    public static void AddTask(MoveItemsTask task) {
        loadedMoveItemTasks.add(task);
    }

    public static void sendConfigToPlayer(ServerPlayerEntity player) {
        ConfigSyncPayload payload = new ConfigSyncPayload(getSpeedMultiplier(), getMovementSpeed(), getInteractionTime(), getCooldownTime(), getMaxStackSize(), getMaxChestsRemembered(), getHorizontalSearchRadius(), getVerticalSearchRadius());
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void onInitialize() {
        CONFIG = ModModConfig.load();
        if (CONFIG.lastModified == 0L) CONFIG.lastModified = ModModConfig.configFile.lastModified();

        ServerTickEvents.START_SERVER_TICK.register(Faster_copper_golem::onServerTick);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;

            // Otherwise the client will crash
            if (ServerPlayNetworking.canSend(player, ConfigSyncPayload.ID)) {
                ConfigSyncPayload payload = new ConfigSyncPayload(getSpeedMultiplier(), getMovementSpeed(), getInteractionTime(), getCooldownTime(), getMaxStackSize(), getMaxChestsRemembered(), getHorizontalSearchRadius(), getVerticalSearchRadius());
                ServerPlayNetworking.send(player, payload);
            }
        });
    }
}
