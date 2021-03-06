package com.skuxxbuilds.metadiscord;


import com.skuxxbuilds.database.DatabaseFactory;
import com.skuxxbuilds.database.DatabaseService;
import com.skuxxbuilds.database.model.DatabaseType;
import com.skuxxbuilds.metadiscord.configuration.DiscordConfiguration;
import com.skuxxbuilds.redis.RedisManager;
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

    // Database
    private DatabaseService databaseService;

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
        this.api.setReconnectDelay(attempt -> attempt * 2);
        this.api.updateActivity(configuration.activityType(), configuration.activityDescription());
        this.api.updateStatus(configuration.status());

        // Database
        this.databaseService = new DatabaseService(DatabaseFactory.create(DatabaseType.MARIA_DB));

        // Redis
        RedisManager.enable();
    }

    @Override
    public void terminate() {
        instance = null;

        // Javacord
        this.api.disconnect();

        // Redis
        RedisManager.invalidate();

        // DB
        this.databaseService.unload();
    }

    public static MetaDiscord get() {
        return instance;
    }
}
