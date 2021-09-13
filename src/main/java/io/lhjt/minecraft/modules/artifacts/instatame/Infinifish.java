package io.lhjt.minecraft.modules.artifacts.instatame;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

@Artifact(name = "fish.infinite")
public class Infinifish extends BaseArtifact implements Listener {
    protected static Material material = Material.SALMON;
    protected static String name = "fish.infinite";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Infinifish").color(NamedTextColor.GREEN)
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

        if (!(entity instanceof Cat))
            return;

        final var ent = (Cat) entity;
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

    // crafting recipe
    public static ShapedRecipe getRecipe() {
        final var plugin = Stagehand.getPlugin(Stagehand.class);

        final var item = createArtifact();
        final var key = new NamespacedKey(plugin, name);
        final var recipe = new ShapedRecipe(key, item);

        recipe.shape("RAP", "CSD", "MMM");
        recipe.setIngredient('R', Material.RABBIT_STEW);
        recipe.setIngredient('A', Material.AXOLOTL_BUCKET);
        recipe.setIngredient('P', Material.PUFFERFISH);
        recipe.setIngredient('C', Material.TROPICAL_FISH);
        recipe.setIngredient('S', Material.SALMON);
        recipe.setIngredient('D', Material.COD);
        recipe.setIngredient('M', Material.MILK_BUCKET);
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
