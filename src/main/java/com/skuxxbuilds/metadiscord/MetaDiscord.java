package com.skuxxbuilds.metadiscord;


import com.skuxxbuilds.metadiscord.configuration.DiscordConfiguration;
import lombok.Getter;
import net.minestom.server.extensions.Extension;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.nio.file.Files;

@Getter
public class MetaDiscord extends Extension {

    private static MetaDiscord instance;

    // Configuration
    private DiscordConfiguration configuration;

    // JavaCord
    private DiscordApi api;

    @Override
    public void preInitialize() {
        instance = this;
    }

    @Override
    public void initialize() {

        // Configuration
        try {
            Files.createDirectory(getDataDirectory());
        } catch (IOException ignored) {
        }
        this.configuration = new DiscordConfiguration(getDataDirectory().toFile());
        this.configuration.load();

        // Javacord
        this.api = new DiscordApiBuilder()
                .setToken(configuration.token())
                .setAllIntents()
                .login()
                .join();
    }

    @Override
    public void terminate() {
        instance = null;
    }

    public static MetaDiscord get() {
        return instance;
    }
}
