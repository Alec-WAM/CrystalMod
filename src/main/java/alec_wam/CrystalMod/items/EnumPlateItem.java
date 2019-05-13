package alec_wam.CrystalMod.items;

import net.minecraft.util.IStringSerializable;

public enum EnumPlateItem implements IStringSerializable {
	DARKIRON,
	BLUE,
	RED,
	GREEN,
	DARK,
	PURE;

    @Override
    public String getName() {
      return name().toLowerCase();
    }
}
