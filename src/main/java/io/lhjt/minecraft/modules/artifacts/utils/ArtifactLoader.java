package io.lhjt.minecraft.modules.artifacts.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import io.lhjt.minecraft.modules.artifacts.Artifact;

public class ArtifactLoader {
    /**
     * Registers each annotated artifact class as a listener with the main plugin.
     *
     * @param pluginManager The plugin manager to register the listeners with.
     * @param plugin        The main plugin.
     */
    public static void registerArtifactEvents(PluginManager pluginManager, JavaPlugin plugin) {
        final var logger = plugin.getLogger();
        var ref = new Reflections("io.lhjt.minecraft.modules");

        for (Class<?> c : ref.getTypesAnnotatedWith(Artifact.class)) {
            try {
                logger.info("Registering artifact: " + c.getAnnotation(Artifact.class).name());
                final var listener = (Listener) c.getDeclaredConstructor().newInstance();
                pluginManager.registerEvents(listener, plugin);
                logger.info(listener.toString());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return A map of artifact name to artifact class.
     */
    public static HashMap<String, Method> getArtifactsMap() {
        var artifacts = new HashMap<String, Method>();
        var ref = new Reflections("io.lhjt.minecraft.modules");

        for (Class<?> c : ref.getTypesAnnotatedWith(Artifact.class)) {
            try {
                artifacts.put(c.getAnnotation(Artifact.class).name(), c.getMethod("createArtifact"));
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }

        return artifacts;
    }

    /**
     * @return a list of the names of all artifacts.
     */
    public static ArrayList<String> getArtifactNames() {
        var artifacts = new ArrayList<String>();
        var ref = new Reflections("io.lhjt.minecraft.modules");

        for (Class<?> c : ref.getTypesAnnotatedWith(Artifact.class)) {
            artifacts.add(c.getAnnotation(Artifact.class).name());
        }

        return artifacts;
    }
}
