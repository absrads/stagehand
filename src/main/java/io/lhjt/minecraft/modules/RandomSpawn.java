package io.lhjt.minecraft.modules;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public final class RandomSpawn {
    private static JavaPlugin plugin;

    public static List<String> defaultBlacklist = Arrays.asList(
        "lava",
        "water",
        "acacia_leaves",
        "azalea_leaves",
        "birch_leaves",
        "dark_oak_leaves",
        "flowering_azalea_leaves",
        "oak_leaves",
        "spruce_leaves"
    );

    public static void handleEvent(PlayerJoinEvent event, JavaPlugin plugin) {
        RandomSpawn.plugin = plugin;
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            teleportPlayer(player);
        }
    }

    private static void teleportPlayer(Player player) {
        World world = Bukkit.getWorlds().get(0);

        int attemptCounter = 0;

        while (attemptCounter < plugin.getConfig().getInt("spawnRandomizer.attempts")) {
            int worldBorder = (int) Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2;
            int xCoordinate = randomNumber(-worldBorder, worldBorder);
            int zCoordinate = randomNumber(-worldBorder, worldBorder);
            int yCoordinate = world.getHighestBlockYAt(xCoordinate, zCoordinate);

            if (!plugin.getConfig().getStringList("spawnRandomizer.blacklist")
                    .contains(new Location(world, xCoordinate, yCoordinate, zCoordinate).getBlock().getType().name()
                            .toLowerCase())) {
                player.teleport(new Location(world, xCoordinate + 0.5, yCoordinate + 1, zCoordinate + 0.5));
                return;
            } else {
                attemptCounter++;
            }
        }

        final TextComponent textComponent = Component.text(
            "We were unable to find you a valid spawn location, please rejoin to try again.", 
            NamedTextColor.RED
        );

        player.kick(textComponent);
    }

    private static int randomNumber(int min, int max) {
        return min + (int) (Math.random() * (double) (max - min + 1));
    }
}
