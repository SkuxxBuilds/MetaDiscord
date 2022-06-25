package com.skuxxbuilds.metadiscord.model;

import com.skuxxbuilds.core.model.Nameable;
import com.skuxxbuilds.core.model.Unique;
import com.skuxxbuilds.redis.Redisable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DiscordPlayer implements Unique<UUID>, Nameable, Redisable {

    private final UUID uniqueId;
    private final String name;
    private long id;
    private transient @Nullable User user;
}
