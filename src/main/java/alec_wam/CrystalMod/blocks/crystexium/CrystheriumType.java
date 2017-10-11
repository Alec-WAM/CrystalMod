package alec_wam.CrystalMod.blocks.crystexium;

import java.util.Locale;

import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import net.minecraft.util.IStringSerializable;

public enum CrystheriumType implements IStringSerializable, IEnumMeta, IEnumMetaItem {
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

	@Override
	public int getMetadata() {
		return meta;
	}
	
}
