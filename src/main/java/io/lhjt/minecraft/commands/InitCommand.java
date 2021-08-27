package io.lhjt.minecraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class InitCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("init"))
            return false;

        // Apply border controls
        // Border controls will apply to both the overworld and nether
        // The end will have border restrictions disabled (i.e. set to the max diameter)
        for (final var world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == org.bukkit.World.Environment.THE_END) {
                world.getWorldBorder().setCenter(new Location(world, 0, 0, 0));
                world.getWorldBorder().setSize(30_000_000, 0);
            } else if (world.getEnvironment() == org.bukkit.World.Environment.NORMAL
                    || world.getEnvironment() == org.bukkit.World.Environment.NETHER) {
                // Check to see if the world has standard world border sizes
                if (world.getWorldBorder().getSize() != 30_000_000) {
                    final var msg = new ComponentBuilder().color(ChatColor.RED).append("The world border size for ")
                            .bold(false).append(world.getName()).bold(true).color(ChatColor.RED)
                            .append(" has been modified; ").bold(false)
                            .append("please reset its world border and run this command again.").create();

                    sender.sendMessage(msg);
                } else {
                    world.getWorldBorder().setCenter(new Location(world, 0, 0, 0));
                    world.getWorldBorder().setSize(2000, 0);
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
    public @Nullable List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        // There is no tab completion for this command at the moment
        return new ArrayList<String>();
    }

}
