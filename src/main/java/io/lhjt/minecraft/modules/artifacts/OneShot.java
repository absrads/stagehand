package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "bow.one.shot")
public class OneShot extends BaseArtifact implements Listener {
    protected static Material material = Material.BOW;
    protected static String name = "bow.one.shot";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Divine Splinter").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("hehe bonk").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);
        loreTexts.add(Component.text(""));
        final var castHeading = Component.text("Cooldown:").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,
                false);
        final var secondLine = Component.text(" 2 minutes").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC,
                false);
        final var thirdLine = Component.text("Legendary Weapon").color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        final var fourthLine = Component.text("- only one legendary").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        final var fifthLine = Component.text("weapon can be equipped at once").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        loreTexts.add(castHeading);
        loreTexts.add(secondLine);
        loreTexts.add(Component.text(""));
        loreTexts.add(thirdLine);
        loreTexts.add(fourthLine);
        loreTexts.add(fifthLine);

        meta.lore(loreTexts);

        meta.addEnchant(Enchantment.FROST_WALKER, 1, true);
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
    public void onShoot(EntityShootBowEvent e) {
        final var shooter = e.getEntity();

        if (!(shooter instanceof Player))
            return;

        final var s = (Player) shooter;

        if (!isArtifact(e.getBow()))
            return;

        final var proj = e.getProjectile();
        final var force = e.getForce();

        if (!(proj instanceof Arrow))
            return;

        final var arrow = (Arrow) proj;

        arrow.setDamage(arrow.getDamage() / 2);
        s.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 120 * 20, 4, false, false));

        if (force == 1) {
            arrow.setGlowing(true);
            arrow.setVisualFire(true);
        } else {
            arrow.setBounce(true);
        }

        new BukkitRunnable() {
            private int crashPrevent = 0;

            @Override
            public void run() {

                proj.setVelocity(proj.getVelocity().multiply(2));
                ;

                crashPrevent++;
                if (crashPrevent == 14)
                    this.cancel();
            };
        }.runTaskTimer(Stagehand.getPlugin(Stagehand.class), (long) 0, (long) (3 * 1 / force));
    }

    @EventHandler
    public void onEntDamgage(EntityDamageByEntityEvent e) {
        final var damager = e.getDamager();
        final var victim = e.getEntity();

        if (!(damager instanceof Arrow))
            return;

        if (!(victim instanceof LivingEntity))
            return;

        final var v = (LivingEntity) victim;
        final var d = (Arrow) damager;
        final var player = d.getShooter();

        if (!(player instanceof Player))
            return;

        final var p = (Player) player;

        if (!isArtifact(p.getInventory().getItemInMainHand()))
            return;

        v.getWorld().spawnEntity(v.getLocation(), EntityType.LIGHTNING);
        v.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 15 * 20, 3));

        p.sendMessage("raw damage:" + String.valueOf(e.getDamage()));
        p.sendMessage("post mitigation damage:" + String.valueOf((int) e.getFinalDamage()));
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {

        final var entity = e.getEntity();

        if (!(entity instanceof Player))
            return;

        final var player = (Player) entity;

        final var potionEffects = player.getActivePotionEffects();
        for (final var potionEffect : potionEffects) {
            if (potionEffect.getType().equals(PotionEffectType.LUCK) && potionEffect.getAmplifier() == 3) {
                return;
            }

            e.setCancelled(true);
            e.setAmount(e.getAmount() * 0.5);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final var player = e.getPlayer();

        final var potionEffects = player.getActivePotionEffects();
        for (final var potionEffect : potionEffects) {
            if (potionEffect.getType().equals(PotionEffectType.LUCK) && potionEffect.getAmplifier() == 4) {
                e.setCancelled(true);
            }
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