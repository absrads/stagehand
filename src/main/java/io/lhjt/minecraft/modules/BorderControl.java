package io.lhjt.minecraft.modules;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BorderControl {
    final public static void handleEvent(PlayerJoinEvent event, JavaPlugin plugin) {
        if (!plugin.getConfig().getBoolean("enabled"))
            return;

        final var expectedSize = BorderControl.calculateBorderDiameter();
        final var currentSize = Bukkit.getWorld("world").getWorldBorder().getSize();

        if (currentSize != expectedSize) {
            for (final var world : Bukkit.getWorlds()) {
                if (world.getEnvironment() == Environment.NORMAL || world.getEnvironment() == Environment.NETHER) {
                    world.getWorldBorder().setSize(expectedSize, 120);
                }
            }

            var msg = new ComponentBuilder().append("[World Event] ").color(ChatColor.DARK_PURPLE)
                    .append("The border diameter is now increasing to ").color(ChatColor.LIGHT_PURPLE).bold(false)
                    .append(String.valueOf(expectedSize)).bold(true).append(" blocks.").bold(false).create();
            plugin.getServer().broadcast(msg);
        }
    }

    final private static double calculateBorderDiameter() {
        final var playersCount = Bukkit.getOfflinePlayers().length - 1;
        return playersCount < 5 ? 2000 : playersCount * 500;
    }
}
