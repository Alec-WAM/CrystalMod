package alec_wam.CrystalMod.tiles.enhancedEnchantmentTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.block.ICustomRaytraceBlock;
import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.BlockBasicTile;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.tiles.pipes.CollidableComponent;
import alec_wam.CrystalMod.tiles.pipes.RaytraceResult;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnhancedEnchantmentTable extends BlockBasicTile implements ICustomRaytraceBlock, ICustomModel {

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
	protected static final AxisAlignedBB AABB_FULL = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.3D, 0.75D, 1.0D);

	public BlockEnhancedEnchantmentTable() {
		super(TileEntityEnhancedEnchantmentTable.class, Material.IRON);
		this.setLightOpacity(0);
		this.setHardness(1.5F);
		this.setSoundType(SoundType.STONE);
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockStateFacing.facingProperty, EnumFacing.NORTH));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "facing=north"));
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile !=null && tile instanceof IInventory)ItemUtil.dropContent(0, (IInventory)tile, worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }

	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
        boolean update = false;
        if(tile !=null && tile instanceof IFacingTile){
        	EnumFacing face = placer.getHorizontalFacing().getOpposite();
        	((IFacingTile)tile).setFacing(face.getHorizontalIndex());
        }
        if(update)BlockUtil.markBlockForUpdate(world, pos);
    }
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		TileEntity te = world.getTileEntity(pos);
		EnumFacing facing = EnumFacing.NORTH;
        if(te !=null && te instanceof IFacingTile){
        	IFacingTile tile = (IFacingTile)te;
        	facing = EnumFacing.getHorizontal(tile.getFacing());
        }
        return state.withProperty(BlockStateFacing.facingProperty, facing);
    }
	
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateFacing(this);
    }
	
	@Override
	public int getMetaFromState(IBlockState state){
		return 0;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
	
	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
		TileEntity te = world.getTileEntity(pos);
        if(te !=null && te instanceof IFacingTile){
        	IFacingTile tile = (IFacingTile)te;
        	
        	EnumFacing newFacing = axis;
        	if(axis.getAxis() == Axis.Y){
        		newFacing = EnumFacing.getHorizontal(tile.getFacing()).rotateY();
        	}
        	tile.setFacing(newFacing.getHorizontalIndex());
        	if(!world.isRemote){
        		CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "Update"), te);
        	}
        	BlockUtil.markBlockForUpdate(world, pos);
        	return true;
        }
        return false;
    }
	@Override
	public EnumFacing[] getValidRotations(World world, BlockPos pos)
    {
    	return EnumFacing.HORIZONTALS;
    }
	
	//RayTrace

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return AABB_FULL;
    }
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
	{
		for(final AxisAlignedBB bb : getBounds()){
			addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		AxisAlignedBB minBB = Block.FULL_BLOCK_AABB;

		List<RaytraceResult> results = BlockUtil.doRayTraceAll(world, pos.getX(), pos.getY(), pos.getZ(), player, this);
		Iterator<RaytraceResult> iter = results.iterator();
		while (iter.hasNext()) {
			CollidableComponent component = iter.next().component;
			if (component == null) {
				iter.remove();
			}
		}

		RaytraceResult hit = RaytraceResult.getClosestHit(EntityUtil.getEyePosition(player), results);
		if (hit != null && hit.component != null && hit.component.bound != null) {
			minBB = hit.component.bound;
		} else {
			minBB = Block.FULL_BLOCK_AABB;
		}
		return new AxisAlignedBB(pos.getX() + minBB.minX, pos.getY()
				+ minBB.minY, pos.getZ() + minBB.minZ, pos.getX() + minBB.maxX,
				pos.getY() + minBB.maxY, pos.getZ() + minBB.maxZ);
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d origin, Vec3d direction) {

		RaytraceResult raytraceResult = BlockUtil.doRayTrace(world, pos.getX(), pos.getY(), pos.getZ(), origin, direction, null, this);
		net.minecraft.util.math.RayTraceResult ret = null;
		if (raytraceResult != null) {
			ret = raytraceResult.rayTraceResult;
			if (ret != null) {
				ret.hitInfo = raytraceResult.component;
			}
		}

		return ret;
	}

	private AxisAlignedBB bounds;

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source,
			BlockPos pos) {
		if(bounds == null){
			return AABB_FULL;
		}
		return bounds;
	}

	@Override
	public Collection<? extends CollidableComponent> getCollidableComponents(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		EnumFacing facing = EnumFacing.UP;
		boolean hasItem = false;
		if (te !=null) {
			if(te instanceof IPedistal){
				hasItem = ItemStackTools.isValid(((IPedistal)te).getStack());
				facing = ((IPedistal)te).getRotation();
			}
		}

		final List<CollidableComponent> collidables = new ArrayList<CollidableComponent>();
		for(final AxisAlignedBB bb : getBounds()){
			collidables.add(new CollidableComponent(bb, null, 0));			
		}

		//Do we have an Item in the pedestal?
		if(hasItem){
			AxisAlignedBB bb = new AxisAlignedBB(0.3, 0.3, 0.3, 0.7, 0.7, 0.7);
			AxisAlignedBB realBB = bb.offset(facing.getFrontOffsetX() * 0.35, facing.getFrontOffsetY() * 0.35, facing.getFrontOffsetZ() * 0.35);
			collidables.add(new CollidableComponent(realBB, null, 1));
		}
		return collidables;
	}

	private List<AxisAlignedBB> getBounds(){
		List<AxisAlignedBB> list = Lists.newArrayList();
		float pixel = 1.0F / 16.0F;

		list.add(AABB);
		list.add(new AxisAlignedBB(4 *pixel, 12 *pixel, 4 *pixel, 12 *pixel, 19 *pixel, 12 *pixel));
		
		return list;
	}

	@SuppressWarnings("deprecation")
	@Override
	public RayTraceResult defaultRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d origin, Vec3d direction) {
		return super.collisionRayTrace(blockState, world, pos, origin, direction);
	}

	@Override
	public void setBounds(AxisAlignedBB bound) {
		bounds = bound;
	}

	@Override
	public void resetBounds() {
		bounds = Block.FULL_BLOCK_AABB;
	}

}
