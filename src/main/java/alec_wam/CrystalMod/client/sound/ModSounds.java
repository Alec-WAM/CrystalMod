package alec_wam.CrystalMod.client.sound;

import java.util.HashMap;
import java.util.Map;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSounds {
	public static final Map<String, SoundEvent> SOUND_EVENTS = new HashMap<String, SoundEvent>();
	
	public static final SoundEvent fusionStartup;
	public static final SoundEvent fusionRunning;
	public static final SoundEvent fusionCooldown;
	public static final SoundEvent fusionDing;

	public static final SoundEvent levelDown;
	public static final SoundEvent unsplash;
	public static final SoundEvent redstone_removed;
	public static final SoundEvent explosion_ringing;
	

	public static final SoundEvent explosion_fusor_tier0;
	public static final SoundEvent explosion_fusor_tier1;
	public static final SoundEvent explosion_fusor_tier2;
	
	public static final SoundEvent dark_infection_start;
	public static final SoundEvent dark_infection_looping;

	public static final SoundEvent devil_ambient;
	public static final SoundEvent devil_death;
	public static final SoundEvent devil_hurt;
	public static final SoundEvent angel_ambient;
	public static final SoundEvent angel_death;
	public static final SoundEvent angel_hurt;

	public static final SoundEvent backpack_zipper;
	
	static {
        if (!Bootstrap.isRegistered()) {
            throw new RuntimeException("Accessed Sounds before Bootstrap!");
        } else {
        	fusionStartup = getRegisteredSoundEvent(CrystalMod.resource("fusion_startup"));
        	fusionRunning = getRegisteredSoundEvent(CrystalMod.resource("fusion_running"));
        	fusionCooldown = getRegisteredSoundEvent(CrystalMod.resource("fusion_cooldown"));
        	fusionDing = getRegisteredSoundEvent(CrystalMod.resource("fusion_ding"));
        	levelDown = getRegisteredSoundEvent(CrystalMod.resource("leveldown"));
        	unsplash = getRegisteredSoundEvent(CrystalMod.resource("unsplash"));
        	redstone_removed = getRegisteredSoundEvent(CrystalMod.resource("redstone_removed"));
        	explosion_ringing = getRegisteredSoundEvent(CrystalMod.resource("explosion_ringing"));
        	

        	explosion_fusor_tier0 = getRegisteredSoundEvent(CrystalMod.resource("explosion_fusor_tier0"));
        	explosion_fusor_tier1 = getRegisteredSoundEvent(CrystalMod.resource("explosion_fusor_tier1"));
        	explosion_fusor_tier2 = getRegisteredSoundEvent(CrystalMod.resource("explosion_fusor_tier2"));
        	
        	dark_infection_start = getRegisteredSoundEvent(CrystalMod.resource("dark_infection_start"));
        	dark_infection_looping = getRegisteredSoundEvent(CrystalMod.resource("dark_infection_looping"));
        	
        	devil_ambient = getRegisteredSoundEvent(CrystalMod.resource("devil_ambient"));
        	devil_death = getRegisteredSoundEvent(CrystalMod.resource("devil_death"));
        	devil_hurt = getRegisteredSoundEvent(CrystalMod.resource("devil_hurt"));
        	angel_ambient = getRegisteredSoundEvent(CrystalMod.resource("angel_ambient"));
        	angel_death = getRegisteredSoundEvent(CrystalMod.resource("angel_death"));
        	angel_hurt = getRegisteredSoundEvent(CrystalMod.resource("angel_hurt"));
        	
        	backpack_zipper = getRegisteredSoundEvent(CrystalMod.resource("backpack_zipper"));
        }
    }

    private static SoundEvent getRegisteredSoundEvent(String id) {
        SoundEvent soundevent = new SoundEvent(new ResourceLocation(id));

        SOUND_EVENTS.put(id, soundevent);
		return soundevent;
    }

    public static SoundEvent getSound(String id) {
        if (SOUND_EVENTS.containsKey(id)){
            return SOUND_EVENTS.get(id);
        }
        else if (SoundEvent.REGISTRY.containsKey(new ResourceLocation(id))) {
            return SoundEvent.REGISTRY.getObject(new ResourceLocation(id));
        }
        else {
            return null;
        }
    }
}
