package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "sword.c")
public class SwordC extends BaseArtifact implements Listener {
    protected static Material material = Material.NETHERITE_SWORD;
    protected static String name = "sword.c";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Sword C").color(NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("sex").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);

        meta.lore(loreTexts);

        final var modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 7.00,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);

        // looting 2 enchant
        meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        artifact.setItemMeta(meta);
        final var nbti = new NBTItem(artifact);
        nbti.setString(LegendaryBase.nameKey, name);

        return nbti.getItem();
    }

    /**
     * Validates if the given ItemStack is an artifact.
     *
     * @param stack ItemStack to validate.
     * @return True if the given ItemStack is an artifact.
     */
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

    @EventHandler
    public void onEvent(EntityDamageByEntityEvent e) {
        final var plugin = Stagehand.getPlugin(Stagehand.class);
        if (!(e.getDamager() instanceof Player))
            return;

        if (!(e.getEntity() instanceof Player))
            return;

        final var player = (Player) e.getDamager();
        final var item = player.getInventory().getItemInMainHand();

        if (!isArtifact(item))
            return;

        var hasModified = false;
        final var d = 5 * 20;

        for (var effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                int amplifier = effect.getAmplifier();
                if (amplifier == 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, d, 1));
                }
                hasModified = true;
                // new BukkitRunnable() {
                // @Override
                // public void run() {
                // for (var effect : player.getActivePotionEffects()) {
                // int amp2 = effect.getAmplifier();
                // }
                // if (amplifier == 1) {
                // player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, 2));
                // player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, d, 1));
                // hasModified = true;
                // } else if (amplifier == 2 || amplifier == 3) {
                // player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, 3));
                // player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, d, 1));
                // hasModified = true;
                // }
                // }
                // };
            }
        }
        ;

        if (!hasModified) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, d, 0));
            // 0-1-1-1
            // 1-0-0-0 (it isn't symmetrical)
        }

        // Scale down the levels over time
        // new BukkitRunnable() {

        // @Override
        // public void run() {
        // for (var effect : player.getActivePotionEffects()) {
        // int amplifier = effect.getAmplifier();
        // if (effect.getType().equals(PotionEffectType.SPEED)) {
        // if (amplifier == 3) {
        // player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, amplifier
        // - 1));
        // player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, d, amplifier -
        // 2));
        // } else if (amplifier == 2) {
        // player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, amplifier
        // - 1));
        // player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, d, amplifier -
        // 2));
        // } else if (amplifier == 1) {
        // player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, amplifier
        // - 1));
        // player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, d, amplifier -
        // 3));
        // } else {
        // this.cancel();
        // }
        // } // braces be like
        // }
        // }
        // }.runTaskTimer(plugin, d + 1, d + 1);
    }
}
