package alec_wam.CrystalMod.tiles;

import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import net.minecraft.util.IStringSerializable;

public enum EnumCrystalColorWithCreative implements IStringSerializable {
	BLUE("blue"),
	RED("red"),
	GREEN("green"),
	DARK("dark"),
	CREATIVE("creative");
	
	private final String name;

	EnumCrystalColorWithCreative(String name) {
		this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

	public static EnumCrystalColorWithCreative convert(EnumCrystalColor type) {
		if(type == EnumCrystalColor.BLUE){
			return BLUE;
		}
		if(type == EnumCrystalColor.RED){
			return RED;
		}
		if(type == EnumCrystalColor.GREEN){
			return GREEN;
		}
		if(type == EnumCrystalColor.DARK){
			return DARK;
		}
		return null;
	}
}
