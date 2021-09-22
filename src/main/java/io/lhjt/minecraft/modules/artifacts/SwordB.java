package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
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

@Artifact(name = "sword.b")
public class SwordB extends BaseArtifact implements Listener {
    private static Material material = Material.NETHERITE_SWORD;
    private static String name = "sword.b";

    public static ItemStack createArtifact() {
        final var swordB = new ItemStack(material);
        final var meta = swordB.getItemMeta();

        final TextComponent swordTitle = Component.text("B").color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Reap.").color(NamedTextColor.DARK_PURPLE);
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

        swordB.setItemMeta(meta);
        final var nbti = new NBTItem(swordB);
        nbti.setString(LegendaryBase.nameKey, name);

        return nbti.getItem();
    }

    @EventHandler
    public void onEvent(EntityDamageByEntityEvent e) {
        final var plugin = Stagehand.getPlugin(Stagehand.class);
        if (!(e.getDamager() instanceof Player))
            return;

        if (!(e.getEntity() instanceof Damageable))
            return;

        final var player = (Player) e.getDamager();
        final var item = player.getInventory().getItemInMainHand();

        if (!isArtifact(item))
            return;

        final var damageDealt = e.getDamage();
        final var absorptionAmount = damageDealt / 4;

        player.setAbsorptionAmount(player.getAbsorptionAmount() + absorptionAmount);

        new BukkitRunnable() {
            @Override
            public void run() {
                double healthAfter = player.getAbsorptionAmount() - absorptionAmount;
                player.setAbsorptionAmount(healthAfter);
            }
        }.runTaskLater(plugin, 15 * 20);
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
