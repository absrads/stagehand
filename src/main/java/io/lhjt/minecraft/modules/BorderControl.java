package io.lhjt.minecraft.modules;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import io.lhjt.minecraft.Stagehand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class BorderControl implements Listener {
    private JavaPlugin plugin;

    public BorderControl() {
        this.plugin = Stagehand.getPlugin(Stagehand.class);
    }

    @EventHandler
    public void handleEvent(PlayerJoinEvent event) {
        if (!this.plugin.getConfig().getBoolean("enabled"))
            return;

        final double expectedSize = BorderControl.calculateBorderDiameter();
        final double currentSize = Bukkit.getWorlds().get(0).getWorldBorder().getSize();

        if (currentSize < expectedSize) {
            for (final World world : Bukkit.getWorlds()) {
                if (world.getEnvironment() == Environment.NORMAL || world.getEnvironment() == Environment.NETHER) {
                    world.getWorldBorder().setSize(expectedSize, 120);
                }
            }

            final var m = Component.text().append(Component.text("[World Event] ").color(NamedTextColor.DARK_PURPLE))
                    .append(Component.text("The border diameter is now increasing to ")
                            .color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(String.valueOf(expectedSize)).decorate(TextDecoration.BOLD))
                    .append(Component.text(" blocks.")).build();
            this.plugin.getServer().broadcast(m);
        }
    }

    public static double calculateBorderDiameter() {
        final var playersCount = Bukkit.getOfflinePlayers().length - 1;
        return playersCount < 5 ? 2000 : playersCount * 500;
    }
}
