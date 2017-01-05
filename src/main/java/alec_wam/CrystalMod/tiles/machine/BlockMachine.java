package alec_wam.CrystalMod.tiles.machine;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.UUIDUtils;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockMachine  extends BlockContainer {

	public BlockMachine(Material materialIn) {
		super(materialIn);
	}
	
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
	
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateMachine(this);
    }
    
    public int getMetaFromState(IBlockState state){
    	return 0;
    }
    
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
        EnumFacing face = EnumFacing.NORTH;
        boolean active = false;
        if (te !=null) {
        	if(te instanceof IFacingTile){
        		boolean vert = ((IFacingTile)te).useVerticalFacing();
        		int facing = ((IFacingTile)te).getFacing();
        		face = vert ? EnumFacing.getFront(facing) : EnumFacing.getHorizontal(facing);
        	}
        	if(te instanceof IActiveTile)active = ((IActiveTile)te).isActive();
        }
        return state.withProperty(BlockStateMachine.facingProperty, face).withProperty(BlockStateMachine.activeProperty, Boolean.valueOf(active));
    }
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
	    if (willHarvest) {
	      return true;
	    }
	    return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te,
	      @Nullable ItemStack stack) {
	    super.harvestBlock(worldIn, player, pos, state, te, stack);
	    worldIn.setBlockToAir(pos);
	}
	
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
		if(world == null || pos == null)return super.getPickBlock(state, target, world, pos, player);
		return getNBTDrop(world, pos, world.getTileEntity(pos));
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
	    if (world == null || pos == null) {
	      return super.getDrops(world, pos, state, fortune);
	    }
    	return Lists.newArrayList(getNBTDrop(world, pos, world.getTileEntity(pos)));
	}
	
	public static final String TILE_NBT_STACK = "TileData";
	
	protected ItemStack getNBTDrop(IBlockAccess world, BlockPos pos, TileEntity tileEntity) {
		ItemStack stack = new ItemStack(this, 1, damageDropped(world.getBlockState(pos)));
		if(tileEntity !=null && tileEntity instanceof INBTDrop){
			INBTDrop machine = (INBTDrop)tileEntity;
			NBTTagCompound nbt = new NBTTagCompound();
			machine.writeToStack(nbt);
			ItemNBTHelper.getCompound(stack).setTag(TILE_NBT_STACK, nbt);
		}
		return stack;
	}

	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		
        TileEntity tile = world.getTileEntity(pos);
        boolean update = false;
        if(ItemNBTHelper.verifyExistance(stack, TILE_NBT_STACK)){
        	if(tile !=null && tile instanceof INBTDrop){
        		INBTDrop machine = (INBTDrop)tile;
        		machine.readFromStack(ItemNBTHelper.getCompound(stack).getCompoundTag(TILE_NBT_STACK));
        		update = true;
        	}
        }
        if(tile !=null && tile instanceof IFacingTile){
        	boolean vert = ((IFacingTile)tile).useVerticalFacing();
        	EnumFacing face = getFacingFromEntity(pos, placer, vert);
        	((IFacingTile)tile).setFacing(vert ? face.getIndex() : face.getHorizontalIndex());
        }
        if(update)BlockUtil.markBlockForUpdate(world, pos);
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
    
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
		TileEntity te = world.getTileEntity(pos);
        if(te !=null && te instanceof IFacingTile){
        	IFacingTile tile = (IFacingTile)te;
        	int next = tile.getFacing();
        	next++;
        	if(tile.useVerticalFacing())next%=6;
        	else next%=4;
        	tile.setFacing(next);
        	BlockUtil.markBlockForUpdate(world, pos);
        	return true;
        }
        return false;
    }
    
    public EnumFacing[] getValidRotations(World world, BlockPos pos)
    {
    	TileEntity tile = world.getTileEntity(pos);
    	if(tile !=null && tile instanceof IFacingTile){
    		boolean vert = ((IFacingTile)tile).useVerticalFacing();
    		if(vert)return EnumFacing.VALUES;
    	}
        return EnumFacing.HORIZONTALS;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called
     */
    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam)
    {
    	super.eventReceived(state, worldIn, pos, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }

}
