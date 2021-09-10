package io.lhjt.minecraft.modules.artifacts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Artifact {
    /**
     * Name of the artifact.
     */
    String name();
}
