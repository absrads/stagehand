package io.lhjt.minecraft.modules.artifacts.ingredients;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.modules.artifacts.Artifact;
import io.lhjt.minecraft.modules.artifacts.BaseArtifact;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "ingredient.jelly")
public class RoyalJelly extends BaseArtifact implements Listener {
    protected static Material material = Material.HONEY_BOTTLE;
    protected static String name = "ingredient.jelly";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Royal Jelly").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Goes great with chainmail!").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);

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
    public void drop(PlayerInteractEvent e) {

        if (e.getClickedBlock() == null)
            return;
        
        final var block = e.getClickedBlock().getType();
        if (!block.equals(Material.BEEHIVE) && !block.equals(Material.BEE_NEST))
            return;

        final var blockData = e.getClickedBlock().getBlockData();
        if (!(blockData instanceof Beehive))
            return;

        final var honeyInfo = (Beehive) blockData;
        if (honeyInfo.getHoneyLevel() != honeyInfo.getMaximumHoneyLevel())
            return;

        final var item = e.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() != Material.GLASS_BOTTLE)
            return;


        final var prob = Math.random();
        // 5% chance of adding to player's inventory after using a bottle on a hive
        if (prob <= 0.05) {
            e.getPlayer().getInventory().addItem(createArtifact());
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        if (!isArtifact(e.getItem()))
            return;

        e.setCancelled(true);
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30 * 15, 0));
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