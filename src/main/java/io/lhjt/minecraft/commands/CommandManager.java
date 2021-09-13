package io.lhjt.minecraft.commands;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CommandManager {
    /**
     * Register all the commands for this plugin.
     *
     * @param plugin The instance of the JavaPlugin the commandManager is linked to.
     */
    public static void registerCommands(@NotNull JavaPlugin plugin) {
        plugin.getCommand("init").setExecutor(new InitCommand(plugin));
        plugin.getCommand("artifact").setExecutor(new SummonArtifactCommand());
    }
}
