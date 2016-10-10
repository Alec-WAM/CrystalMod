package com.alec_wam.CrystalMod.util;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReflectionWrapper
{
    public static final ReflectionWrapper instance;
    private Field mapRegSprites;
    
    public ReflectionWrapper() {
        this.mapRegSprites = null;
    }
    
    private Field findField(Class<?> clz, final String... methods) throws Exception {
        while (clz != null && clz != Object.class) {
            for (final String name : methods) {
                try {
                    final Field f = clz.getDeclaredField(name);
                    if (f != null) {
                        return f;
                    }
                }
                catch (Exception ex) {}
            }
            clz = clz.getSuperclass();
        }
        throw new Exception("Unable to find field " + methods[0]);
    }
    
    @SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
    public Map<String, TextureAtlasSprite> getRegSprite(final TextureMap map) {
        try {
            if (this.mapRegSprites == null) {
                this.mapRegSprites = this.findField(map.getClass(), "mapRegisteredSprites", "field_110574_e");
            }
            this.mapRegSprites.setAccessible(true);
            return (Map<String, TextureAtlasSprite>)this.mapRegSprites.get(map);
        }
        catch (Throwable t) {
            this.notifyDeveloper(t);
            return null;
        }
    }
    
    private void notifyDeveloper(final Throwable t) {
        if (this.deobfuscatedEnvironment()) {
            throw new RuntimeException(t);
        }
    }
    
    private boolean deobfuscatedEnvironment() {
        final Object deObf = Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return Boolean.valueOf(String.valueOf(deObf));
    }
    
    static {
        instance = new ReflectionWrapper();
    }
}
