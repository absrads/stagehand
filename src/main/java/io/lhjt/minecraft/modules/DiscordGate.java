package io.lhjt.minecraft.modules;

import java.util.UUID;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class DiscordGate {
    public static void handleJoin(PlayerJoinEvent event, JavaPlugin plugin) {
        final UUID uuid = event.getPlayer().getUniqueId();

        // TODO: Validate uuid against account linking service
        final var valid = true;

        if (!valid) {
            // TODO: Replace <placeholder url> with actual verification link
            final var m = Component.text()
                    .append(Component.text("You have not linked your Discord account\nto your Minecraft account yet.")
                            .decorate(TextDecoration.BOLD).color(NamedTextColor.RED))
                    .append(Component.text("\n\n"))
                    .append(Component.text("To link your accounts, please visit the link below:\n\n")
                            .color(NamedTextColor.AQUA))
                    .append(Component.text("<placeholder url>").decorate(TextDecoration.UNDERLINED)).build();

            event.getPlayer().kick(m);
        }
    }
}
