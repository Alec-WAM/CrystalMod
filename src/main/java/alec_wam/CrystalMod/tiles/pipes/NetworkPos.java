package alec_wam.CrystalMod.tiles.pipes;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class NetworkPos {

	private BlockPos pos;
	private DimensionType dimension;
	
	public NetworkPos(BlockPos pos){
		this(pos, DimensionType.OVERWORLD);
	}
	
	public NetworkPos(BlockPos pos, DimensionType dim){
		this.pos = pos;
		this.dimension = dim;
	}

	public BlockPos getBlockPos() {
		return pos;
	}

	public void setBlockPos(BlockPos pos) {
		this.pos = pos;
	}

	public DimensionType getDimension() {
		return dimension;
	}

	public void setDimension(DimensionType dimension) {
		this.dimension = dimension;
	}

	public NetworkPos offset(EnumFacing dir) {
		return new NetworkPos(pos.offset(dir), dimension);
	}
	
}
