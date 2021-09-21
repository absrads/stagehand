package io.lhjt.minecraft.modules.artifacts.instatame;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.Artifact;
import io.lhjt.minecraft.modules.artifacts.BaseArtifact;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "bone.infinite")
public class Infinibone extends BaseArtifact implements Listener {
    protected Material material = Material.BONE;
    protected String name = "bone.infinite";

    public ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Infinibone").color(NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

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

        final var ent = (Wolf) entity;
        final var item = e.getPlayer().getInventory().getItemInMainHand();

        if (!isArtifact(item))
            return;

        e.setCancelled(true);

        ent.setOwner(player);
        ent.playEffect(EntityEffect.WOLF_HEARTS);
    }

    // crafting recipe
    public ShapedRecipe getRecipe() {
        final var plugin = Stagehand.getPlugin(Stagehand.class);

        final var item = createArtifact();
        final var key = new NamespacedKey(plugin, name);
        final var recipe = new ShapedRecipe(key, item);

        recipe.shape("BBB", "SRM", "BBB");
        recipe.setIngredient('B', Material.BONE_MEAL);
        recipe.setIngredient('S', Material.BEEF);
        recipe.setIngredient('R', Material.RABBIT_FOOT);
        recipe.setIngredient('M', Material.MUTTON);
        return recipe;
    }
}
