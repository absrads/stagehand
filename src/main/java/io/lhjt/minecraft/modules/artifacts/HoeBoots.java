package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "boots.hoe")
public class HoeBoots extends BaseArtifact implements Listener {
    protected static Material material = Material.NETHERITE_BOOTS;
    protected static String name = "boots.hoe";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Tilling Treads").color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("hoes but for your feet lmao").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);
        meta.lore(loreTexts);

        final var modifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 2.00,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier);

        meta.addEnchant(Enchantment.RIPTIDE, 1, true);

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
    public void onPlayerMove(PlayerMoveEvent e) {
        final var player = e.getPlayer();
        final var boots = player.getInventory().getBoots();

        if (!isArtifact(boots))
            return;

        final var locationBlock = player.getLocation().getBlock();
        final var blockBelow = locationBlock.getRelative(BlockFace.DOWN);

        // TODO: clean this implementation up a bit
        List<Pair<Integer, Integer>> list = Arrays.asList(new Pair<>(0, 0), new Pair<>(-1, 1), new Pair<>(0, 1),
                new Pair<>(1, 1), new Pair<>(1, 0), new Pair<>(1, -1), new Pair<>(0, -1), new Pair<>(-1, -1),
                new Pair<>(-1, 0), new Pair<>(-2, 0), new Pair<>(-2, -1), new Pair<>(-2, 1), new Pair<>(2, 0),
                new Pair<>(2, -1), new Pair<>(2, 1), new Pair<>(-1, 2), new Pair<>(0, 2), new Pair<>(1, 2),
                new Pair<>(-1, -2), new Pair<>(0, -2), new Pair<>(1, -2));

        for (Pair<Integer, Integer> pair : list) {
            final var block = blockBelow.getRelative(pair.getValue0(), 0, pair.getValue1());
            if (block.getType() == Material.GRASS_BLOCK || block.getType() == Material.DIRT) {
                block.setType(Material.FARMLAND);
            }

            final var blockAbove = locationBlock.getRelative(pair.getValue0(), 0, pair.getValue1());
            if (blockAbove.getType() == Material.GRASS) {
                blockAbove.breakNaturally();
            }
        }
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
