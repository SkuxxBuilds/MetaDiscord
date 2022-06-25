package com.skuxxbuilds.metadiscord.configuration;

import com.skuxxbuillds.configuration.ConfigurationSection;
import com.skuxxbuillds.configuration.toml.TomlConfiguration;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.user.UserStatus;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

public class DiscordConfiguration extends TomlConfiguration {

    // General
    public ConfigurationSection general() {
        return getSection("general");
    }

    public UserStatus status() {
        return Arrays.stream(UserStatus.values())
                .filter(activityType -> activityType.name().equalsIgnoreCase(general().getString("status")))
                .findFirst()
                .orElse(UserStatus.ONLINE);
    }

    public String token() {
        return general().getString("token");
    }

    // Activity
    public ConfigurationSection activity() {
        return getSection("activity");
    }

    public ActivityType activityType() {
        return Arrays.stream(ActivityType.values())
                .filter(activityType -> activityType.name().equalsIgnoreCase(activity().getString("type")))
                .findFirst()
                .orElse(ActivityType.LISTENING);
    }

    public String activityDescription() {
        return activity().getString("description", "www.skuxxbuilds.com");
    }

    public DiscordConfiguration(File file) {
        super(new File(file, "discord.toml"), Path.of("discord.toml"));
    }
}
