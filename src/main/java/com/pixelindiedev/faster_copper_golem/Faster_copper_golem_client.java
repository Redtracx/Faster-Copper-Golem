package com.pixelindiedev.faster_copper_golem;

import com.pixelindiedev.faster_copper_golem.config.ClientConfigCache;
import com.pixelindiedev.faster_copper_golem.config.ConfigSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class Faster_copper_golem_client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ID, (payload, context) -> context.client().execute(() -> ClientConfigCache.update(payload.speedMultiplier(), payload.movementSpeed(), payload.interactionTime(), payload.cooldownTime(), payload.maxStackSize(), payload.maxChestsRemembered(), payload.horizontalRange(), payload.verticalRange())));
    }
}
