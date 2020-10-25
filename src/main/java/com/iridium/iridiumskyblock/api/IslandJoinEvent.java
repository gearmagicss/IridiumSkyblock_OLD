package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IslandJoinEvent extends IslandEvent implements Cancellable {
    @Getter @Nullable private final User joinedUser;
    @Getter @Setter private boolean cancelled = false;
    public IslandJoinEvent(@NotNull Island island, @Nullable User joinedUser) {
        super(island);
        this.joinedUser = joinedUser;
    }
}