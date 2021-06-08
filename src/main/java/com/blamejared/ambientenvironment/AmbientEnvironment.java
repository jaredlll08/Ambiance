package com.blamejared.ambientenvironment;

import com.blamejared.ambientenvironment.mixin.BiomeColorsAccessor;
import net.fabricmc.api.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.levelgen.SimpleRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class AmbientEnvironment implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        
        final var grassColor = BiomeColors.GRASS_COLOR_RESOLVER;
        final var waterColor = BiomeColors.WATER_COLOR_RESOLVER;
        
        final var levels = 2;
        final var NOISE_GRASS = new PerlinSimplexNoise(new SimpleRandomSource("NOISE_GRASS".hashCode()), IntStream.rangeClosed(0, levels));
        final var NOISE_WATER = new PerlinSimplexNoise(new SimpleRandomSource("NOISE_WATER".hashCode()), IntStream.rangeClosed(0, levels));
        
        BiomeColorsAccessor.setGrassColorResolver((biome, posX, posZ) -> {
            final var newColor = grassColor.getColor(biome, posX, posZ);
            final var scale = 8f;
            var value = ((NOISE_GRASS.getValue(posX / scale, posZ / scale, false)));
            final var darkness = 0.25f;
            value = curve(0, 1, remap(value, -((1 << levels) - 1), (1 << levels) - 1, 0, 1)) * darkness;
            return blend(newColor, 0, (float) (value));
        });
        
        BiomeColorsAccessor.setWaterColorResolver((biome, posX, posZ) -> {
            final var newColor = waterColor.getColor(biome, posX, posZ);
            final var scale = 16f;
            var value = ((NOISE_WATER.getValue(posX / scale, posZ / scale, false)));
            final var darkness = 0.3f;
            value = curve(0, 1, remap(value, -((1 << levels) - 1), (1 << levels) - 1, 0, 1)) * darkness;
            return blend(newColor, 0, (float) (value));
        });
    }
    
    public static double remap(final double value, final double currentLow, final double currentHigh, final double newLow, final double newHigh) {
        return newLow + (value - currentLow) * (newHigh - newLow) / (currentHigh - currentLow);
    }
    
    private static float getRed(final int hex) {
        return ((hex >> 16) & 0xFF) / 255f;
    }
    
    private static float getGreen(final int hex) {
        return ((hex >> 8) & 0xFF) / 255f;
    }
    
    private static float getBlue(final int hex) {
        return ((hex) & 0xFF) / 255f;
    }
    
    private static float getAlpha(final int hex) {
        return ((hex >> 24) & 0xff) / 255f;
    }
    
    private static float[] getARGB(final int hex) {
        return new float[] {getAlpha(hex), getRed(hex), getGreen(hex), getBlue(hex)};
    }
    
    private static int toInt(final float[] argb) {
        final var r = (int) Math.floor(argb[1] * 255) & 0xFF;
        final var g = (int) Math.floor(argb[2] * 255) & 0xFF;
        final var b = (int) Math.floor(argb[3] * 255) & 0xFF;
        final var a = (int) Math.floor(argb[0] * 255) & 0xFF;
        return (a << 24) + (r << 16) + (g << 8) + (b);
    }
    
    public static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(value, max));
    }
    
    public static double curve(final double start, final double end, double amount) {
        amount = clamp(amount, 0, 1);
        amount = clamp((amount - start) / (end - start), 0, 1);
        return clamp(0.5 + 0.5 * Math.sin(Math.cos(Math.PI * Math.tan(90 * amount))) * Math.cos(Math.sin(Math.tan(amount))), 0, 1);
    }
    
    public static int blend(final int color1, final int color2, final float ratio) {
        final var ir = 1.0f - ratio;
        
        final var rgb1 = getARGB(color2);
        final var rgb2 = getARGB(color1);
        
        return toInt(new float[] {rgb1[0] * ratio + rgb2[0] * ir, rgb1[1] * ratio + rgb2[1] * ir, rgb1[2] * ratio + rgb2[2] * ir, rgb1[3] * ratio + rgb2[3] * ir});
        
    }
    
    
}
