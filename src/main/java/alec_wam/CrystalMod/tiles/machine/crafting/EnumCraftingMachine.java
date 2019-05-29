package alec_wam.CrystalMod.tiles.machine.crafting;

import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.TileEntityPoweredFurnace;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.TileEntityGrinder;
import alec_wam.CrystalMod.tiles.machine.crafting.press.TileEntityPress;
import net.minecraft.util.IStringSerializable;

public enum EnumCraftingMachine implements IStringSerializable {
	FURNACE("furnace", TileEntityPoweredFurnace.class),
	GRINDER("grinder", TileEntityGrinder.class),
	PRESS("press", TileEntityPress.class);

	private final String unlocalizedName;
	public final Class<? extends TileEntityMachine> clazz;

	EnumCraftingMachine(String name, Class<? extends TileEntityMachine> clazz) {
      this.clazz = clazz;
      unlocalizedName = name;
    }

    @Override
    public String getName() {
      return unlocalizedName;
    }
	
}
