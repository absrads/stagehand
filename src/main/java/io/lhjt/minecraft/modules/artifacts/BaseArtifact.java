package io.lhjt.minecraft.modules.artifacts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class BaseArtifact {
    protected static Material material;
    protected static String name;

    /**
     * Create an instance of this artifact.
     *
     * @return ItemStack containing this artifact.
     */
    public static ItemStack createArtifact() {
        return new ItemStack(material);
    }
}
