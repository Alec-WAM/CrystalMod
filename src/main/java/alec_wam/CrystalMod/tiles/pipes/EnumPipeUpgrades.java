package alec_wam.CrystalMod.tiles.pipes;

import net.minecraft.util.IStringSerializable;

public enum EnumPipeUpgrades implements IStringSerializable {
	SPEED(), STACK(), SLOW(), COBBLE(true);
	
	final boolean isExternal;
	EnumPipeUpgrades() {
		this(false);
	}
	EnumPipeUpgrades(boolean isExternal) {
		this.isExternal = isExternal;
	}

	public boolean isExternal(){
		return isExternal;
	}
	
    @Override
    public String getName() {
      return name().toLowerCase();
    }
}
