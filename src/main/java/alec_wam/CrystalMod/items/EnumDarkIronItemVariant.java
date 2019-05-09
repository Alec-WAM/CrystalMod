package alec_wam.CrystalMod.items;

import net.minecraft.util.IStringSerializable;

public enum EnumDarkIronItemVariant implements IStringSerializable {
	INGOT,
	NUGGET;

    @Override
    public String getName() {
      return name().toLowerCase();
    }
}
