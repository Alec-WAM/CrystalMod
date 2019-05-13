package alec_wam.CrystalMod.tiles;

import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import net.minecraft.util.IStringSerializable;

public enum EnumCrystalColorSpecialWithCreative implements IStringSerializable {
	BLUE("blue"),
	RED("red"),
	GREEN("green"),
	DARK("dark"),
	PURE("pure"),
	CREATIVE("creative");
	
	private final String name;

	EnumCrystalColorSpecialWithCreative(String name) {
		this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
    
    public static EnumCrystalColorSpecialWithCreative convert(EnumCrystalColor color){
    	if(color == EnumCrystalColor.BLUE){
    		return BLUE;
    	}
    	if(color == EnumCrystalColor.RED){
    		return RED;
    	}
    	if(color == EnumCrystalColor.GREEN){
    		return GREEN;
    	}
    	if(color == EnumCrystalColor.DARK){
    		return DARK;
    	}
    	return null;
    }
}
