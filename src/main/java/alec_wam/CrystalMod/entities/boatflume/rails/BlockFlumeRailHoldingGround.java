package alec_wam.CrystalMod.entities.boatflume.rails;

import alec_wam.CrystalMod.blocks.FakeBlockStateWithData;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.entities.boatflume.BlockFlumeRailBase;
import alec_wam.CrystalMod.entities.boatflume.EntityFlumeBoat;
import alec_wam.CrystalMod.entities.boatflume.BlockFlumeRailBase.EnumRailDirection;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFlumeRailHoldingGround extends BlockFlumeRailPoweredGround {

	@SideOnly(Side.CLIENT)
    public final ModelFlumeRailRaisedGroundPowered RAISED_MODEL_INSTANCE = new ModelFlumeRailRaisedGroundPowered("rail_holding_ground", POWERED);
    
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
        return super.getSpeed(world, flume, pos);
    }
	
	@Override
	public Vec3d handleMotion(World world, EntityFlumeBoat flume, BlockPos pos, Vec3d motion){
		IBlockState state = world.getBlockState(pos);
		if(state.getValue(POWERED).booleanValue() == true){
			if(state.getValue(SHAPE).isAscending()){
				boolean NS = state.getValue(SHAPE) == EnumRailDirection.ASCENDING_NORTH || state.getValue(SHAPE) == EnumRailDirection.ASCENDING_SOUTH;
				double progress = NS ? (flume.posZ - pos.getZ()) : (flume.posX - pos.getX());//flume.posY - pos.getY();
				//ModLogger.info("Progress: "+progress);
				
				if(progress < 0.3){
					return new Vec3d(0, 0, 0);
				}
				return motion.scale(0.5);
			} else {
				boolean NS = state.getValue(SHAPE) == EnumRailDirection.NORTH_SOUTH || state.getValue(SHAPE) == EnumRailDirection.ASCENDING_NORTH || state.getValue(SHAPE) == EnumRailDirection.ASCENDING_SOUTH;
				double progress = NS ? (flume.posZ - pos.getZ()) : (flume.posX - pos.getX());
				if(progress < 0.55 && progress > 0.45){
					return new Vec3d(0, 0, 0);
				}
			}
		}
		return super.handleMotion(world, flume, pos, motion);
	}
	
}
