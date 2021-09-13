package io.lhjt.minecraft.modules.artifacts.instatame;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Parrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.Artifact;
import io.lhjt.minecraft.modules.artifacts.BaseArtifact;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "seeds.trail")
public class TrailMix extends BaseArtifact implements Listener {
    protected static Material material = Material.PUMPKIN_SEEDS;
    protected static String name = "seeds.trail";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Trail Mix").color(NamedTextColor.GREEN)
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

        if (!(entity instanceof Parrot))
            return;

        final var ent = (Parrot) entity;
        final var item = e.getPlayer().getInventory().getItemInMainHand();

        if (!isArtifact(item))
            return;

        e.setCancelled(true);

        ent.setOwner(player);
        ent.playEffect(EntityEffect.CAT_TAME_SUCCESS);
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        if (!isArtifact(e.getItem()))
            return;

        e.setCancelled(true);
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30 * 15, 0));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!isArtifact(e.getPlayer().getInventory().getItemInMainHand()))
            return;

        final var block = e.getClickedBlock();
        if (block == null)
            return;

        if (!(block.getType().equals(Material.FARMLAND) || block.getType().equals(Material.COMPOSTER)))
            return;

        e.setCancelled(true);
    }

    // crafting recipe
    public static ShapedRecipe getRecipe() {
        final var plugin = Stagehand.getPlugin(Stagehand.class);

        final var item = createArtifact();
        final var key = new NamespacedKey(plugin, name);
        final var recipe = new ShapedRecipe(key, item);

        recipe.shape("WWW", "PBM", "WWW");
        recipe.setIngredient('P', Material.PUMPKIN_SEEDS);
        recipe.setIngredient('B', Material.BEETROOT_SEEDS);
        recipe.setIngredient('M', Material.MELON_SEEDS);
        recipe.setIngredient('W', Material.WHEAT_SEEDS);
        return recipe;
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
