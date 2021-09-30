package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "axe.freeze")
public class FreezeAxe extends BaseArtifact implements Listener {
    protected static Material material = Material.DIAMOND_AXE;
    protected static String name = "axe.freeze";
    private static HashMap<UUID, BukkitTask> prevTasks = new HashMap<>();

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Silly Cold Axe").color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("mmm cold monke").color(NamedTextColor.DARK_PURPLE);
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

        meta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);

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
    public void onEntDamgage(EntityDamageByEntityEvent e) {

        final var damager = e.getDamager();
        final var victim = e.getEntity();

        if (!(damager instanceof Player))
            return;

        if (!(victim instanceof Player))
            return;

        final var d = (Player) damager;
        final var v = (Player) victim;

        if (!isArtifact(d.getInventory().getItemInMainHand()))
            return;

        if (v.getFreezeTicks() == 140) {
            v.setFreezeTicks(0);
            v.damage(2);
            v.getWorld().playSound(v.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1.0f, 1.0f);
        } else {
            final var freezeTicks = v.getFreezeTicks();
            v.setFreezeTicks(freezeTicks + 70);
        }

        if (FreezeAxe.prevTasks.containsKey(v.getUniqueId())) {
            FreezeAxe.prevTasks.get(v.getUniqueId()).cancel();
            FreezeAxe.prevTasks.remove(v.getUniqueId());
        }

        final BukkitTask runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (v.getFreezeTicks() == 0) {
                    this.cancel();
                    return;
                }
                final var freezeTicks = v.getFreezeTicks();
                v.setFreezeTicks(freezeTicks);
            }
        }.runTaskTimer(Stagehand.getPlugin(Stagehand.class), 1, 1);

        FreezeAxe.prevTasks.put(v.getUniqueId(), runnable);
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
