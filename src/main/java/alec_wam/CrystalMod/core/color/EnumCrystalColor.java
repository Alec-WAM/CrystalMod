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
}
