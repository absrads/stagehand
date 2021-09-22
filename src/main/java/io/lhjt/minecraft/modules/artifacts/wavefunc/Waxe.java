package io.lhjt.minecraft.modules.artifacts.wavefunc;

import java.util.Set;
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

@Artifact(name = "wave.axe")
public class Waxe extends BaseArtifact implements Listener {
    protected static Material material = Material.DIAMOND_AXE;
    protected static String name = "wave.axe";

    public static Set<Material> materialsList = Set.of(Material.BIRCH_LOG, Material.BIRCH_PLANKS, Material.BIRCH_FENCE,
            Material.BIRCH_FENCE_GATE, Material.BIRCH_SLAB, Material.BIRCH_BOAT, Material.BIRCH_BUTTON,
            Material.BIRCH_LEAVES, Material.BIRCH_SIGN, Material.BIRCH_STAIRS, Material.BIRCH_DOOR,
            Material.BIRCH_WALL_SIGN, Material.BIRCH_WOOD, Material.BIRCH_PRESSURE_PLATE,

            Material.OAK_LOG, Material.OAK_PLANKS, Material.OAK_FENCE, Material.OAK_FENCE_GATE, Material.OAK_SLAB,
            Material.OAK_BOAT, Material.OAK_BUTTON, Material.OAK_LEAVES, Material.OAK_SIGN, Material.OAK_STAIRS,
            Material.OAK_DOOR, Material.OAK_WALL_SIGN, Material.OAK_WOOD, Material.OAK_PRESSURE_PLATE,

            Material.SPRUCE_LOG, Material.SPRUCE_PLANKS, Material.SPRUCE_FENCE, Material.SPRUCE_FENCE_GATE,
            Material.SPRUCE_SLAB, Material.SPRUCE_BOAT, Material.SPRUCE_BUTTON, Material.SPRUCE_LEAVES,
            Material.SPRUCE_SIGN, Material.SPRUCE_STAIRS, Material.SPRUCE_DOOR, Material.SPRUCE_WALL_SIGN,
            Material.SPRUCE_WOOD, Material.SPRUCE_PRESSURE_PLATE,

            Material.JUNGLE_LOG, Material.JUNGLE_PLANKS, Material.JUNGLE_FENCE, Material.JUNGLE_FENCE_GATE,
            Material.JUNGLE_SLAB, Material.JUNGLE_BOAT, Material.JUNGLE_BUTTON, Material.JUNGLE_LEAVES,
            Material.JUNGLE_SIGN, Material.JUNGLE_STAIRS, Material.JUNGLE_DOOR, Material.JUNGLE_WALL_SIGN,
            Material.JUNGLE_WOOD, Material.JUNGLE_PRESSURE_PLATE,

            Material.DARK_OAK_LOG, Material.DARK_OAK_PLANKS, Material.DARK_OAK_FENCE, Material.DARK_OAK_FENCE_GATE,
            Material.DARK_OAK_SLAB, Material.DARK_OAK_BOAT, Material.DARK_OAK_BUTTON, Material.DARK_OAK_LEAVES,
            Material.DARK_OAK_SIGN, Material.DARK_OAK_STAIRS, Material.DARK_OAK_DOOR, Material.DARK_OAK_WALL_SIGN,
            Material.DARK_OAK_WOOD, Material.DARK_OAK_PRESSURE_PLATE,

            Material.ACACIA_LOG, Material.ACACIA_PLANKS, Material.ACACIA_FENCE, Material.ACACIA_FENCE_GATE,
            Material.ACACIA_SLAB, Material.ACACIA_BOAT, Material.ACACIA_BUTTON, Material.ACACIA_LEAVES,
            Material.ACACIA_SIGN, Material.ACACIA_STAIRS, Material.ACACIA_DOOR, Material.ACACIA_WALL_SIGN,
            Material.ACACIA_WOOD, Material.ACACIA_PRESSURE_PLATE,

            Material.CRAFTING_TABLE, Material.FLETCHING_TABLE, Material.CACTUS);

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
        else if (!Waveshov.materialsList.contains(blockType) && !materialsList.contains(blockType))
            e.getPlayer().getInventory().setItemInMainHand(Wavepick.createArtifact());
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
