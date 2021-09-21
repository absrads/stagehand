package io.lhjt.minecraft.modules.artifacts.instatame;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.Artifact;
import io.lhjt.minecraft.modules.artifacts.BaseArtifact;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "tame.infinite")
public class Kibble extends BaseArtifact implements Listener {
    protected Material material = Material.GLOW_BERRIES;
    protected String name = "tame.infinite";

    public ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Kibble").color(NamedTextColor.BLUE)
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

        if (!(entity instanceof Wolf || entity instanceof Cat || entity instanceof Parrot))
            return;

        final var ent = (Tameable) entity;
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

        recipe.shape("   ", "BFT", "   ");
        recipe.setIngredient('B', new RecipeChoice.ExactChoice(new Infinibone().createArtifact()));
        recipe.setIngredient('F', new RecipeChoice.ExactChoice(new Infinifish().createArtifact()));
        recipe.setIngredient('T', new RecipeChoice.ExactChoice(new TrailMix().createArtifact()));
        return recipe;
    }

    // stop idiots from eating
    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        if (!isArtifact(e.getItem()))
            return;

        e.setCancelled(true);
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 60, 0));
    }

}
