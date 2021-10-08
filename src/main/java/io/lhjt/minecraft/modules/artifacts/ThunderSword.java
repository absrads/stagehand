package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "sword.thunder")
public class ThunderSword extends BaseArtifact implements Listener {
    protected static Material material = Material.STONE_SWORD;
    protected static String name = "sword.thunder";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Thunderlord").color(NamedTextColor.BLUE)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("The storm beckons").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);
        loreTexts.add(Component.text(""));
        final var secondLine = Component.text("Legendary Weapon").color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        final var thirdLine = Component.text("- only one piece of legendary").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        final var fourthLine = Component.text("gear can be equipped at once").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        loreTexts.add(secondLine);
        loreTexts.add(thirdLine);
        loreTexts.add(fourthLine);

        meta.lore(loreTexts);

        meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

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
    public void onStrike(LightningStrikeEvent e) {
        final var boltLoc = e.getLightning().getLocation();

        if (e.getCause().equals(LightningStrikeEvent.Cause.TRIDENT)
                || e.getCause().equals(LightningStrikeEvent.Cause.CUSTOM))
            return;

        final var number = Math.random();
        if (!(number <= 0.05))
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                boltLoc.getWorld().dropItemNaturally(boltLoc, ThunderSword.createArtifact());
                boltLoc.getWorld().playSound(boltLoc, Sound.BLOCK_BEACON_POWER_SELECT, 1000.00f, 1.00f);
                for (var player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(Component.text("the air crackles...").color(NamedTextColor.AQUA));
                }
            }
        }.runTaskLater(Stagehand.getPlugin(Stagehand.class), 1 * 20);

        new BukkitRunnable() {
            private int strikes = 0;

            @Override
            public void run() {
                boltLoc.getWorld().spawnEntity(boltLoc, EntityType.LIGHTNING);
                this.strikes += 1;

                if (this.strikes >= 15)
                    this.cancel();
            }
        }.runTaskTimer(Stagehand.getPlugin(Stagehand.class), 0, 1 * 20);
    }

    @EventHandler
    public void fireRes(EntityDamageEvent e) {
        final var item = e.getEntity();

        if (!(item instanceof Item))
            return;

        final var i = (Item) item;
        final var itemStack = i.getItemStack();

        if (!isArtifact(itemStack))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        final var damager = e.getDamager();
        final var victim = e.getEntity();

        final var world = damager.getWorld();
        if (!world.getEnvironment().equals(Environment.NORMAL))
            return;

        if (!(damager instanceof Player))
            return;

        final var d = (Player) damager;

        if (!isArtifact(d.getInventory().getItemInMainHand()))
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                d.getWorld().spawnEntity(victim.getLocation(), EntityType.LIGHTNING);
            }
        }.runTaskLater(Stagehand.getPlugin(Stagehand.class), 1 * 5);

        if (!(d.getWorld().hasStorm()))
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                d.getWorld().spawnEntity(victim.getLocation(), EntityType.LIGHTNING);
            }
        }.runTaskLater(Stagehand.getPlugin(Stagehand.class), 1 * 10);

        new BukkitRunnable() {
            @Override
            public void run() {
                d.getWorld().spawnEntity(victim.getLocation(), EntityType.LIGHTNING);
            }
        }.runTaskLater(Stagehand.getPlugin(Stagehand.class), 1 * 15);
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
