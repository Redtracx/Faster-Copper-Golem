package com.pixelindiedev.faster_copper_golem.config;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ConfigSyncPayload(float speedMultiplier, float movementSpeed, int interactionTime, int cooldownTime,
                                int maxStackSize, int maxChestsRemembered, int horizontalRange,
                                int verticalRange) implements CustomPayload {

    public static final CustomPayload.Id<ConfigSyncPayload> ID = new CustomPayload.Id<>(Identifier.of("faster_copper_golem", "config_sync"));

    public static final PacketCodec<PacketByteBuf, ConfigSyncPayload> CODEC = PacketCodec.of(ConfigSyncPayload::write, ConfigSyncPayload::read);

    private static ConfigSyncPayload read(PacketByteBuf buf) {
        return new ConfigSyncPayload(buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    private void write(PacketByteBuf buf) {
        buf.writeFloat(speedMultiplier);
        buf.writeFloat(movementSpeed);
        buf.writeInt(interactionTime);
        buf.writeInt(cooldownTime);
        buf.writeInt(maxStackSize);
        buf.writeInt(maxChestsRemembered);
        buf.writeInt(horizontalRange);
        buf.writeInt(verticalRange);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
