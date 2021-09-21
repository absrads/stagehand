package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

// @Artifact(name = "SwordA", type = ArtifactType.SWORD, level = 1)
@Artifact(name = "sword.a")
public class SwordA extends BaseArtifact implements Listener {
    protected Material material = Material.NETHERITE_SWORD;
    protected String name = "sword.a";

    private static HashMap<UUID, Long> lastKill = new HashMap<>();

    public ItemStack createArtifact() {
        final var swordA = new ItemStack(material);
        final var meta = swordA.getItemMeta();

        final TextComponent swordTitle = Component.text("Penumbral Edge").color(NamedTextColor.DARK_BLUE)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Grahaha, the hunt is on boys.").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);

        meta.lore(loreTexts);

        final var modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 9.00,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);

        // looting 2 enchant
        meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        swordA.setItemMeta(meta);
        final var nbti = new NBTItem(swordA);
        nbti.setString(LegendaryBase.nameKey, name);

        return nbti.getItem();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent eventData) {
        final var player = eventData.getEntity();
        final var killer = player.getKiller();

        if (killer == null)
            return;

        final var inventory = killer.getInventory();
        final var killingItem = inventory.getItemInMainHand();

        if (!isArtifact(killingItem))
            return;

        final var currentTime = System.currentTimeMillis();
        try {
            final var lastKillTime = getLastKillTime(killer);

            // check if last kill this player made was less than 30s ago
            if ((currentTime - lastKillTime) < 30 * 1000) {
                killer.playSound(killer.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 100.0f, 1.0f);
                // remove all the potion effects from the killer
                for (var effect : killer.getActivePotionEffects()) {
                    killer.removePotionEffect(effect.getType());
                }

                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30 * 20, 2, false, false));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 2, false, false));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 15 * 20, 2, false, false));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 1, false, false));

                // TODO: vanish; this is still a sub for vanish
                killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 20, 1, false, false));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30 * 20, 0, false, false));

                setLastKillTime(killer, currentTime);
                return;
            }
        } catch (Exception e) {
            // no last kill time logged
        }

        // set the last kill time
        setLastKillTime(killer, currentTime);

        killer.playSound(killer.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 100.0f, 1.0f);

        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30 * 20, 1, false, false));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 1, false, false));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 15 * 20, 1, false, false));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 0, false, false));

        // TODO: Add vanish effect
        killer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30 * 20, 0, false, false));
    }

    @EventHandler
    public void onHandChange(PlayerItemHeldEvent e) {
        final var player = e.getPlayer();
        final var mainItem = player.getInventory().getItem(e.getNewSlot());

        if (isArtifact(mainItem)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999 * 20, 0, false, false));
        } else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

    private long getLastKillTime(Player player) throws Exception {
        if (lastKill.containsKey(player.getUniqueId())) {
            return lastKill.get(player.getUniqueId());
        }

        throw new Exception("No last kill time found");
    }

    private void setLastKillTime(Player player, long worldTime) {
        lastKill.put(player.getUniqueId(), worldTime);
    }

    // TODO: Handle when item is clicked out of the main hand
    // TODO: Refresh effects on second kill within 30 seconds of first
}
