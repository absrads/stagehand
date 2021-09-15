package io.lhjt.minecraft.modules.artifacts.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BaseEffects {
    /**
     * Immobilise a player, preventing movement and obscuring vision.
     *
     * @param p        The player to immobilise.
     * @param duration The duration of the immobilisation.
     */
    public static void blindAndImmobilise(final @NotNull Player p, @NotNull int duration) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 100));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 100));
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 250));
    }
}
