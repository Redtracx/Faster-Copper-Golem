package com.pixelindiedev.faster_copper_golem.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "faster_copper_golem.json";
    public static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), FILE_NAME);
    private static final Logger LOGGER = LoggerFactory.getLogger("fasterCopperGolem");
    public InteractionTime gollemInteractionTime = InteractionTime.Fast;
    public int gollemMaxStackSize = 24;
    public RememberCountEnum gollemAmountChestRemembered = RememberCountEnum.Many;
    public SearchRadiusEnum gollemSearchRadius = SearchRadiusEnum.Vanilla;
    public InteractionTime gollemMovingSpeed = InteractionTime.Vanilla;
    // MODIFIED: Added smartSorting field
    public boolean smartSorting = true;
    // MODIFIED: Added specific sorting options
    public boolean frameSorting = true;
    public boolean tagSorting = true;
    // MODIFIED: Added power user naming option
    public boolean nameSorting = true;
    public transient long lastModified = 0L;

    public static com.pixelindiedev.faster_copper_golem.config.ModModConfig load() {
        ModModConfig config = new ModModConfig();
        JsonObject obj = new JsonObject();
        boolean changed = false;

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonObject())
                    obj = element.getAsJsonObject();
            } catch (IOException e) {
                LOGGER.error("Failed to read config, restoring defaults.", e);
                config = new com.pixelindiedev.faster_copper_golem.config.ModModConfig();
            }
        } else {
            LOGGER.warn("Config file not found, creating a new one.");
            config = new com.pixelindiedev.faster_copper_golem.config.ModModConfig();
            changed = true;
        }

        // Check for missing options
        if (!obj.has("gollemInteractionTime")) {
            LOGGER.warn("Missing option 'gollemInteractionTime', adding default (Fast).");
            obj.addProperty("gollemInteractionTime", InteractionTime.Fast.name());
            changed = true;
        }
        if (!obj.has("gollemMaxStackSize")) {
            LOGGER.warn("Missing option 'gollemMaxStackSize', adding default (24).");
            obj.addProperty("gollemMaxStackSize", 24);
            changed = true;
        }
        if (!obj.has("gollemAmountChestRemembered")) {
            LOGGER.warn("Missing option 'gollemAmountChestRemembered', adding default (Many).");
            obj.addProperty("gollemAmountChestRemembered", RememberCountEnum.Many.name());
            changed = true;
        }
        if (!obj.has("gollemSearchRadius")) {
            LOGGER.warn("Missing option 'gollemSearchRadius', adding default (Vanilla).");
            obj.addProperty("gollemSearchRadius", SearchRadiusEnum.Vanilla.name());
            changed = true;
        }
        if (!obj.has("gollemMovingSpeed")) {
            LOGGER.warn("Missing option 'gollemMovingSpeed', adding default (Vanilla).");
            obj.addProperty("gollemMovingSpeed", InteractionTime.Vanilla.name());
            changed = true;
        }
        // MODIFIED: Added check for smartSorting
        if (!obj.has("smartSorting")) {
            LOGGER.warn("Missing option 'smartSorting', adding default (true).");
            obj.addProperty("smartSorting", true);
            changed = true;
        }
        // MODIFIED: Added checks for new sorting options
        if (!obj.has("frameSorting")) {
            LOGGER.warn("Missing option 'frameSorting', adding default (true).");
            obj.addProperty("frameSorting", true);
            changed = true;
        }
        if (!obj.has("tagSorting")) {
            LOGGER.warn("Missing option 'tagSorting', adding default (true).");
            obj.addProperty("tagSorting", true);
            changed = true;
        }
        // MODIFIED: Added check for nameSorting
        if (!obj.has("nameSorting")) {
            LOGGER.warn("Missing option 'nameSorting', adding default (true).");
            obj.addProperty("nameSorting", true);
            changed = true;
        }

        config = GSON.fromJson(obj, com.pixelindiedev.faster_copper_golem.config.ModModConfig.class);

        if (changed) {
            config.save();
        }

        config.lastModified = configFile.lastModified();

        return config;
    }

    public void save() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), FILE_NAME);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
            lastModified = configFile.lastModified();
        } catch (IOException e) {
            LOGGER.error("Failed to save config:", e);
        }
    }

    public boolean hasExternalChange() {
        return configFile.exists() && configFile.lastModified() != lastModified;
    }
}
