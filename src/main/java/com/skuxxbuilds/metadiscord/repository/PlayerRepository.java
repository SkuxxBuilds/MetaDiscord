package com.skuxxbuilds.metadiscord.repository;

import com.skuxxbuilds.metadiscord.model.DiscordPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerRepository {

    void save(@NotNull DiscordPlayer player);

    CompletableFuture<Optional<DiscordPlayer>> get(@NotNull UUID id);

    CompletableFuture<Boolean> exists(@NotNull UUID id);

    boolean isCached(@NotNull UUID id);
}
