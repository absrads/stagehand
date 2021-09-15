package io.lhjt.minecraft.modules.artifacts.singleuse;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.Artifact;
import io.lhjt.minecraft.modules.artifacts.BaseArtifact;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "escape.rope")
public class EscapeRope extends BaseArtifact implements Listener {
    protected static Material material = Material.LEAD;
    protected static String name = "escape.rope";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Escape Rope").color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Right-click to use B)").color(NamedTextColor.DARK_PURPLE);
        final var secondLine = Component.text("Helpful in caves!").color(NamedTextColor.DARK_PURPLE);
        final var castHeading = Component.text("Cast time:").color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false);
        final var thirdLine = Component.text(" 2 seconds").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC,
                false);
        loreTexts.add(firstLine);
        loreTexts.add(secondLine);
        loreTexts.add(Component.text(""));
        loreTexts.add(castHeading);
        loreTexts.add(thirdLine);

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
    public void onUse(PlayerInteractEvent e) {
        final var p = e.getPlayer();
        if (!isArtifact(p.getInventory().getItemInMainHand()))
            return;

        // Only activate on right click
        final var action = e.getAction();
        if (!(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)))
            return;

        e.setCancelled(true);

        final var world = p.getWorld();
        if (!world.getEnvironment().equals(Environment.NORMAL))
            return;

        final var xCoordinate = (int) p.getLocation().getX();
        final var zCoordinate = (int) p.getLocation().getZ();
        final var yCoordinate = world.getHighestBlockYAt(xCoordinate, zCoordinate);
        final var resultLoc = new Location(world, xCoordinate, yCoordinate + 1, zCoordinate);

        p.getInventory().getItemInMainHand().setAmount(0);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 100));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 100));
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 3 * 20, 250));

        new BukkitRunnable() {
            @Override
            public void run() {
                world.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.00f, 1.00f);
                world.playSound(resultLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.00f, 1.00f);

                p.teleport(resultLoc);
            }
        }.runTaskLater(Stagehand.getPlugin(Stagehand.class), 2 * 20);
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
