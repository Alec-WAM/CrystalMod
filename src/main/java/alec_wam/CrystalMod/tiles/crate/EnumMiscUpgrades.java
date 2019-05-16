package alec_wam.CrystalMod.tiles.crate;

import net.minecraft.util.IStringSerializable;

public enum EnumMiscUpgrades implements IStringSerializable {
	VOID,
	FUSION_AUTO;
	
	EnumMiscUpgrades() {}

    @Override
    public String getName() {
      return name().toLowerCase();
    }
}
