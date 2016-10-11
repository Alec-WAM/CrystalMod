package alec_wam.CrystalMod.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {

	@CapabilityInject(ExtendedPlayer.class)
    public static Capability<ExtendedPlayer> EXTENDED_PLAYER = null;
	
}
