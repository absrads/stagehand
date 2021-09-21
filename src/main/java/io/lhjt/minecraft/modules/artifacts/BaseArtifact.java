package io.lhjt.minecraft.modules.artifacts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import de.tr7zw.nbtapi.NBTItem;
import io.lhjt.minecraft.modules.artifacts.utils.LegendaryBase;

public abstract class BaseArtifact {
    protected Material material;
    protected String name;

    /**
     * Create an instance of this artifact.
     *
     * @return ItemStack containing this artifact.
     */
    public ItemStack createArtifact() {
        return new ItemStack(material);
    }

    /**
     * Validates if the given ItemStack is an artifact.
     *
     * @param stack ItemStack to validate.
     * @return True if the given ItemStack is an artifact.
     */
    protected boolean isArtifact(@Nullable ItemStack stack) {
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
