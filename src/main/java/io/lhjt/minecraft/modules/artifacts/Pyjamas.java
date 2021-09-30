package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "chestplate.pyjamas")
public class Pyjamas extends BaseArtifact implements Listener {
    protected static Material material = Material.LEATHER_CHESTPLATE; // TODO: change colour of tunic
    protected static String name = "chestplate.pyjamas";

    private Set<Material> bedsList = Set.of(Material.RED_BED, Material.BLUE_BED, Material.CYAN_BED, Material.GREEN_BED,
            Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED, Material.LIME_BED, Material.MAGENTA_BED,
            Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED, Material.YELLOW_BED, Material.WHITE_BED,
            Material.GRAY_BED, Material.BROWN_BED, Material.BLACK_BED);

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Pyjamas").color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Sleep tight :)").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);
        loreTexts.add(Component.text(""));
        final var secondLine = Component.text("Legendary Gear").color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        final var thirdLine = Component.text("- only one piece of legendary").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        final var fourthLine = Component.text("gear can be equipped at once").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        loreTexts.add(secondLine);
        loreTexts.add(thirdLine);
        loreTexts.add(fourthLine);

        meta.lore(loreTexts);

        final var modifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 7.00,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier);

        // looting 2 enchant
        meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);

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
    public void onPlayerInteract(PlayerInteractEvent e) {
        final var block = e.getClickedBlock();

        if (block == null)
            return;

        if (!this.bedsList.contains(block.getType()))
            return;

        final var chestplate = e.getPlayer().getInventory().getChestplate();

        if (!isArtifact(chestplate))
            return;

        e.setCancelled(true);
        e.getPlayer().setBedSpawnLocation(block.getLocation());

        final var m = Component.text("Respawn point set.");
        e.getPlayer().sendMessage(m);
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
