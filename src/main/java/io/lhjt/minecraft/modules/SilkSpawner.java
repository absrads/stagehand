package io.lhjt.minecraft.modules;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class SilkSpawner {
    private static String spawnerTypeTag = "stagehand:silkspawner:type";

    public static void handleBreakEvent(BlockBreakEvent event, JavaPlugin plugin) {
        if (event.isCancelled())
            return;

        if (!event.getBlock().getType().equals(Material.SPAWNER))
            return;

        Player p = event.getPlayer();
        if (p.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
            final var spawner = (CreatureSpawner) event.getBlock().getState();
            final var type = spawner.getSpawnedType();

            final ItemStack spawnerItem = new ItemStack(Material.SPAWNER, 1);
            final ItemMeta spawnerMeta = spawnerItem.getItemMeta();

            // Create the item name based on the type of spawner
            final var name = type.name().toLowerCase();
            final var nameCase = name.substring(0, 1).toUpperCase() + name.substring(1) + " Spawner";

            // Disable italics
            final var comp = Component.text().decoration(TextDecoration.ITALIC, false).content(nameCase).build();
            spawnerMeta.displayName(comp);

            spawnerItem.setItemMeta(spawnerMeta);

            final var nbti = new NBTItem(spawnerItem);
            nbti.setString(spawnerTypeTag, type.name()); // set spawner type in NBT

            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), nbti.getItem());
            event.setExpToDrop(0);
        }
    }

    public static void handlePlaceEvent(BlockPlaceEvent event, JavaPlugin plugin) {
        final var block = event.getBlock();
        if (!block.getType().equals(Material.SPAWNER))
            return;

        // Grab the item that was just placed
        final var nbti = new NBTItem(event.getItemInHand());
        if (!nbti.hasKey(spawnerTypeTag))
            return;

        // Convert the string from the NBT to an entity type and set the spawner type
        final var type = EntityType.valueOf(nbti.getString(spawnerTypeTag));
        final var spawner = (CreatureSpawner) block.getState();
        spawner.setSpawnedType(type);
        spawner.update();
    }
}
