package com.skuxxbuilds.metadiscord.repository;

import com.skuxxbuilds.database.DatabaseService;
import com.skuxxbuilds.metadiscord.MetaDiscord;
import com.skuxxbuilds.metadiscord.model.DiscordPlayer;
import com.skuxxbuilds.redis.Redis;
import org.intellij.lang.annotations.Language;
import org.javacord.api.DiscordApi;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PersistentPlayerRepository implements PlayerRepository {

    private final DatabaseService service;
    private final DiscordApi api;

    private static final String REDIS_FORMAT = "dplayer_%s";

    public PersistentPlayerRepository(MetaDiscord discord) {
        this.service = discord.getDatabaseService();
        this.api = discord.getApi();
    }

    @Override
    public void save(@NotNull DiscordPlayer player) {
        @Language("SQL") String query = """
                INSERT INTO `discord_players` (`uuid`, `name`, `discord_id`) VALUES (?, ?, ?) \
                ON DUPLICATE KEY UPDATE \
                `name`=? \
                `discord_id`=?;
                """;
        service.run(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, player.getName());
                statement.setLong(3, player.getId());
                statement.setString(4, player.getName());
                statement.setLong(5, player.getId());
                statement.executeUpdate();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<DiscordPlayer>> get(@NotNull UUID id) {
        CompletableFuture<Optional<DiscordPlayer>> future = new CompletableFuture<>();

        // Redis
        String redisId = REDIS_FORMAT.formatted(id.toString());
        if (Redis.exists(redisId)) {
            Redis.get(redisId)
                    .ifPresentOrElse(o -> {
                        DiscordPlayer player = (DiscordPlayer) o;
                        api.getUserById(player.getId())
                                .thenAccept(user -> {
                                    player.setUser(user);
                                    future.complete(Optional.of(player));
                                })
                                .exceptionally(throwable -> {
                                    future.completeExceptionally(throwable);
                                    return null;
                                });
                    }, () -> future.complete(Optional.empty()));
            return future;
        }

        // SQL
        @Language("SQL") String query = """
                SELECT `name`, `discord_id` FROM `discord_players` WHERE `uuid`=?;
                """;
        service.run(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, id.toString());
                try (ResultSet rs = statement.executeQuery()) {
                    if (!rs.next()) {
                        future.complete(Optional.empty());
                        return;
                    }
                    long discordId = rs.getLong("discord_id");
                    String name = rs.getString("name");
                    api.getUserById(discordId)
                            .thenAccept(user -> future.complete(Optional.of(new DiscordPlayer(id, name, discordId, user))))
                            .exceptionally(throwable -> {
                                future.completeExceptionally(throwable);
                                return null;
                            });
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> exists(@NotNull UUID id) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        @Language("SQL") String query = """
                SELECT 1 FROM `discord_players` WHERE `uuid`=?;
                """;
        service.run(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    future.complete(resultSet.next());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public boolean isCached(@NotNull UUID id) {
        return !Redis.exists(REDIS_FORMAT.formatted(id.toString()));
    }
}
