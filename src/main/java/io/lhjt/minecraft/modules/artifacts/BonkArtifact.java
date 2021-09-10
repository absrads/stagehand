package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
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
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "bonk")
public class BonkArtifact extends BaseArtifact implements Listener {
    protected static Material material = Material.GOLDEN_SHOVEL;
    protected static String name = "bonk";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Bonk").color(NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("bonk").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);

        meta.lore(loreTexts);

        final var modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 6,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);

        meta.addEnchant(Enchantment.KNOCKBACK, 9, true);

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

        if (!(e.getEntity() instanceof LivingEntity))
            return;

        final var killer = (Player) e.getDamager();
        final var item = killer.getInventory().getItemInMainHand();
        final var victim = (LivingEntity) e.getEntity();

        if (!isArtifact(item))
            return;

        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 2));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2 * 20, 9));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2 * 20, 249));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5 * 20, 0));

        victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_BELL_USE, 100.0f, 1.0f);
        final var msg = Component.text("GET BONKED").decorate(TextDecoration.BOLD).color(NamedTextColor.RED);

        victim.sendActionBar(msg);

        final var randomNumber = Math.random();
        int qty = 0;

        if (randomNumber < 0.05) {
            qty = 3;
        } else if (randomNumber < 0.25) {
            qty = 2;
        } else if (randomNumber < 0.5) {
            qty = 1;
        }

        if (qty > 0) {
            final var stack = new ItemStack(Material.GOLD_NUGGET, qty);
            victim.getWorld().dropItemNaturally(victim.getLocation(), stack);
        }
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
}
