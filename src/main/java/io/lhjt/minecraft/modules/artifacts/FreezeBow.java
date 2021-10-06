package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.Stagehand;
import io.lhjt.minecraft.modules.artifacts.ingredients.FrozenSoul;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "bow.freeze")
public class FreezeBow extends BaseArtifact implements Listener {
    protected static Material material = Material.BOW;
    protected static String name = "bow.freeze";
    private static HashMap<UUID, BukkitTask> prevTasks = new HashMap<>();

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Mounting Dread").color(NamedTextColor.DARK_AQUA)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("How cruel of you").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);
        loreTexts.add(Component.text(""));
        final var secondLine = Component.text("Legendary Weapon").color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        final var thirdLine = Component.text("- only one legendary weapon").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        final var fourthLine = Component.text("can be equipped at once").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        loreTexts.add(secondLine);
        loreTexts.add(thirdLine);
        loreTexts.add(fourthLine);

        meta.lore(loreTexts);

        meta.addEnchant(Enchantment.ARROW_DAMAGE, 4, true);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

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

        if (!(damager instanceof Arrow))
            return;

        if (!(victim instanceof LivingEntity))
            return;

        final var d = (Arrow) damager;
        final var v = (LivingEntity) victim;
        final var player = d.getShooter();

        if(!(player instanceof Player))
            return;

        final var p = (Player) player;

        if (!isArtifact(p.getInventory().getItemInMainHand())) {
            return;
        }
            
        v.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 5 * 20, 0, false, false));

        if (v.getFreezeTicks() > 137) {
            v.setFreezeTicks(0);
            v.damage(6);
            v.getWorld().playSound(v.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 3.0f, 1.0f);
        } else {
            final var freezeTicks = v.getFreezeTicks();
            v.setFreezeTicks(freezeTicks + 70);
        }
    

        if (FreezeBow.prevTasks.containsKey(v.getUniqueId())) {
            FreezeBow.prevTasks.get(v.getUniqueId()).cancel();
            FreezeBow.prevTasks.remove(v.getUniqueId());
        }

        final BukkitTask runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (v.getFreezeTicks() == 0) {
                    this.cancel();
                    return;
                }
                final var freezeTicks = v.getFreezeTicks();
                p.sendMessage(String.valueOf(freezeTicks));
                v.setFreezeTicks(freezeTicks + 2);
            }
        }.runTaskTimer(Stagehand.getPlugin(Stagehand.class), 1, 1);

        FreezeBow.prevTasks.put(v.getUniqueId(), runnable);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        final var potionEffects = e.getPlayer().getActivePotionEffects();
        var hasLuck = false;
        for (var effect : potionEffects) {
            if (effect.getType().equals(PotionEffectType.LUCK)) {
                hasLuck = true;
            }
        }
        if (!hasLuck)
                e.getPlayer().setFreezeTicks(0);
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

    public static ShapedRecipe getRecipe() {
        final var plugin = Stagehand.getPlugin(Stagehand.class);

        final var item = createArtifact();
        final var key = new NamespacedKey(plugin, name);
        final var recipe = new ShapedRecipe(key, item);

        recipe.shape("   ", "CN ", "   ");
        recipe.setIngredient('C', new RecipeChoice.ExactChoice(FrozenSoul.createArtifact()));
        recipe.setIngredient('N', new RecipeChoice.MaterialChoice(Material.BOW));
        return recipe;
    }

    @EventHandler
    public void drop(EntityDeathEvent e) {
        final var ent = e.getEntity().getType();
        if (!ent.equals(EntityType.STRAY))
            return;

        final var prob = Math.random();
        // 2% chance of dropping on stray death
        if (prob <= 0.02)
            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), createArtifact());
    }
}
