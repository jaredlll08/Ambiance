package com.blamejared.ambientenvironment.mixin;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeColors.class)
public interface BiomeColorsAccessor {
    
    @Mutable
    @Accessor("GRASS_COLOR_RESOLVER")
    public static void setGrassColorResolver(ColorResolver newResolver) {
        throw new AssertionError();
    }
    @Mutable
    @Accessor("WATER_COLOR_RESOLVER")
    public static void setWaterColorResolver(ColorResolver newResolver) {
        throw new AssertionError();
    }
}
