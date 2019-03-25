package alec_wam.CrystalMod.tiles.fusion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.block.ICustomRaytraceBlock;
import alec_wam.CrystalMod.api.pedistals.IFusionPedistal;
import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.tiles.pipes.CollidableComponent;
import alec_wam.CrystalMod.tiles.pipes.RaytraceResult;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class BlockPedistal extends BlockContainer implements ICustomModel, ICustomRaytraceBlock {

	public BlockPedistal() {
		super(Material.ROCK);
		this.setHardness(1f).setResistance(10F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setSoundType(SoundType.STONE);
	}
	
	public BlockPedistal(Material material) {
		super(material);
		this.setHardness(1f).setResistance(10F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new PedistalBlockStateMapper());
		ModBlocks.initBasicModel(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TilePedistal();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
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
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
        EnumFacing face = EnumFacing.NORTH;
        if (te !=null) {
        	if(te instanceof IFacingTile){
        		int facing = ((IFacingTile)te).getFacing();
        		face = EnumFacing.getFront(facing);
        	}
        }
        return state.withProperty(BlockStateFacing.facingProperty, face);
    }

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if (worldIn.isRemote) {
            return true;
        }
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof IPedistal){
			IPedistal pedistal = (IPedistal)tile;
			boolean locked = false;
			if(tile instanceof IFusionPedistal){
				locked = ((IFusionPedistal)tile).isLocked();
			}
			if(locked)return false;
			ItemStack heldItem = playerIn.getHeldItem(hand);
			if(ItemStackTools.isValid(heldItem)){
				ItemStack insertStack = heldItem;
				if(ItemStackTools.isEmpty(pedistal.getStack())){
					insertStack = ItemUtil.copy(heldItem, 1);
				}
				IItemHandler handler = ItemUtil.getExternalItemHandler(worldIn, pos, EnumFacing.UP);
				if(handler == null)return false;
				int insert = ItemUtil.doInsertItem(handler, insertStack, EnumFacing.UP);
				if(insert > 0){
					ItemStackTools.incStackSize(heldItem, -insert);
					worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f, 0.95f);
					return true;
				}
			} 
			RaytraceResult result = BlockUtil.doRayTrace(worldIn, pos.getX(), pos.getY(), pos.getZ(), playerIn, this);
			ItemStack pedistalStack = pedistal.getStack();
			if(ItemStackTools.isValid(pedistalStack) && result !=null && result.component !=null){
				if(result.component.data !=null && result.component.data instanceof Integer){
					//Are we looking at the item? If so then remove the item from the pedestal
					if(((Integer)result.component.data) == 1){
						ItemStack drop = ItemStackTools.safeCopy(pedistalStack);
						pedistal.setStack(ItemStackTools.getEmptyStack());
						boolean dropItem = true;
						if(ItemStackTools.isEmpty(heldItem) && playerIn.isSneaking()){
							playerIn.setHeldItem(hand, drop);
							dropItem = false;
						}
						if(dropItem){
							playerIn.inventory.addItemStackToInventory(drop);
							if(!ItemStackTools.isEmpty(drop)){
								ItemUtil.spawnItemInWorldWithRandomMotion(worldIn, drop, pos);
							}
						}
						worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2f, 0.85f);
						return true;
					}
				}
			}
			return false;
		}
        return false;
    }
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		
    }
    
	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
		TileEntity te = world.getTileEntity(pos);
        if(te !=null && te instanceof IFacingTile){
        	IFacingTile tile = (IFacingTile)te;
        	int next = tile.getFacing();
        	next++;
        	next%=6;
        	tile.setFacing(next);
        	BlockUtil.markBlockForUpdate(world, pos);
        	return true;
        }
        return false;
    }
    
    @Override
	public EnumFacing[] getValidRotations(World world, BlockPos pos)
    {
    	TileEntity tile = world.getTileEntity(pos);
    	if(tile !=null && tile instanceof IFacingTile){
    		return EnumFacing.VALUES;
    	}
        return new EnumFacing[0];
    }

    @Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile !=null && tile instanceof IInventory)ItemUtil.dropContent(0, (IInventory)tile, worldIn, pos);
        super.breakBlock(worldIn, pos, state);
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
	
	public static class PedistalBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			BlockPedistal block = (BlockPedistal)state.getBlock();
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			builder.append(BlockStateFacing.facingProperty.getName());
			builder.append("=");
			builder.append(state.getValue(BlockStateFacing.facingProperty));
			
			nameOverride = block.getRegistryName().getResourcePath();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}
	
	//RayTrace
	
	@Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
    {
		TileEntity te = worldIn.getTileEntity(pos);
        EnumFacing facing = EnumFacing.UP;
        if (te !=null) {
        	if(te instanceof IPedistal){
        		facing = ((IPedistal)te).getRotation();
        	}
        }
		for(final AxisAlignedBB bb : getPedistalBounds(facing)){
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
			return new AxisAlignedBB(0.0625F, 0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
		for(final AxisAlignedBB bb : getPedistalBounds(facing)){
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
	
	private List<AxisAlignedBB> getPedistalBounds(EnumFacing facing){
		List<AxisAlignedBB> list = Lists.newArrayList();
		List<AxisAlignedBB> bbs = Lists.newArrayList();
		float pixel = 1.0F / 16.0F;
		
		bbs.add(new AxisAlignedBB(1 * pixel, 8 * pixel, 1 * pixel, 15 * pixel, 10 * pixel, 15 * pixel));
		bbs.add(new AxisAlignedBB(3 * pixel, 7 * pixel, 3 * pixel, 13 * pixel, 8 * pixel, 13 * pixel));
		bbs.add(new AxisAlignedBB(4 * pixel, 2 * pixel, 4 * pixel, 12 * pixel, 7 * pixel, 12 * pixel));
		bbs.add(new AxisAlignedBB(3 * pixel, 1 * pixel, 3 * pixel, 13 * pixel, 2 * pixel, 13 * pixel));
		bbs.add(new AxisAlignedBB(2 * pixel, 0 * pixel, 2 * pixel, 14 * pixel, 1 * pixel, 14 * pixel));
		
		for(final AxisAlignedBB bb : bbs){
			list.add(BlockUtil.rotateBoundingBox(bb, facing));
		}
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
