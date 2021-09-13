package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
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

@Artifact(name = "helmet.beekeeper")
public class BeekeeperHelmet extends BaseArtifact implements Listener {
    protected static Material material = Material.CHAINMAIL_HELMET;
    protected static String name = "helmet.beekeeper";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Beekeeper Veil").color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Stinger safety").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);

        meta.lore(loreTexts);

        final var modifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 2.00,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier);

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
    public void onEvent(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player))
            return;

        if (!(e.getEntity() instanceof Bee))
            return;

        final var player = (Player) e.getDamager();

        if (!isArtifact(player.getInventory().getHelmet()))
            return;

        e.setCancelled(true);
    }

    @EventHandler // prevents bees from aggroing against players with the veil
    public void onEntityTarget(EntityTargetEvent e) {
        final var ent = e.getEntity();

        if (!(ent instanceof Bee))
            return;

        final var target = e.getTarget();
        if (!(target instanceof Player))
            return;

        final var player = (Player) target;

        if (!isArtifact(player.getInventory().getHelmet()))
            return;

        final var bee = (Bee) ent;
        bee.setAnger(0);
        bee.setTarget(null);
    }

    @EventHandler // safe bottling + shearing of honey from hives
    public void onPlayerInteract(PlayerInteractEvent e) {
        final var block = e.getClickedBlock();
        final var blockData = block.getBlockData();
        if (!(blockData instanceof Beehive))
            return;

        final var player = e.getPlayer();
        final var itemInHand = player.getInventory().getItemInMainHand();

        if (!isArtifact(player.getInventory().getHelmet()))
            return;

        e.setCancelled(true);
        final var hive = (Beehive) blockData;

        if (hive.getHoneyLevel() < 5)
            return;

        if (itemInHand.getType() == Material.GLASS_BOTTLE) {
            // decrement the number of glass bottles in the player's hand
            itemInHand.setAmount(itemInHand.getAmount() - 1);

            // add a bottle of honey to the player's inventory
            player.getInventory().setItemInMainHand(itemInHand);
            player.getInventory().addItem(new ItemStack(Material.HONEY_BOTTLE));

            // play bottling sound to make it seem normal
            player.getWorld().playSound(block.getLocation(), Sound.ITEM_BOTTLE_FILL, 1.00f, 1.00f);

            // reset the honey level of the hive and save it to the block
            hive.setHoneyLevel(0);
            block.setBlockData(hive);
        } else if (itemInHand.getType() == Material.SHEARS) {
            // drop the honeycomb from the hive
            block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.HONEYCOMB, 3));

            // decrement durability of the shears
            final var meta = (org.bukkit.inventory.meta.Damageable) itemInHand.getItemMeta();
            meta.setDamage(meta.getDamage() + 1);
            itemInHand.setItemMeta(meta);

            // play sound
            player.getWorld().playSound(block.getLocation(), Sound.BLOCK_BEEHIVE_SHEAR, 1.00f, 1.00f);

            // reset the honey level of the hive and save it to the block
            hive.setHoneyLevel(0);
            block.setBlockData(hive);
        } else {
            return;
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
