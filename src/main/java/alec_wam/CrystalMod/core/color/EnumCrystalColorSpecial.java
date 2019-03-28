package alec_wam.CrystalMod.core.color;

import net.minecraft.util.IStringSerializable;

public enum EnumCrystalColorSpecial implements IStringSerializable {
	BLUE("blue"),
	RED("red"),
	GREEN("green"),
	DARK("dark"),
	PURE("pure");
	
	private final String name;

	EnumCrystalColorSpecial(String name) {
		this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
    
    public static EnumCrystalColorSpecial convert(EnumCrystalColor color){
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
