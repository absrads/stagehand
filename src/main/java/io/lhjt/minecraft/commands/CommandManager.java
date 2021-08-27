package io.lhjt.minecraft.commands;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CommandManager {
    /**
     * Register all the commands for this plugin.
     *
     * @param plugin
     */
    public static void registerCommands(@NotNull JavaPlugin plugin) {
        plugin.getCommand("init").setExecutor(new InitCommand());
    }
}
