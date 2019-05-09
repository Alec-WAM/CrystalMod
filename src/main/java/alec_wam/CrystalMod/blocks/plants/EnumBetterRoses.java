package alec_wam.CrystalMod.blocks.plants;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum EnumBetterRoses implements IStringSerializable {
	WHITE, ORANGE, MAGENTA, YELLOW, PINK, LIGHT_BLUE, PURPLE;

	final String name;
	EnumBetterRoses(){
		name = toString().toLowerCase(Locale.US);
	}

	@Override
	public String getName() {
		return name;
	}
	
}
