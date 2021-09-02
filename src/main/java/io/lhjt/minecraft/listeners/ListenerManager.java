package io.lhjt.minecraft.listeners;

import io.lhjt.minecraft.modules.BorderControl;
import io.lhjt.minecraft.modules.RandomSpawn;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerManager implements Listener {
    final private JavaPlugin plugin;

    public ListenerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BorderControl.handleEvent(event, this.plugin);
        RandomSpawn.handleEvent(event, this.plugin);
    }
}
