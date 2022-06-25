package com.skuxxbuilds.metadiscord.configuration;

import com.skuxxbuillds.configuration.toml.TomlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

public class DiscordConfiguration extends TomlConfiguration {

    public String token() {
        return getString("token");
    }

    public DiscordConfiguration(File file) {
        super(new File(file, "discord.toml"), Path.of("discord.toml"));
    }
}
