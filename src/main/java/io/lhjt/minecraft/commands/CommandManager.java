package io.lhjt.minecraft.commands;

import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {
    /**
     * Register all the commands for this plugin.
     *
     * @param plugin
     */
    public static void registerCommands(JavaPlugin plugin) {
        plugin.getCommand("init").setExecutor(new InitCommand());
    }
}
