package io.lhjt.minecraft.modules.artifacts.wavefunc;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.modules.artifacts.Artifact;
import io.lhjt.minecraft.modules.artifacts.BaseArtifact;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "wave.pick")
public class Wavepick extends BaseArtifact implements Listener {
    protected static Material material = Material.DIAMOND_PICKAXE;
    protected static String name = "wave.pick";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final var swordTitle = Component.text("Wavefunction").color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var modifier = new AttributeModifier(UUID.randomUUID(), "generic.attack", 4.00,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);

        meta.addEnchant(Enchantment.DIG_SPEED, 4, true);
        meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);

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
    public void onMine(BlockDamageEvent e) {

        final var item = e.getPlayer().getInventory().getItemInMainHand();

        if (!isArtifact(item))
            return;

        final var blockType = e.getBlock().getType();
        if (Waveshov.materialsList.contains(blockType))
            e.getPlayer().getInventory().setItemInMainHand(Waveshov.createArtifact());
        else if (Waxe.materialsList.contains(blockType))
            e.getPlayer().getInventory().setItemInMainHand(Waxe.createArtifact());
        else
            return;

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        final var item = e.getPlayer().getInventory().getItemInMainHand();

        if (!isArtifact(item))
            return;

        final var blockType = e.getClickedBlock().getType();
        if (Wavehoe.materialsList.contains(blockType))
            e.getPlayer().getInventory().setItemInMainHand(Wavehoe.createArtifact());
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
