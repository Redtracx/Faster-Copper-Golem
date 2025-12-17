package com.pixelindiedev.faster_copper_golem.config;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ConfigSyncPayload(float speedMultiplier, float movementSpeed, int interactionTime, int cooldownTime, int maxStackSize, int maxChestsRemembered, int horizontalRange, int verticalRange) implements CustomPayload {
    public static final CustomPayload.Id<ConfigSyncPayload> ID = new CustomPayload.Id<>(Identifier.of("faster_copper_golem", "config_sync"));

    public static final PacketCodec<RegistryByteBuf, ConfigSyncPayload> CODEC = PacketCodec.tuple(
                    PacketCodecs.FLOAT, ConfigSyncPayload::speedMultiplier,
                    PacketCodecs.FLOAT, ConfigSyncPayload::movementSpeed,
                    PacketCodecs.VAR_INT, ConfigSyncPayload::interactionTime,
                    PacketCodecs.VAR_INT, ConfigSyncPayload::cooldownTime,
                    PacketCodecs.VAR_INT, ConfigSyncPayload::maxStackSize,
                    PacketCodecs.VAR_INT, ConfigSyncPayload::maxChestsRemembered,
                    PacketCodecs.VAR_INT, ConfigSyncPayload::horizontalRange,
                    PacketCodecs.VAR_INT, ConfigSyncPayload::verticalRange,
                    ConfigSyncPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
