package alec_wam.CrystalMod.tiles.pipes;

import net.minecraft.util.IStringSerializable;

public enum EnumPipeUpgrades implements IStringSerializable {
	SPEED, STACK, SLOW, COBBLE;
	
	EnumPipeUpgrades() {}

    @Override
    public String getName() {
      return name().toLowerCase();
    }
}
