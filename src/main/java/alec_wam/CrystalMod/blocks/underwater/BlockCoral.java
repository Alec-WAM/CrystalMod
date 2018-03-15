package alec_wam.CrystalMod.blocks.underwater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.block.ICustomRaytraceBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.connected.BlockConnectedTexture;
import alec_wam.CrystalMod.blocks.connected.ConnectedBlockState;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.pipes.CollidableComponent;
import alec_wam.CrystalMod.tiles.pipes.RaytraceResult;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCoral extends BlockColored implements ICustomModel, ICustomRaytraceBlock {

	public BlockCoral() {
		super(Material.WATER);
        setTickRandomly(true);
        setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setHardness(1.0F);
		setDefaultState(getDefaultState()
				.withProperty(BlockLiquid.LEVEL, 0)
				.withProperty(BlockConnectedTexture.CONNECTED_DOWN, Boolean.FALSE)
				.withProperty(BlockConnectedTexture.CONNECTED_EAST, Boolean.FALSE)
				.withProperty(BlockConnectedTexture.CONNECTED_NORTH, Boolean.FALSE)
				.withProperty(BlockConnectedTexture.CONNECTED_SOUTH, Boolean.FALSE)
				.withProperty(BlockConnectedTexture.CONNECTED_UP, Boolean.FALSE)
				.withProperty(BlockConnectedTexture.CONNECTED_WEST, Boolean.FALSE));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		StateMapperBase ignoreState = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				EnumDyeColor color = state.getValue(COLOR);
				return new ModelResourceLocation(getRegistryName()+"_"+color.getUnlocalizedName().toLowerCase(), "normal");
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
		for(EnumDyeColor color : EnumDyeColor.values()){
			ModelCoral model = new ModelCoral(color);
			ModelResourceLocation inv = new ModelResourceLocation(getRegistryName()+"_"+color.getUnlocalizedName().toLowerCase(), "inventory");
	    	ClientProxy.registerCustomModel(inv, model);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), color.getMetadata(), inv);
	        ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName()+"_"+color.getUnlocalizedName().toLowerCase(), "normal"), model);
		}
	}
	
	/**
	 * This method will generate a cluster of Coral that will randomly choose a color for each piece of Coral.
	 * @param world 
	 * @param pos
	 * @param size
	 * @param ignoreSpread if this is enabled the search will not avoid duplicate directions (Forms a tighter clump)
	 * @param notifyBlocks
	 */
	public static void generateCoralCluster(World world, BlockPos pos, int size, boolean ignoreSpread, boolean notifyBlocks){
		BlockPos nodePos = pos;
		EnumFacing lastFace = null;
		for(int i = 0; i < size; i++){
			EnumDyeColor color = EnumDyeColor.byMetadata(MathHelper.getInt(Util.rand, 0, 15));
			if(ModBlocks.coral.canPlaceBlockAt(world, nodePos)){
				world.setBlockState(nodePos, ModBlocks.coral.getDefaultState().withProperty(BlockColored.COLOR, color), notifyBlocks ? 3 : 2);
				boolean found = false;
				search: for(int t = 0; t < 6; t++){
					EnumFacing face = EnumFacing.VALUES[MathHelper.getInt(Util.rand, 0, 5)];
					if(!ignoreSpread && lastFace !=null && face == lastFace){
						continue search;
					}					
					if(ModBlocks.coral.canPlaceBlockAt(world, nodePos.offset(face))){
						nodePos = nodePos.offset(face);
						lastFace = face;
						found = true;
						break search;
					}
				}
				if(!found){
					break;
				}
			}
		}
	}
	
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}	
	
	@Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return new ConnectedBlockState(state, world, pos);
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {
      return state.withProperty(BlockConnectedTexture.CONNECTED_DOWN,  isSideConnectable(world, position, EnumFacing.DOWN))
                  .withProperty(BlockConnectedTexture.CONNECTED_EAST,  isSideConnectable(world, position, EnumFacing.EAST))
                  .withProperty(BlockConnectedTexture.CONNECTED_NORTH, isSideConnectable(world, position, EnumFacing.NORTH))
                  .withProperty(BlockConnectedTexture.CONNECTED_SOUTH, isSideConnectable(world, position, EnumFacing.SOUTH))
                  .withProperty(BlockConnectedTexture.CONNECTED_UP,    isSideConnectable(world, position, EnumFacing.UP))
                  .withProperty(BlockConnectedTexture.CONNECTED_WEST,  isSideConnectable(world, position, EnumFacing.WEST))
                  .withProperty(BlockLiquid.LEVEL, 0);
    }
    
    public boolean isSideConnectable(IBlockAccess world, BlockPos pos, EnumFacing side) {
    	final IBlockState original = world.getBlockState(pos);
    	final IBlockState connected = world.getBlockState(pos.offset(side));

    	return original != null && connected != null && canConnect(world, pos, original, connected, side);
    }

    public boolean canConnect(IBlockAccess world, BlockPos pos, @Nonnull IBlockState original, @Nonnull IBlockState connected, EnumFacing side) {
    	if(connected.isSideSolid(world, pos, side)){
    		return true;
    	}
    	return original.getBlock() == connected.getBlock();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
    	return new BlockStateContainer(this, new IProperty[] { COLOR, BlockLiquid.LEVEL, BlockConnectedTexture.CONNECTED_DOWN, BlockConnectedTexture.CONNECTED_UP, BlockConnectedTexture.CONNECTED_NORTH, BlockConnectedTexture.CONNECTED_SOUTH, BlockConnectedTexture.CONNECTED_WEST, BlockConnectedTexture.CONNECTED_EAST });
    }
	
	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if(blockIn !=this && blockIn !=Blocks.FLOWING_WATER){
        	this.checkAndDropBlock(worldIn, pos, state);
        }
    }

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        checkAndDropBlock(worldIn, pos, state);
    }
	
	public boolean canBlockStay(final World worldIn, final BlockPos pos, final IBlockState state) {
		for(EnumFacing facing : EnumFacing.HORIZONTALS){
			BlockPos offset = pos.offset(facing);
			if(worldIn.getBlockState(offset).getBlock() == Blocks.WATER || worldIn.getBlockState(offset).getBlock() == this){
				return true;
			}
		}
		return worldIn.getBlockState(pos.up()).getBlock() == Blocks.WATER || worldIn.getBlockState(pos.up()).getBlock() == this;
	}

	@Override
	public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
		return canBlockStay(worldIn, pos, getDefaultState());
	}

	@Override
	public void onBlockDestroyedByPlayer(final World worldIn, final BlockPos pos, final IBlockState state) {
		worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
	}

	protected void checkAndDropBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
		if (!canBlockStay(worldIn, pos, state)) {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
		}
	}
	
	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return false;
    }
	
	//RayTrace
	public static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
	{
		for(final AxisAlignedBB bb : getBounds(worldIn, pos)){
			addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		AxisAlignedBB minBB = DEFAULT_AABB;

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
			minBB = DEFAULT_AABB;
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
			return DEFAULT_AABB;
		}
		return bounds;
	}

	@Override
	public Collection<? extends CollidableComponent> getCollidableComponents(World world, BlockPos pos) {
		final List<CollidableComponent> collidables = new ArrayList<CollidableComponent>();
		for(final AxisAlignedBB bb : getBounds(world, pos)){
			collidables.add(new CollidableComponent(bb, null, 0));			
		}
		return collidables;
	}

	private List<AxisAlignedBB> getBounds(World world, BlockPos pos){
		List<AxisAlignedBB> list = Lists.newArrayList();
		list.add(DEFAULT_AABB);
		float min = 0.25f;
		float max = 0.75f;
		float newMin = 0.36f;
		float newMax = 0.64f;
		
		if(isSideConnectable(world, pos, EnumFacing.UP))list.add(new AxisAlignedBB(newMin, max, newMin, newMax, 1.0F, newMax));
		if(isSideConnectable(world, pos, EnumFacing.DOWN))list.add(new AxisAlignedBB(newMin, 0.0F, newMin, newMax, min, newMax));
		if(isSideConnectable(world, pos, EnumFacing.WEST))list.add(new AxisAlignedBB(0.0F, newMin, newMin, min, newMax, newMax));
		if(isSideConnectable(world, pos, EnumFacing.EAST))list.add(new AxisAlignedBB(max, newMin, newMin, 1.0F, newMax, newMax));
		if(isSideConnectable(world, pos, EnumFacing.NORTH))list.add(new AxisAlignedBB(newMin, newMin, 0.0F, newMax, newMax, min));
		if(isSideConnectable(world, pos, EnumFacing.SOUTH))list.add(new AxisAlignedBB(newMin, newMin, max, newMax, newMax, 1.0F));
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
		bounds = DEFAULT_AABB;
	}
	
}
