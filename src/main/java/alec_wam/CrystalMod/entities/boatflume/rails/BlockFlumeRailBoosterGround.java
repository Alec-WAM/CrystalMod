package alec_wam.CrystalMod.entities.boatflume.rails;

import alec_wam.CrystalMod.blocks.FakeBlockStateWithData;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.entities.boatflume.BlockFlumeRailBase;
import alec_wam.CrystalMod.entities.boatflume.EntityFlumeBoat;
import alec_wam.CrystalMod.proxy.ClientProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFlumeRailBoosterGround extends BlockFlumeRailPoweredGround {

	@SideOnly(Side.CLIENT)
    public final ModelFlumeRailRaisedGroundPowered RAISED_MODEL_INSTANCE = new ModelFlumeRailRaisedGroundPowered("rail_booster_ground", POWERED);
    
    @Override
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		if(state.getValue(SHAPE).isAscending()){
			return new FakeBlockStateWithData(state, world, pos);
		}
    	return super.getExtendedState(state, world, pos);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
    	ModBlocks.initBasicModel(this);
    	for(EnumRailDirection dir : new EnumRailDirection[]{BlockFlumeRailBase.EnumRailDirection.ASCENDING_NORTH, BlockFlumeRailBase.EnumRailDirection.ASCENDING_SOUTH, BlockFlumeRailBase.EnumRailDirection.ASCENDING_WEST, BlockFlumeRailBase.EnumRailDirection.ASCENDING_EAST}){
			ResourceLocation baseLocation = getRegistryName();
			ModelResourceLocation inv = new ModelResourceLocation(baseLocation, "powered=false,shape="+dir.getName());
			ClientProxy.registerCustomModel(inv, RAISED_MODEL_INSTANCE);
			inv = new ModelResourceLocation(baseLocation, "powered=true,shape="+dir.getName());
			ClientProxy.registerCustomModel(inv, RAISED_MODEL_INSTANCE);
		}
	}
	
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
