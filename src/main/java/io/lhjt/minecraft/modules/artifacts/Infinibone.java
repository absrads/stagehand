package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "bone.infinite")
public class Infinibone extends BaseArtifact implements Listener {
    protected static Material material = Material.BONE;
    protected static String name = "bone.infinite";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Infinibone").color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Mmm, bone").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);

        artifact.setItemMeta(meta);
        final var nbti = new NBTItem(artifact);
        nbti.setString(LegendaryBase.nameKey, name);

        return nbti.getItem();
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        final var player = e.getPlayer();
        final var entity = e.getRightClicked();

        if (!(entity instanceof Wolf))
            return;

        final var wolf = (Wolf) entity;
        final var item = e.getPlayer().getInventory().getItemInMainHand();

        if (!isArtifact(item))
            return;

        e.setCancelled(true);

        wolf.setOwner(player);
    }

    protected static boolean isArtifact(@Nullable ItemStack stack) {
        if (stack == null)
            return false;

        if (stack.getType() != material)
            return false;

        final var nbti = new NBTItem(stack);
        if (!nbti.hasKey(LegendaryBase.nameKey))
            return false;

        if (!nbti.getString(LegendaryBase.nameKey).equals(name))
            return false;

        return true;
    }
}
