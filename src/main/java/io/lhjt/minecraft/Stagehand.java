package io.lhjt.minecraft;

import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.permission.ChildPermission;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion.Target;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import io.lhjt.minecraft.commands.CommandManager;
import io.lhjt.minecraft.listeners.ListenerManager;
import io.lhjt.minecraft.modules.RandomSpawn;

@Plugin(name = "stagehand", version = "0.5.2-alpha")
@ApiVersion(value = Target.v1_17)
@Author(value = "lhjt")
@Description(value = "A management plugin to set the scene of the server.")
@Commands({
        @Command(name = "init", desc = "Initialise the stagehand border control system.", usage = "/<command>", permission = "stagehand.bordercontrol", permissionMessage = "§cInsufficient privileges to execute this command.", aliases = {}) })
@Permission(name = "stagehand.bordercontrol", desc = "Allow managing of stagehand border controls", defaultValue = PermissionDefault.FALSE)
@Permission(name = "stagehand.*", desc = "Wildcard permission", defaultValue = PermissionDefault.FALSE, children = {
        @ChildPermission(name = "stagehand.bordercontrol") })
public class Stagehand extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
        this.loadConfig();

        // Register all commands
        CommandManager.registerCommands(this);

        // Register all listeners
        this.getServer().getPluginManager().registerEvents(new ListenerManager(this), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void loadConfig() {
        var config = this.getConfig();

        // Set the defaults
        config.addDefault("enabled", false); // Whether the plugin is enabled

        config.addDefault("spawnRandomizer.attempts", 10);
        config.addDefault("spawnRandomizer.blacklist", RandomSpawn.defaultBlacklist);

        // Discord Gate
        config.addDefault("discordGate.enabled", false);

        config.options().copyDefaults(true);
        this.saveConfig();
    }
}
