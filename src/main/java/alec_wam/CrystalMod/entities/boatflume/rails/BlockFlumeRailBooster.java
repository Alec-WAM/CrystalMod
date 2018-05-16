package alec_wam.CrystalMod.entities.boatflume.rails;

import alec_wam.CrystalMod.entities.boatflume.EntityFlumeBoat;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFlumeRailBooster extends BlockFlumeRailPowered {

	@Override
	public float getSpeed(World world, EntityFlumeBoat flume, BlockPos pos)
    {
		IBlockState state = world.getBlockState(pos);
		if(state.getValue(POWERED).booleanValue() == true){
			return 0.0028f;
		}
        return super.getSpeed(world, flume, pos);
    }
	
}
