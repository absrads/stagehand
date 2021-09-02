package io.lhjt.minecraft.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
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

import java.util.ArrayList;
import java.util.List;

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

        // Apply border controls
        // Border controls will apply to both the overworld and nether
        // The end will have border restrictions disabled (i.e. set to the max diameter)
        for (final var world : Bukkit.getWorlds()) {
            @NotNull
            WorldBorder wb = world.getWorldBorder();
            if (world.getEnvironment() == Environment.THE_END) {
                wb.setCenter(new Location(world, 0, 0, 0));
                wb.setSize(30_000_000, 0);
            } else if (world.getEnvironment() == Environment.NORMAL || world.getEnvironment() == Environment.NETHER) {
                // Check to see if the world has standard world border sizes
                if (wb.getSize() != 30_000_000) {
                    final var msg = new ComponentBuilder().color(ChatColor.RED).append("The world border size for ")
                            .bold(false).append(world.getName()).bold(true).color(ChatColor.RED)
                            .append(" has been modified; ").bold(false)
                            .append("please reset its world border and run this command again.").create();

                    sender.sendMessage(msg);
                } else {
                    wb.setCenter(new Location(world, 0, 0, 0));
                    wb.setSize(2000, 0);
                }
            }
        }

        // Send a message to the sender indicate the command has been completed
        // successfully.
        final TextComponent component = new TextComponent("World border initialisation complete.");
        component.setColor(ChatColor.GREEN);
        sender.sendMessage(component);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, String[] args) {
        // There is no tab completion for this command at the moment
        return new ArrayList<>();
    }

}
