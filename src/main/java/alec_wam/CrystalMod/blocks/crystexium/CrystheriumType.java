package alec_wam.CrystalMod.blocks.crystexium;

import java.util.Locale;

import alec_wam.CrystalMod.util.IEnumMeta;
import net.minecraft.util.IStringSerializable;

public enum CrystheriumType implements IStringSerializable, IEnumMeta {
	NORMAL, BLUE, RED, GREEN, DARK;

	final int meta;
	
	CrystheriumType(){
		meta = ordinal();
	}
	
	@Override
	public int getMeta() {
		return meta;
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase(Locale.US);
	}
	
}
