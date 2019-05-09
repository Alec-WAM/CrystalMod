package alec_wam.CrystalMod.tiles;

import net.minecraft.tileentity.TileEntityType;

public class TileEntityModVariant<VARIANT extends Enum<VARIANT>> extends TileEntityMod {

	protected final VARIANT type;
	
	public TileEntityModVariant(TileEntityType<?> tileType) {
		super(tileType);
		type = null;
	}
	
	public TileEntityModVariant(TileEntityType<?> tileType, VARIANT type) {
		super(tileType);
		this.type = type;
	}

}
