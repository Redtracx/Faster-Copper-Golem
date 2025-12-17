package com.pixelindiedev.faster_copper_golem;

import com.pixelindiedev.faster_copper_golem.config.ClientConfigCache;
import com.pixelindiedev.faster_copper_golem.config.ConfigSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import com.pixelindiedev.faster_copper_golem.screen.GolemFilterScreenHandler;
import com.pixelindiedev.faster_copper_golem.client.gui.GolemFilterScreen;

public class Faster_copper_golem_client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientConfigCache.update(payload.speedMultiplier(), payload.movementSpeed(), payload.interactionTime(),
                        payload.cooldownTime(), payload.maxStackSize(), payload.maxChestsRemembered(),
                        payload.horizontalRange(), payload.verticalRange());
            });
        });

        HandledScreens.register(Faster_copper_golem.GOLEM_FILTER_SCREEN_HANDLER, GolemFilterScreen::new);
    }
}
