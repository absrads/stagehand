package io.lhjt.minecraft.modules.artifacts;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Artifact(name = "tool.honey")
public class HoneyMax extends BaseArtifact implements Listener {
    protected static Material material = Material.STICK;
    protected static String name = "tool.honey";

    public static ItemStack createArtifact() {
        final var artifact = new ItemStack(material);
        final var meta = artifact.getItemMeta();

        final TextComponent swordTitle = Component.text("Honey Fill Tool").color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
        meta.displayName(swordTitle);

        final var loreTexts = new ArrayList<Component>();
        final var firstLine = Component.text("Stinger safety").color(NamedTextColor.DARK_PURPLE);
        loreTexts.add(firstLine);

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
    public void honey(PlayerInteractEvent e) {
        final var item = e.getPlayer().getInventory().getItemInMainHand();
        if (!isArtifact(item))
            return;

        if (e.getClickedBlock() == null) {
            return;
        }
        
        final var block = e.getClickedBlock().getType();
        if (!block.equals(Material.BEEHIVE) && !block.equals(Material.BEE_NEST)) {
            return;
        }
    
        final var blockData = e.getClickedBlock().getBlockData();
        if (!(blockData instanceof Beehive))
            return;

        final var honeyInfo = (Beehive) blockData;
        if (honeyInfo.getHoneyLevel() != honeyInfo.getMaximumHoneyLevel()) {
            honeyInfo.setHoneyLevel(honeyInfo.getMaximumHoneyLevel());
            e.getClickedBlock().setBlockData(honeyInfo);
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
