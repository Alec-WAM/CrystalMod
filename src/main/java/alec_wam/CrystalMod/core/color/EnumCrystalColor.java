package alec_wam.CrystalMod.core.color;

import net.minecraft.util.IStringSerializable;

public enum EnumCrystalColor implements IStringSerializable {
	BLUE("blue"),
	RED("red"),
	GREEN("green"),
	DARK("dark");
	
	private final String name;

	EnumCrystalColor(String name) {
		this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

	public static EnumCrystalColor convert(EnumCrystalColorSpecial type) {
		if(type == EnumCrystalColorSpecial.BLUE){
			return BLUE;
		}
		if(type == EnumCrystalColorSpecial.RED){
			return RED;
		}
		if(type == EnumCrystalColorSpecial.GREEN){
			return GREEN;
		}
		if(type == EnumCrystalColorSpecial.DARK){
			return DARK;
		}
		return null;
	}
}
