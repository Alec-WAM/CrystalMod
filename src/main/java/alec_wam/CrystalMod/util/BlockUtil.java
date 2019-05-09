package alec_wam.CrystalMod.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

public class BlockUtil {

	public static void markBlockForUpdate(World world, BlockPos pos){
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		world.notifyNeighborsOfStateChange(pos, state.getBlock());
	}
	
	public static RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, RayTraceFluidMode useLiquids)
	{
		float f = playerIn.rotationPitch;
		float f1 = playerIn.rotationYaw;
		double d0 = playerIn.posX;
		double d1 = playerIn.posY + playerIn.getEyeHeight();
		double d2 = playerIn.posZ;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = 5.0D;
		if (playerIn instanceof net.minecraft.entity.player.EntityPlayerMP)
		{
			d3 = ((net.minecraft.entity.player.EntityPlayerMP)playerIn).getAttribute(EntityPlayer.REACH_DISTANCE).getValue();
		}
		Vec3d vec3d1 = vec3d.add(f6 * d3, f5 * d3, f7 * d3);
		return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, false, false);
	}

	public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entityIn, boolean vert) {
    	
    	if (vert && MathHelper.abs((float) entityIn.posX - clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.posY + entityIn.getEyeHeight();

            if (d0 - clickedBlock.getY() > 2.0D) {
                return EnumFacing.UP;
            }

            if (clickedBlock.getY() - d0 > 0.0D) {
                return EnumFacing.DOWN;
            }
        }
    	
        return entityIn.getHorizontalFacing().getOpposite();
    }
	
	public static EnumFacing getFacingFromContext(BlockItemUseContext context, boolean allowVert) {
    	
    	if (allowVert && MathHelper.abs((float) context.getPlayer().posX - context.getPos().getX()) < 2.0F && MathHelper.abs((float) context.getPlayer().posZ - context.getPos().getZ()) < 2.0F) {
            double d0 = context.getPlayer().posY + context.getPlayer().getEyeHeight();

            if (d0 - context.getPos().getY() > 2.0D) {
                return EnumFacing.UP;
            }

            if (context.getPos().getY() - d0 > 0.0D) {
                return EnumFacing.DOWN;
            }
        }
    	
        return context.getPlacementHorizontalFacing().getOpposite();
    }

	public static VoxelShape makeVoxelShape(AxisAlignedBB bb) {
		return Block.makeCuboidShape(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
	}
	
	public static AxisAlignedBB rotateBoundingBox(final AxisAlignedBB bb, EnumFacing facing, double shiftSize){
		AxisAlignedBB realBB = bb;
		if(facing == EnumFacing.DOWN){
			realBB = new AxisAlignedBB(bb.minX, shiftSize - bb.minY, bb.minZ, bb.maxX, shiftSize - bb.maxY, bb.maxZ);
		}
		if(facing == EnumFacing.NORTH){
			realBB = new AxisAlignedBB(bb.minZ, bb.minX, shiftSize - bb.minY, bb.maxZ, bb.maxX, shiftSize - bb.maxY);
		}
		if(facing == EnumFacing.SOUTH){
			realBB = new AxisAlignedBB(bb.minZ, bb.minX, bb.minY, bb.maxZ, bb.maxX, bb.maxY);
		}
		if(facing == EnumFacing.EAST){
			realBB = new AxisAlignedBB(bb.minY, bb.minX, bb.minX, bb.maxY, bb.maxX, bb.maxZ);
		}
		if(facing == EnumFacing.WEST){
			realBB = new AxisAlignedBB(shiftSize - bb.minY, bb.minX, bb.minX, shiftSize - bb.maxY, bb.maxX, bb.maxZ);
		}
		return realBB;
	}
	
}
