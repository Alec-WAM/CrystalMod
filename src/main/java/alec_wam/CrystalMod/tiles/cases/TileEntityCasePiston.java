package alec_wam.CrystalMod.tiles.cases;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCasePiston extends TileEntityCaseBase {

	public List<EnumFacing> validFaces = Lists.newArrayList();
	public float[] progress = new float[6];
	public float[] lastProgress = new float[6];
	public boolean[] isExtending = new boolean[6];
	public boolean opening;
	
    @Override
    public void writeCustomNBT(NBTTagCompound nbt){
    	super.writeCustomNBT(nbt);
    	//nbt.setBoolean("Opening", opening);
    	
    	for(int i = 0; i < 6; i++){
    		nbt.setFloat("Progress"+i, progress[i]);
    		nbt.setFloat("LastProgress"+i, lastProgress[i]);
    	}
    }
    
    @Override
    public void readCustomNBT(NBTTagCompound nbt){
    	super.readCustomNBT(nbt);
    	//opening = nbt.getBoolean("Opening");
    	
    	for(int i = 0; i < 6; i++){
    		progress[i] = nbt.getFloat("Progress"+i);
    		lastProgress[i] = nbt.getFloat("LastProgress"+i);
    	}
    }
    
    @Override
    public void update(){
    	super.update();
    	//this.opening = getWorld().isBlockPowered(getPos());
    	if(opening){
    		
    		if(validFaces.isEmpty()){
    			for(EnumFacing face : EnumFacing.VALUES){
    				if(getWorld().isAirBlock(getPos().offset(face))){
    					validFaces.add(face);
    				}
    			}
    		}
    		
    		boolean open = false;
    		for(EnumFacing side : validFaces){
    			int i = side.getIndex();
    			int phase = (int)(progress[i] / 0.5F);
    			this.isExtending[i] = phase < 2;
	    		if(lastProgress[i] < 1.0F){
	    			this.lastProgress[i] = this.progress[i];
	    			open = true;
	    			float f = this.progress[i] + 0.5F;
	    			double push = f;
	    			AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(new AxisAlignedBB(0, 0, 0, 1, 1, 1), side);
	                List<Entity> list1 = getWorld().getEntitiesWithinAABBExcludingEntity((Entity)null, this.getMovementArea(axisalignedbb, side, push).union(axisalignedbb));
	                if (!list1.isEmpty())
	                {
	                    for (int e = 0; e < list1.size(); ++e)
	                    {
	                        Entity entity = list1.get(e);

	                        if (entity.getPushReaction() != EnumPushReaction.IGNORE)
	                        {
	                        	double move = push + 0.001;
	                        	entity.move(MoverType.PISTON, move * side.getFrontOffsetX(), move * side.getFrontOffsetY(), move * side.getFrontOffsetZ());
                        		this.fixEntityWithinPistonBase(entity, side, push);
	                        }
	                    }
	                }
	    			this.progress[i] = f;
		    		if (this.progress[i] >= 1.0F)
		    		{
		    			this.progress[i] = 1.0F;
		    		}    
	    		}
    		}
    		if(open){
    			//ModLogger.info("Opening");
    		}
    	} else {
    		boolean closed = false;
    		for(EnumFacing side : validFaces){
    			int i = side.getIndex();
        		this.lastProgress[i] = this.progress[i];
    			this.isExtending[i] = false;
	    		if(this.progress[i] > 0.0F){
	    			closed = true;
		    		float f = this.progress[i] - 0.5F;
		    		this.progress[i] = f;
		
		    		if (this.progress[i] <= 0.0F)
		    		{
		    			this.progress[i] = 0.0F;
		    		}
	    		}
    		}
    		if(closed){
        		//ModLogger.info("Closing");
    		} else {
    			if(!validFaces.isEmpty()){
    				validFaces.clear();
    			}
    		}
    	}
    }
    
    @SideOnly(Side.CLIENT)
    public float getProgress(EnumFacing facing, float ticks)
    {
        if (ticks > 1.0F)
        {
            ticks = 1.0F;
        }

        return this.lastProgress[facing.getIndex()] + (this.progress[facing.getIndex()] - this.lastProgress[facing.getIndex()]) * ticks;
    }
    
	@Override
	public void onOpened() {
		opening = true;
		if(!getWorld().isRemote){
			CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Open"), this);
		}
	}

	@Override
	public void onClosed() {
		//ModLogger.info("Close "+getWorld().isRemote);
		opening = false;
	}
	
	public float getExtendedProgress(float progress, EnumFacing side)
    {
        return isExtending[side.getIndex()] ? progress - 1.0F : 1.0F - progress;
    }

    public AxisAlignedBB getAABB(IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return this.getAABB(world, pos, progress[side.getIndex()], side).union(this.getAABB(world, pos, lastProgress[side.getIndex()], side));
    }

    public AxisAlignedBB getAABB(IBlockAccess world, BlockPos pos, float progress, EnumFacing side)
    {
    	progress = getExtendedProgress(progress, side);
        IBlockState iblockstate = this.getCollisionRelatedBlockState(side);
        return iblockstate.getBoundingBox(world, pos).offset(progress * side.getFrontOffsetX(), progress * side.getFrontOffsetY(), progress * side.getFrontOffsetZ());
    }

    private IBlockState getCollisionRelatedBlockState(EnumFacing side)
    {
        return !isExtending[side.getIndex()] ? Blocks.PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.DEFAULT).withProperty(BlockDirectional.FACING, side) : getWorld().getBlockState(getPos());
    }

    public void moveCollidedEntities(float power, EnumFacing side)
    {
        EnumFacing enumfacing = isExtending[side.getIndex()] ? side : side.getOpposite();
        double d0 = power - progress[side.getIndex()];
        List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        this.getCollisionRelatedBlockState(side).addCollisionBoxToList(this.world, BlockPos.ORIGIN, new AxisAlignedBB(BlockPos.ORIGIN), list, (Entity)null, true);

        if (!list.isEmpty())
        {
            AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(getHeadBB(list), side);
            List<Entity> list1 = this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, this.getMovementArea(axisalignedbb, enumfacing, d0).union(axisalignedbb));

            if (!list1.isEmpty())
            {
                for (int i = 0; i < list1.size(); ++i)
                {
                    Entity entity = list1.get(i);

                    if (entity.getPushReaction() != EnumPushReaction.IGNORE)
                    {
                        double d1 = 0.0D;

                        for (int j = 0; j < list.size(); ++j)
                        {
                            AxisAlignedBB axisalignedbb1 = this.getMovementArea(this.moveByPositionAndProgress(list.get(j), side), enumfacing, d0);
                            AxisAlignedBB axisalignedbb2 = entity.getEntityBoundingBox();

                            if (axisalignedbb1.intersectsWith(axisalignedbb2))
                            {
                                d1 = Math.max(d1, this.getMovement(axisalignedbb1, enumfacing, axisalignedbb2));

                                if (d1 >= d0)
                                {
                                    break;
                                }
                            }
                        }

                        if (d1 > 0.0D)
                        {
                            d1 = Math.min(d1, d0) + 0.01D;
                            //MOVING_ENTITY.set(enumfacing);
                            entity.move(MoverType.PISTON, d1 * enumfacing.getFrontOffsetX(), d1 * enumfacing.getFrontOffsetY(), d1 * enumfacing.getFrontOffsetZ());
                            //MOVING_ENTITY.set((EnumFacing)null);

                            if (!isExtending[side.getIndex()])
                            {
                                this.fixEntityWithinPistonBase(entity, enumfacing, d0);
                            }
                        }
                    }
                }
            }
        }
    }

    public AxisAlignedBB getHeadBB(List<AxisAlignedBB> list)
    {
        double d0 = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 1.0D;
        double d4 = 1.0D;
        double d5 = 1.0D;

        for (AxisAlignedBB axisalignedbb : list)
        {
            d0 = Math.min(axisalignedbb.minX, d0);
            d1 = Math.min(axisalignedbb.minY, d1);
            d2 = Math.min(axisalignedbb.minZ, d2);
            d3 = Math.max(axisalignedbb.maxX, d3);
            d4 = Math.max(axisalignedbb.maxY, d4);
            d5 = Math.max(axisalignedbb.maxZ, d5);
        }

        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public double getMovement(AxisAlignedBB p_190612_1_, EnumFacing p_190612_2_, AxisAlignedBB p_190612_3_)
    {
        switch (p_190612_2_.getAxis())
        {
            case X:
                return getDeltaX(p_190612_1_, p_190612_2_, p_190612_3_);
            case Y:
            default:
                return getDeltaY(p_190612_1_, p_190612_2_, p_190612_3_);
            case Z:
                return getDeltaZ(p_190612_1_, p_190612_2_, p_190612_3_);
        }
    }

    public AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB bb, EnumFacing side)
    {
        double d0 = getExtendedProgress(progress[side.getIndex()], side);
        BlockPos pos = getPos();
        return bb.offset(pos.getX() + d0 * side.getFrontOffsetX(), pos.getY() + d0 * side.getFrontOffsetY(), pos.getZ() + d0 * side.getFrontOffsetZ());
    }

    public AxisAlignedBB getMovementArea(AxisAlignedBB bb, EnumFacing side, double power)
    {
        double d0 = power * side.getAxisDirection().getOffset();
        double d1 = Math.min(d0, 0.0D);
        double d2 = Math.max(d0, 0.0D);

        switch (side)
        {
            case WEST:
                return new AxisAlignedBB(bb.minX + d1, bb.minY, bb.minZ, bb.minX + d2, bb.maxY, bb.maxZ);
            case EAST:
                return new AxisAlignedBB(bb.maxX + d1, bb.minY, bb.minZ, bb.maxX + d2, bb.maxY, bb.maxZ);
            case DOWN:
                return new AxisAlignedBB(bb.minX, bb.minY + d1, bb.minZ, bb.maxX, bb.minY + d2, bb.maxZ);
            case UP:
            default:
                return new AxisAlignedBB(bb.minX, bb.maxY + d1, bb.minZ, bb.maxX, bb.maxY + d2, bb.maxZ);
            case NORTH:
                return new AxisAlignedBB(bb.minX, bb.minY, bb.minZ + d1, bb.maxX, bb.maxY, bb.minZ + d2);
            case SOUTH:
                return new AxisAlignedBB(bb.minX, bb.minY, bb.maxZ + d1, bb.maxX, bb.maxY, bb.maxZ + d2);
        }
    }

    public void fixEntityWithinPistonBase(Entity entity, EnumFacing side, double p_190605_3_)
    {
        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
        BlockPos pos = getPos();
        AxisAlignedBB axisalignedbb1 = Block.FULL_BLOCK_AABB.offset(pos);

        if (axisalignedbb.intersectsWith(axisalignedbb1))
        {
            EnumFacing enumfacing = side.getOpposite();
            double d0 = getMovement(axisalignedbb1, enumfacing, axisalignedbb) + 0.01D;
            double d1 = getMovement(axisalignedbb1, enumfacing, axisalignedbb.intersect(axisalignedbb1)) + 0.01D;

            if (Math.abs(d0 - d1) < 0.01D)
            {
                d0 = Math.min(d0, p_190605_3_) + 0.1D;
                //MOVING_ENTITY.set(p_190605_2_);
                entity.move(MoverType.PISTON, d0 * enumfacing.getFrontOffsetX(), d0 * enumfacing.getFrontOffsetY(), d0 * enumfacing.getFrontOffsetZ());
                //MOVING_ENTITY.set((EnumFacing)null);
            }
        }
    }

    public static double getDeltaX(AxisAlignedBB p_190611_0_, EnumFacing p_190611_1_, AxisAlignedBB p_190611_2_)
    {
        return p_190611_1_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_190611_0_.maxX - p_190611_2_.minX : p_190611_2_.maxX - p_190611_0_.minX;
    }

    public static double getDeltaY(AxisAlignedBB p_190608_0_, EnumFacing p_190608_1_, AxisAlignedBB p_190608_2_)
    {
        return p_190608_1_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_190608_0_.maxY - p_190608_2_.minY : p_190608_2_.maxY - p_190608_0_.minY;
    }

    public static double getDeltaZ(AxisAlignedBB p_190604_0_, EnumFacing p_190604_1_, AxisAlignedBB p_190604_2_)
    {
        return p_190604_1_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? p_190604_0_.maxZ - p_190604_2_.minZ : p_190604_2_.maxZ - p_190604_0_.minZ;
    }

}
