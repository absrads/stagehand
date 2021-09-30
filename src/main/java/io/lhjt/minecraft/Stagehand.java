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
import io.lhjt.minecraft.modules.RandomSpawn;
import io.lhjt.minecraft.modules.artifacts.BeekeeperHelmet;
import io.lhjt.minecraft.modules.artifacts.MagmaBoots;
import io.lhjt.minecraft.modules.artifacts.instatame.Infinibone;
import io.lhjt.minecraft.modules.artifacts.instatame.Infinifish;
import io.lhjt.minecraft.modules.artifacts.instatame.Kibble;
import io.lhjt.minecraft.modules.artifacts.instatame.TrailMix;
import io.lhjt.minecraft.utils.EventLoader;

@Plugin(name = "stagehand", version = "0.7.0")
@ApiVersion(value = Target.v1_17)
@Author(value = "lhjt")
@Description(value = "A management plugin to set the scene of the server.")
@Commands({
        @Command(name = "init", desc = "Initialise the stagehand border control system.", usage = "/<command>", permission = "stagehand.bordercontrol", permissionMessage = "§cInsufficient privileges to execute this command.", aliases = {}),
        @Command(name = "artifact", desc = "Artifact management interface.", usage = "/<command> give <name>", aliases = {}, permission = "stagehand.artifacts", permissionMessage = "§cInsufficient privileges to execute this command.") })
@Permission(name = "stagehand.bordercontrol", desc = "Allow managing of stagehand border controls", defaultValue = PermissionDefault.FALSE)
@Permission(name = "stagehand.artifacts", desc = "Allow managing of artifacts", defaultValue = PermissionDefault.FALSE)
@Permission(name = "stagehand.*", desc = "Wildcard permission", defaultValue = PermissionDefault.FALSE, children = {
        @ChildPermission(name = "stagehand.bordercontrol"), @ChildPermission(name = "stagehand.artifacts") })
public class Stagehand extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
        this.loadConfig();

        // Register all commands
        CommandManager.registerCommands(this);

        // Register all listeners
        EventLoader.loadEvents(this);

        // Register recipes
        this.getServer().addRecipe(Infinibone.getRecipe());
        this.getServer().addRecipe(Infinifish.getRecipe());
        this.getServer().addRecipe(TrailMix.getRecipe());
        this.getServer().addRecipe(Kibble.getRecipe());
        this.getServer().addRecipe(MagmaBoots.getRecipe());
        this.getServer().addRecipe(BeekeeperHelmet.getRecipe());
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

        config.options().copyDefaults(true);
        this.saveConfig();
    }
}
