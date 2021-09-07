package io.lhjt.minecraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import io.lhjt.minecraft.modules.BorderControl;
import io.lhjt.minecraft.modules.DiscordGate;
import io.lhjt.minecraft.modules.RandomSpawn;
import io.lhjt.minecraft.modules.SilkSpawner;

public class ListenerManager implements Listener {
    final private JavaPlugin plugin;

    public ListenerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DiscordGate.handleJoin(event, this.plugin);
        BorderControl.handleEvent(event, this.plugin);
        RandomSpawn.handleEvent(event, this.plugin);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        SilkSpawner.handleBreakEvent(event, this.plugin);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        SilkSpawner.handlePlaceEvent(event, this.plugin);
    }
}
