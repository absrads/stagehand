package io.lhjt.minecraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.lhjt.minecraft.modules.BorderControl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class InitCommand implements TabExecutor {
    private final JavaPlugin plugin;

    public InitCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            String[] args) {
        // Set config to enabled if it isn't already
        plugin.getConfig().set("enabled", true);
        plugin.saveConfig();

        final var prefix = Component.text("[Stagehand::BC] ").color(NamedTextColor.LIGHT_PURPLE);

        final var playersCount = Bukkit.getOfflinePlayers().length - 1;
        var m = Component.text().append(prefix).color(NamedTextColor.AQUA).append(Component.text("Total players: "))
                .append(Component.text(String.valueOf(playersCount)).decorate(TextDecoration.BOLD)).build();
        sender.sendMessage(m);

        final var size = BorderControl.calculateBorderDiameter();
        m = Component.text().append(prefix).color(NamedTextColor.AQUA)
                .append(Component.text("Setting border diameter to "))
                .append(Component.text().decorate(TextDecoration.BOLD)
                        .content(String.valueOf(size) + " (± " + (size / 2) + ")"))
                .append(Component.text(" blocks.")).build();
        sender.sendMessage(m);

        // Apply border controls
        // Border controls will apply to both the overworld and nether
        // The end will have border restrictions disabled (i.e. set to the max diameter)
        for (final var world : Bukkit.getWorlds()) {
            WorldBorder wb = world.getWorldBorder();
            Environment env = world.getEnvironment();
            if (env == Environment.THE_END) {
                wb.setCenter(new Location(world, 0, 0, 0));
                wb.setSize(30_000_000, 0);
            } else if (env == Environment.NORMAL || env == Environment.NETHER) {
                wb.setCenter(new Location(world, 0, 0, 0));
                wb.setSize(size, 0);
            }
        }

        // Send a message to the sender indicate the command has been completed
        // successfully.
        m = Component.text().append(prefix).color(NamedTextColor.GREEN).append(Component.text("Operation succeeded."))
                .build();
        sender.sendMessage(m);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, String[] args) {
        // There is no tab completion for this command at the moment
        return new ArrayList<>();
    }

}
