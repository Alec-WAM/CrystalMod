package alec_wam.CrystalMod.tiles.crate;

import net.minecraft.util.IStringSerializable;

public enum EnumCrateUpgrades implements IStringSerializable {
	VOID;
	
	EnumCrateUpgrades() {}

    @Override
    public String getName() {
      return name().toLowerCase();
    }
}
