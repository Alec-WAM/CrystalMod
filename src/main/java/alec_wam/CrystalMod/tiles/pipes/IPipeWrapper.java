package alec_wam.CrystalMod.tiles.pipes;

import net.minecraft.util.EnumFacing;

public interface IPipeWrapper {

	public TileEntityPipe getPipe();
	
	public boolean isSender();

	public EnumFacing getPipeDir();
	
}
