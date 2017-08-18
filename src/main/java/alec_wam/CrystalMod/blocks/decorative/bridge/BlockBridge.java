package alec_wam.CrystalMod.blocks.decorative.bridge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.block.ICustomRaytraceBlock;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.NormalBlockStateMapper;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.tiles.machine.FakeTileState;
import alec_wam.CrystalMod.tiles.pipes.CollidableComponent;
import alec_wam.CrystalMod.tiles.pipes.RaytraceResult;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBridge extends EnumBlock<WoodenBlockProperies.WoodType> implements ITileEntityProvider, ICustomModel, ICustomRaytraceBlock {

	public BlockBridge() {
		super(Material.WOOD, WoodenBlockProperies.WOOD, WoodType.class);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(0.8F);
		this.setResistance(4.0F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new NormalBlockStateMapper());
		for(WoodType type : WoodType.values()){
			ResourceLocation baseLocation = getRegistryName();
			ModelResourceLocation inv = new ModelResourceLocation(baseLocation, "inventory");
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), inv);
			ClientProxy.registerCustomModel(inv, ModelBridge.INSTANCE);
			ClientProxy.registerCustomModel(new ModelResourceLocation(baseLocation, "normal"), ModelBridge.INSTANCE);
		}
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
        return new FakeTileState(state, world, pos, tile);
    }

    @Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) ? this.canBlockStay(worldIn, pos) : false;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    @Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.canBlockStay(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos)
    {
    	IBlockState below = worldIn.getBlockState(pos.down());
    	if(below.getMaterial().isLiquid() || worldIn.isAirBlock(pos.down())){
    		for(EnumFacing face : EnumFacing.HORIZONTALS){
    			IBlockState state = worldIn.getBlockState(pos.offset(face));
    			if(state.getBlock() == this){
    				return true;
    			}
    		}
    	}
        return !below.isFullBlock() || below.isSideSolid(worldIn, pos, EnumFacing.UP);
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hX, float hY, float hZ){
    	ItemStack held = player.getHeldItem(hand);
    	TileEntity tile = world.getTileEntity(pos);
    	if(tile == null || !(tile instanceof TileBridge)) return false;
    	TileBridge bridge = (TileBridge)tile;
    	EnumFacing side = null;
    	if(hX < 0.2D){
    		side = EnumFacing.WEST;
    	}
    	if(hX > 0.8D){
    		side = EnumFacing.EAST;
    	}
    	if(hZ < 0.2D){
    		side = EnumFacing.NORTH;
    	}
    	if(hZ > 0.8D){
    		side = EnumFacing.SOUTH;
    	}
    	if(ItemStackTools.isValid(held)){
    		if(held.getItem() == Item.getItemFromBlock(Blocks.LOG)){
    			if(side !=null && bridge.getBase(side) == null){
    				bridge.setBase(side, BlockPlanks.EnumType.byMetadata(held.getMetadata()));
    				if(!player.capabilities.isCreativeMode){
    					player.setHeldItem(hand, ItemUtil.consumeItem(held));
    				}
    				BlockUtil.markBlockForUpdate(world, pos);
    				return true;
    			}
    		}
    		if(held.getItem() == Item.getItemFromBlock(Blocks.LOG2)){
    			if(side !=null && bridge.getBase(side) == null){
    				bridge.setBase(side, BlockPlanks.EnumType.byMetadata(held.getMetadata()+4));
    				if(!player.capabilities.isCreativeMode){
    					player.setHeldItem(hand, ItemUtil.consumeItem(held));
    				}
    				BlockUtil.markBlockForUpdate(world, pos);
    				return true;
    			}
    		}
    		if(held.getItem() == Items.STICK){
    			if(side !=null && bridge.getBase(side) !=null){
    				RaytraceResult result = BlockUtil.doRayTrace(world, pos.getX(), pos.getY(), pos.getZ(), player, this);
    				if(result !=null){
    					if(result.component !=null && result.component.data !=null){
    						if(result.component.data instanceof Integer){
    							if(result.component.data == Integer.valueOf(1) || result.component.data == Integer.valueOf(2)){
    								if(facing == EnumFacing.UP){
    									if(bridge.hasPost(side, 0) && bridge.hasPost(side, 1) && !bridge.hasPost(side, 2)){
    		    							bridge.setPost(side, 2, true);
    		    							if(!player.capabilities.isCreativeMode){
    		    		    					player.setHeldItem(hand, ItemUtil.consumeItem(held));
    		    		    				}
    		    							BlockUtil.markBlockForUpdate(world, pos);
    		    		    				return true;
    		    						}
    								}
    							}
    						}
    					}
    				}
    				if(side.getAxis() == Axis.X){
    					if(hZ > 0.2D*1 && hZ < 0.2*2){
    						if(!bridge.hasPost(side, 0)){
    							bridge.setPost(side, 0, true);
    							if(!player.capabilities.isCreativeMode){
    		    					player.setHeldItem(hand, ItemUtil.consumeItem(held));
    		    				}
    							BlockUtil.markBlockForUpdate(world, pos);
    		    				return true;
    						}
    			    	}
    			    	if(hZ > 0.2D*3 && hZ < 0.2*4){
    			    		if(!bridge.hasPost(side, 1)){
    							bridge.setPost(side, 1, true);
    							if(!player.capabilities.isCreativeMode){
    		    					player.setHeldItem(hand, ItemUtil.consumeItem(held));
    		    				}
    							BlockUtil.markBlockForUpdate(world, pos);
    		    				return true;
    						}
    			    	}
    				}
    				if(side.getAxis() == Axis.Z){
    					if(hX > 0.2D*1 && hX < 0.2*2){
    						if(!bridge.hasPost(side, 0)){
    							bridge.setPost(side, 0, true);
    							if(!player.capabilities.isCreativeMode){
    		    					player.setHeldItem(hand, ItemUtil.consumeItem(held));
    		    				}
    							BlockUtil.markBlockForUpdate(world, pos);
    		    				return true;
    						}
    			    	}
    			    	if(hX > 0.2D*3 && hX < 0.2*4){
    			    		if(!bridge.hasPost(side, 1)){
    							bridge.setPost(side, 1, true);
    							if(!player.capabilities.isCreativeMode){
    		    					player.setHeldItem(hand, ItemUtil.consumeItem(held));
    		    				}
    							BlockUtil.markBlockForUpdate(world, pos);
    		    				return true;
    						}
    			    	}
    				}
    			}
    		}
    	}
    	return false;
    }
    
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    	boolean breakBlock = true;
    	List<ItemStack> drop = new ArrayList<ItemStack>();
    	TileEntity tile = world.getTileEntity(pos);
    	if(tile == null || !(tile instanceof TileBridge)) {
    		world.setBlockToAir(pos);
    		return true;
    	}
    	TileBridge bridge = (TileBridge)tile;
    	RaytraceResult result = BlockUtil.doRayTrace(world, pos.getX(), pos.getY(), pos.getZ(), player, this);
		if(result !=null && result.component !=null){
			EnumFacing side = result.component.dir;
			if(result.component.data !=null){
				if(result.component.data instanceof Integer){
					int pole = (Integer)result.component.data;
					if(side !=null){
						if(pole > 0){
							int index = pole-1;
							bridge.setPost(side, index, false);
							if(index < 2){
								if(bridge.hasPost(side, 2)){
									bridge.setPost(side, 2, false);
									drop.add(new ItemStack(Items.STICK));
								}
							}
							drop.add(new ItemStack(Items.STICK));
							breakBlock = false;
						} else {
							final int meta = bridge.getBase(side).getMetadata();
							bridge.setBase(side, null);
							for(int i = 0; i < 3; i++){
								if(bridge.hasPost(side, i)){
									bridge.setPost(side, i, false);
									drop.add(new ItemStack(Items.STICK));
								}
							}
							if(meta < 4)drop.add(new ItemStack(Blocks.LOG, 1, meta));
							else drop.add(new ItemStack(Blocks.LOG2, 1, meta-4));
							breakBlock = false;
						}
					}
				}
			}
		}
    	
		if (!breakBlock) {
    		world.notifyBlockUpdate(pos, state, state, 3);
    	}
    	
    	if (!world.isRemote && !player.capabilities.isCreativeMode) {
    		for (ItemStack st : drop) {
    			ItemUtil.spawnItemInWorldWithoutMotion(world, st, pos);
    		}
	    }
		
    	if(breakBlock){
    		world.setBlockToAir(pos);
    		return true;
    	}
    	return false;
    }
    
  //RayTrace
    
    @SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(final IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB mask, final List<AxisAlignedBB> list, final Entity collidingEntity, boolean bool) {
    	Collection<CollidableComponent> bounds = getCollidableComponents(worldIn, pos);
    	for (CollidableComponent bnd : bounds) {
    		setBounds(bnd.bound);
    		super.addCollisionBoxToList(state, worldIn, pos, mask, list, collidingEntity, bool);
    	}
    	resetBounds();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
    	EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		AxisAlignedBB minBB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

		List<RaytraceResult> results = BlockUtil.doRayTraceAll(world, pos.getX(),	pos.getY(), pos.getZ(), player, this);
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
			double BOTTOM_THICKNESS = 0.2D;
			minBB = new AxisAlignedBB(0, 0, 0, 1, BOTTOM_THICKNESS, 1);
		}
		return new AxisAlignedBB(pos.getX() + minBB.minX, pos.getY() + minBB.minY, pos.getZ() + minBB.minZ, pos.getX() + minBB.maxX, pos.getY() + minBB.maxY, pos.getZ() + minBB.maxZ);
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
			double BOTTOM_THICKNESS = 0.2D;
			return new AxisAlignedBB(0, 0, 0, 1, BOTTOM_THICKNESS, 1);
		}
		return bounds;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileBridge();
	}

	@SuppressWarnings("deprecation")
	@Override
	public RayTraceResult defaultRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d origin, Vec3d direction) {
		return super.collisionRayTrace(blockState, world, pos, origin, direction);
	}	

	@Override
	public List<CollidableComponent> getCollidableComponents(World world, BlockPos pos){
		List<CollidableComponent> list = Lists.newArrayList();
		double BOTTOM_THICKNESS = 0.2D;
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileBridge)){
			list.add(new CollidableComponent(new AxisAlignedBB(0, 0, 0, 1, BOTTOM_THICKNESS, 1), null, 0));
			return list;
		}
		TileBridge bridge = (TileBridge)tile;
		boolean left = false;
        boolean right = false;
        boolean front = false;
        boolean back = false;
        
        if(bridge !=null){
        	left = bridge.getBase(EnumFacing.WEST) !=null;
        	right = bridge.getBase(EnumFacing.EAST) !=null;
        	front = bridge.getBase(EnumFacing.NORTH) !=null;
        	back = bridge.getBase(EnumFacing.SOUTH) !=null;
        }
        
		boolean leftPole1 = left && bridge.hasPost(EnumFacing.WEST, 0);
		boolean leftPole2 = left && bridge.hasPost(EnumFacing.WEST, 1);
		boolean leftTopPole = left && bridge.hasPost(EnumFacing.WEST, 2);
		
		boolean rightPole1 = right && bridge.hasPost(EnumFacing.EAST, 0);
		boolean rightPole2 = right && bridge.hasPost(EnumFacing.EAST, 1);
		boolean rightTopPole = right && bridge.hasPost(EnumFacing.EAST, 2);
		
		boolean frontPole1 = front && bridge.hasPost(EnumFacing.NORTH, 0);
		boolean frontPole2 = front && bridge.hasPost(EnumFacing.NORTH, 1);
		boolean frontTopPole = front && bridge.hasPost(EnumFacing.NORTH, 2);
		
		boolean backPole1 = back && bridge.hasPost(EnumFacing.SOUTH, 0);
		boolean backPole2 = back && bridge.hasPost(EnumFacing.SOUTH, 1);
		boolean backTopPole = back && bridge.hasPost(EnumFacing.SOUTH, 2);
		
		float pixel = 1.0f/16.0f;
		float sidewidth = 0.2f;
		float chunk = 0.2f;
        float poleHeight = pixel*12;
        float[] polePos = {chunk*0, chunk*1, chunk*2, chunk*3, chunk*4};
        
        float minSideZ = front ? sidewidth : 0.0f;
    	float maxSideZ = back ? 1.0f-sidewidth : 1.0F;
    	float minTopZ = frontTopPole ? sidewidth : 0.0f;
    	float maxTopZ = backTopPole ? 1.0f-sidewidth : 1.0F;
                
		if(left) {
			list.add(new CollidableComponent(new AxisAlignedBB(0, 0, minSideZ, sidewidth, chunk, maxSideZ), EnumFacing.WEST, 0));
		}
		if(leftPole1) {
			list.add(new CollidableComponent(new AxisAlignedBB(0, BOTTOM_THICKNESS, polePos[1], sidewidth, poleHeight, polePos[1]+chunk), EnumFacing.WEST, 1));
		}
		if(leftPole2) {
			list.add(new CollidableComponent(new AxisAlignedBB(0, BOTTOM_THICKNESS, polePos[3], sidewidth, poleHeight, polePos[3]+chunk), EnumFacing.WEST, 2));
		}
		if(leftTopPole) {
			list.add(new CollidableComponent(new AxisAlignedBB(0, poleHeight, minTopZ, sidewidth, poleHeight+chunk, maxTopZ), EnumFacing.WEST, 3));
		}
		
		if(right) {
			list.add(new CollidableComponent(new AxisAlignedBB(1-sidewidth, 0, minSideZ, 1, chunk, maxSideZ), EnumFacing.EAST, 0));
		}
		if(rightPole1) {
			list.add(new CollidableComponent(new AxisAlignedBB(1-sidewidth, BOTTOM_THICKNESS, polePos[1], 1, poleHeight, polePos[1]+chunk), EnumFacing.EAST, 1));
		}
		if(rightPole2) {
			list.add(new CollidableComponent(new AxisAlignedBB(1-sidewidth, BOTTOM_THICKNESS, polePos[3], 1, poleHeight, polePos[3]+chunk), EnumFacing.EAST, 2));
		}
		if(rightTopPole) {
			list.add(new CollidableComponent(new AxisAlignedBB(1-sidewidth, poleHeight, minTopZ, 1, poleHeight+chunk, maxTopZ), EnumFacing.EAST, 3));
		}
		
		if(front) {
			list.add(new CollidableComponent(new AxisAlignedBB(0, 0, 0, 1, chunk, sidewidth), EnumFacing.NORTH, 0));
		}
		if(frontPole1) {
			list.add(new CollidableComponent(new AxisAlignedBB(polePos[1], BOTTOM_THICKNESS, 0, polePos[1]+chunk, poleHeight, sidewidth), EnumFacing.NORTH, 1));
		}
		if(frontPole2) {
			list.add(new CollidableComponent(new AxisAlignedBB(polePos[3], BOTTOM_THICKNESS, 0, polePos[3]+chunk, poleHeight, sidewidth), EnumFacing.NORTH, 2));
		}
		if(frontTopPole) {
			list.add(new CollidableComponent(new AxisAlignedBB(0, poleHeight, 0, 1, poleHeight+chunk, sidewidth), EnumFacing.NORTH, 3));
		}
		
		if(back){
			list.add(new CollidableComponent(new AxisAlignedBB(0, 0, 1-sidewidth, 1, chunk, 1), EnumFacing.SOUTH, 0));
		}
		if(backPole1) {
			list.add(new CollidableComponent(new AxisAlignedBB(polePos[1], BOTTOM_THICKNESS, 1-sidewidth, polePos[1]+chunk, poleHeight, 1), EnumFacing.SOUTH, 1));
		}
		if(backPole2) {
			list.add(new CollidableComponent(new AxisAlignedBB(polePos[3], BOTTOM_THICKNESS, 1-sidewidth, polePos[3]+chunk, poleHeight, 1), EnumFacing.SOUTH, 2));
		}
		if(backTopPole) {
			list.add(new CollidableComponent(new AxisAlignedBB(0, poleHeight, 1-sidewidth, 1, poleHeight+chunk, 1), EnumFacing.SOUTH, 3));
		}
		
		float minx = !left ? 0 : sidewidth;
        float maxx = 1-(!right ? 0 : sidewidth);
        float minz = !front ? 0 : sidewidth;
        float maxz = 1-(!back ? 0 : sidewidth);
		list.add(new CollidableComponent(new AxisAlignedBB(minx, 0, minz, maxx, BOTTOM_THICKNESS, maxz), null, 0));
		
		return list;
	}

	@Override
	public void setBounds(AxisAlignedBB bound) {
		bounds = bound;
	}

	@Override
	public void resetBounds() {
		bounds = Block.FULL_BLOCK_AABB;
	}
	
	public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			WoodType type = state.getValue(WoodenBlockProperies.WOOD);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			nameOverride = state.getBlock().getRegistryName().getResourcePath() + "_" + type.getName();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}

}
