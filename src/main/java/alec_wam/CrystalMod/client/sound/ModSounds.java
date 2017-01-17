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

	static {
        if (!Bootstrap.isRegistered()) {
            throw new RuntimeException("Accessed Sounds before Bootstrap!");
        } else {
        	fusionStartup = getRegisteredSoundEvent(CrystalMod.resource("fusion_startup"));
        	fusionRunning = getRegisteredSoundEvent(CrystalMod.resource("fusion_running"));
        	fusionCooldown = getRegisteredSoundEvent(CrystalMod.resource("fusion_cooldown"));
        	fusionDing = getRegisteredSoundEvent(CrystalMod.resource("fusion_ding"));
        }
    }

    private static SoundEvent getRegisteredSoundEvent(String id) {
        SoundEvent soundevent = new SoundEvent(new ResourceLocation(id));

        if (soundevent == null) {
            throw new IllegalStateException("Invalid Sound requested: " + id);
        } else {
            SOUND_EVENTS.put(id, soundevent);
            return soundevent;
        }
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