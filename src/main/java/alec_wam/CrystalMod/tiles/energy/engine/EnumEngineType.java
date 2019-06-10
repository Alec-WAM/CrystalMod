package alec_wam.CrystalMod.tiles.energy.engine;

import alec_wam.CrystalMod.tiles.energy.engine.furnace.TileEntityEngineFurnace;
import net.minecraft.util.IStringSerializable;

public enum EnumEngineType implements IStringSerializable {
	FURNACE("furnace", TileEntityEngineFurnace.class);

	private final String unlocalizedName;
	public final Class<? extends TileEntityEngineBase> clazz;

	EnumEngineType(String name, Class<? extends TileEntityEngineBase> clazz) {
      this.clazz = clazz;
      unlocalizedName = name;
    }

    @Override
    public String getName() {
      return unlocalizedName;
    }
	
}
