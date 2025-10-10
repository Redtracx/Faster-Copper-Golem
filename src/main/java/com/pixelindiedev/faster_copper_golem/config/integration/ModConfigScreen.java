package com.pixelindiedev.faster_copper_golem.config.integration;

import com.pixelindiedev.faster_copper_golem.config.InteractionTime;
import com.pixelindiedev.faster_copper_golem.config.RememberCountEnum;
import com.pixelindiedev.faster_copper_golem.config.SearchRadiusEnum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ModConfigScreen extends Screen {
    private final Screen parent;
    private final com.pixelindiedev.faster_copper_golem.config.ModModConfig config;

    protected ModConfigScreen(Screen parent) {
        super(Text.literal("Faster Copper Golem Config"));
        this.parent = parent;
        this.config = com.pixelindiedev.faster_copper_golem.config.ModModConfig.load();
    }

    @Override
    protected void init() {
        int y = height / 4;

        addDrawableChild(ButtonWidget.builder(Text.literal("Interaction time: " + config.gollemInteractionTime), (btn) ->
        {
            InteractionTime[] values = InteractionTime.values();
            int next = (config.gollemInteractionTime.ordinal() + 1) % values.length;
            config.gollemInteractionTime = values[next];
            btn.setMessage(Text.literal("Interaction time: " + config.gollemInteractionTime));
            config.save();
        }).dimensions(width / 2 - 125, y, 250, 20).build());

        y += 25;

        addDrawableChild(new SliderWidget(width / 2 - 125, y, 250, 20, Text.literal("Maximum carry size: " + config.gollemMaxStackSize), (double) (config.gollemMaxStackSize - 16) / (64 - 16)) {
            @Override
            protected void updateMessage() {
                int value = 16 + (int) (this.value * (64 - 16));
                setMessage(Text.literal("Maximum Carry Size: " + value));
            }

            @Override
            protected void applyValue() {
                config.gollemMaxStackSize = 16 + (int) (this.value * (64 - 16));
                config.save();
            }
        });

        y += 25;

        addDrawableChild(ButtonWidget.builder(Text.literal("Search radius: " + config.gollemSearchRadius), (btn) ->
        {
            SearchRadiusEnum[] values = SearchRadiusEnum.values();
            int next = (config.gollemSearchRadius.ordinal() + 1) % values.length;
            config.gollemSearchRadius = values[next];
            btn.setMessage(Text.literal("Search radius: " + config.gollemSearchRadius));
            config.save();
        }).dimensions(width / 2 - 125, y, 250, 20).build());

        y += 25;

        addDrawableChild(ButtonWidget.builder(Text.literal("Movement speed: " + config.gollemMovingSpeed), (btn) ->
        {
            InteractionTime[] values = InteractionTime.values();
            int next = (config.gollemMovingSpeed.ordinal() + 1) % values.length;
            config.gollemMovingSpeed = values[next];
            btn.setMessage(Text.literal("Movement Speed: " + config.gollemMovingSpeed));
            config.save();
        }).dimensions(width / 2 - 125, y, 250, 20).build());

        y += 25;

        addDrawableChild(ButtonWidget.builder(Text.literal("Max amount of chests to check: " + config.gollemAmountChestRemembered), (btn) ->
        {
            RememberCountEnum[] values = RememberCountEnum.values();
            int next = (config.gollemAmountChestRemembered.ordinal() + 1) % values.length;
            config.gollemAmountChestRemembered = values[next];
            btn.setMessage(Text.literal("Max Amount of chests to check: " + config.gollemAmountChestRemembered));
            config.save();
        }).dimensions(width / 2 - 125, y, 250, 20).build());


        y += 30;

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), (btn) -> MinecraftClient.getInstance().setScreen(parent)).dimensions(width / 2 - 100, y, 200, 20).build());
    }

    @Override
    public void close() {
        config.save();
        assert client != null;
        client.setScreen(parent);
    }
}
