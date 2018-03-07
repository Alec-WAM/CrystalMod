package alec_wam.CrystalMod.entities.boatflume.rails;

import alec_wam.CrystalMod.entities.boatflume.EntityFlumeBoat;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockFlumeRailHolding extends BlockFlumeRailPowered {

	@Override
	public float getSpeed(World world, EntityFlumeBoat flume, BlockPos pos)
    {
		IBlockState state = world.getBlockState(pos);
        return super.getSpeed(world, flume, pos);
    }
	
	@Override
	public Vec3d handleMotion(World world, EntityFlumeBoat flume, BlockPos pos, Vec3d motion){
		IBlockState state = world.getBlockState(pos);
		if(state.getValue(POWERED).booleanValue() == true){
			boolean NS = state.getValue(SHAPE) == EnumRailDirection.NORTH_SOUTH || state.getValue(SHAPE) == EnumRailDirection.ASCENDING_NORTH || state.getValue(SHAPE) == EnumRailDirection.ASCENDING_SOUTH;
			double progress = NS ? (flume.posZ - pos.getZ()) : (flume.posX - pos.getX());
			if(progress < 0.55 && progress > 0.45){
				return new Vec3d(0, 0, 0);
			}
		}
		return super.handleMotion(world, flume, pos, motion);
	}
	
}
