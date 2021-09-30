package io.lhjt.minecraft.modules.artifacts.ingredients;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.modules.artifacts.Artifact;
import io.lhjt.minecraft.modules.artifacts.BaseArtifact;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "ingredient.magma.crystal")
public class LavaCrystalliser extends BaseArtifact implements Listener {
    protected static Material material = Material.AMETHYST_SHARD;
    protected static String name = "ingredient.magma.crystal";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Lava Crystalliser").color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Would fit well into").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);
        final var secondLine = Component.text("netherite boots").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(secondLine);

        meta.lore(loreTexts);

        meta.addEnchant(Enchantment.FROST_WALKER, 1, true);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        artifact.setItemMeta(meta);
        final var nbti = new NBTItem(artifact);
        nbti.setString(LegendaryBase.nameKey, name);

        return nbti.getItem();
    }
    
    @EventHandler
    public void drop(EntityDeathEvent e) {
        final var ent = e.getEntity().getType();
        if (!ent.equals(EntityType.MAGMA_CUBE))
            return;

        final var prob = Math.random();
        // 1% chance of dropping on magma cube death
        if (prob <= 0.01)
            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), createArtifact());
    }
}