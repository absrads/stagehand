package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.javatuples.Pair;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "boots.magma")
public class MagmaBoots extends BaseArtifact implements Listener {
    protected Material material = Material.NETHERITE_BOOTS;
    protected String name = "boots.magma";

    public ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Magma Treads").color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Comfy!").color(NamedTextColor.DARK_PURPLE);
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

        final var modifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 4.00,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier);

        // looting 2 enchant
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
    public void onPlayerMove(PlayerMoveEvent e) {
        final var plugin = Stagehand.getPlugin(Stagehand.class);
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
            if (block.getType() == Material.LAVA) {
                block.setType(Material.BASALT);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.05f, 1.0f);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(Material.LAVA);
                    }
                }.runTaskLater(plugin, 10 * 20);
            }
        }
    }
}
