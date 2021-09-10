package io.lhjt.minecraft.utils;

import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

public class EventLoader {
    /**
     * Get all classes that implement Listener within the project.
     *
     * @return Set of classes that implement Listener.
     */
    private static Set<Class<? extends Listener>> getEvents() {
        Reflections reflector = new Reflections("io.lhjt.minecraft");
        try {
            return reflector.getSubTypesOf(Listener.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registers all event listeners in the given plugin.
     *
     * @param plugin The plugin to register the listeners in.
     */
    public static void loadEvents(JavaPlugin plugin) {
        @NotNull
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        for (Class<? extends Listener> event : getEvents()) {
            try {
                plugin.getLogger().info("Registering listener: " + event.getSimpleName());
                pluginManager.registerEvents(event.getDeclaredConstructor().newInstance(), plugin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
