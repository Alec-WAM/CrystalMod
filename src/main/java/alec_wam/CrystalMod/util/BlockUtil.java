package alec_wam.CrystalMod.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

public class BlockUtil {

	public static void markBlockForUpdate(World world, BlockPos pos){
		BlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		world.notifyNeighborsOfStateChange(pos, state.getBlock());
	}
	
	public static BlockRayTraceResult rayTrace(World worldIn, PlayerEntity playerIn, RayTraceContext.FluidMode mode)
	{
		float f = playerIn.rotationPitch;
		float f1 = playerIn.rotationYaw;
		Vec3d vec3d = playerIn.getEyePosition(1.0F);
		float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
		float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = playerIn.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();;
		Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
		return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, mode, playerIn));
	}

	public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entityIn, boolean vert) {
    	
    	if (vert && MathHelper.abs((float) entityIn.posX - clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.posY + entityIn.getEyeHeight();

            if (d0 - clickedBlock.getY() > 2.0D) {
                return Direction.UP;
            }

            if (clickedBlock.getY() - d0 > 0.0D) {
                return Direction.DOWN;
            }
        }
    	
        return entityIn.getHorizontalFacing().getOpposite();
    }
	
	public static Direction getFacingFromContext(BlockItemUseContext context, boolean allowVert) {
    	
    	if (allowVert && MathHelper.abs((float) context.getPlayer().posX - context.getPos().getX()) < 2.0F && MathHelper.abs((float) context.getPlayer().posZ - context.getPos().getZ()) < 2.0F) {
            double d0 = context.getPlayer().posY + context.getPlayer().getEyeHeight();

            if (d0 - context.getPos().getY() > 2.0D) {
                return Direction.UP;
            }

            if (context.getPos().getY() - d0 > 0.0D) {
                return Direction.DOWN;
            }
        }
    	
        return context.getPlacementHorizontalFacing().getOpposite();
    }

	public static VoxelShape makeVoxelShape(AxisAlignedBB bb) {
		return Block.makeCuboidShape(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
	}
	
	public static AxisAlignedBB rotateBoundingBox(final AxisAlignedBB bb, Direction facing, double shiftSize){
		AxisAlignedBB realBB = bb;
		if(facing == Direction.DOWN){
			realBB = new AxisAlignedBB(bb.minX, shiftSize - bb.minY, bb.minZ, bb.maxX, shiftSize - bb.maxY, bb.maxZ);
		}
		if(facing == Direction.NORTH){
			realBB = new AxisAlignedBB(bb.minZ, bb.minX, shiftSize - bb.minY, bb.maxZ, bb.maxX, shiftSize - bb.maxY);
		}
		if(facing == Direction.SOUTH){
			realBB = new AxisAlignedBB(bb.minZ, bb.minX, bb.minY, bb.maxZ, bb.maxX, bb.maxY);
		}
		if(facing == Direction.EAST){
			realBB = new AxisAlignedBB(bb.minY, bb.minX, bb.minX, bb.maxY, bb.maxX, bb.maxZ);
		}
		if(facing == Direction.WEST){
			realBB = new AxisAlignedBB(shiftSize - bb.minY, bb.minX, bb.minX, shiftSize - bb.maxY, bb.maxX, bb.maxZ);
		}
		return realBB;
	}
	
	public static boolean isCobbleGen(World world, BlockPos pos, Direction from){
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == Blocks.COBBLESTONE){
			int waterCount = 0;
			int lavaCount = 0;
			for(Direction facing : Direction.values()){
				BlockPos offsetPos = pos.offset(facing);
				IFluidState fluid = world.getFluidState(offsetPos);
				if(fluid.getFluid() == Fluids.WATER && fluid.isSource()){
					waterCount++;
				}
				if(fluid.getFluid() == Fluids.LAVA && fluid.isSource()){
					lavaCount++;
				}
			}	
			return waterCount >= 1 && lavaCount >=1;
		}
		return false;
	}

	public static void playPlaceSound(World world, BlockPos pos, SoundType soundtype) {
		world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
	}
	
}
