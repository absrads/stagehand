package io.lhjt.minecraft.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import io.lhjt.minecraft.modules.artifacts.utils.ArtifactLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SummonArtifactCommand implements TabExecutor {

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1)
            return Arrays.asList("give");

        if (args.length == 2) {
            return ArtifactLoader.getArtifactNames();
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player))
            return false;

        final var player = (Player) sender;
        if (args.length == 0)
            return false;

        if (args.length == 1)
            return false;

        if (args.length == 2) {
            final var artifactType = args[1];
            final var artifacts = ArtifactLoader.getArtifactsMap();

            if (artifacts.containsKey(artifactType)) {
                final var artifact = artifacts.get(artifactType);
                try {
                    player.getInventory().addItem((ItemStack) artifact.createArtifact());
                    return true;
                } catch (IllegalArgumentException e) {
                    final var msg = Component.text("Failed to summon artifact: " + artifactType)
                            .color(NamedTextColor.RED);
                    player.sendMessage(msg);
                }
            }
        }

        return false;
    }

}
