package alec_wam.CrystalMod.tiles.pipes.liquid.basic;

import java.util.List;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.proxy.ClientProxy;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBasicFluidPipe extends BlockContainer implements ICustomModel {
	
	public static enum ConnectionType implements IStringSerializable {
		NONE, PIPE, TILE;
		ConnectionType(){}

		public boolean isConnection(){
			return this == PIPE || this == TILE;
		}
		
		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	public static final PropertyEnum<ConnectionType> CONNECTED_DOWN = PropertyEnum.<ConnectionType>create("connected_down", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> CONNECTED_UP = PropertyEnum.<ConnectionType>create("connected_up", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> CONNECTED_NORTH = PropertyEnum.<ConnectionType>create("connected_north", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> CONNECTED_SOUTH = PropertyEnum.<ConnectionType>create("connected_south", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> CONNECTED_WEST = PropertyEnum.<ConnectionType>create("connected_west", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> CONNECTED_EAST = PropertyEnum.<ConnectionType>create("connected_east", ConnectionType.class);
    public BlockBasicFluidPipe() {
		super(Material.GLASS);
		this.setHardness(0.3f);
		setHarvestLevel("pickaxe", -1);
		this.setSoundType(SoundType.GLASS);
		this.setCreativeTab(CrystalMod.tabBlocks);
		// By default none of the sides are connected
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(CONNECTED_DOWN, ConnectionType.NONE)
				.withProperty(CONNECTED_EAST, ConnectionType.NONE)
				.withProperty(CONNECTED_NORTH, ConnectionType.NONE)
				.withProperty(CONNECTED_SOUTH, ConnectionType.NONE)
				.withProperty(CONNECTED_UP, ConnectionType.NONE)
				.withProperty(CONNECTED_WEST, ConnectionType.NONE));
	}

	@Override
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
      return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
      return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelResourceLocation inv = new ModelResourceLocation(this.getRegistryName(), "inventory");
    	ClientProxy.registerCustomModel(inv, new ModelBasicFluidPipe());
    	inv = new ModelResourceLocation(this.getRegistryName(), "normal");
    	ClientProxy.registerCustomModel(inv, new ModelBasicFluidPipe());
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return null;
	}
	
	//AABB
	private static final AxisAlignedBB CENTER_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 1, 0.6875);
	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 1);
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.3125, 0.3125, 1, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB[] SIDE_BOXES = new AxisAlignedBB[] {
			DOWN_AABB, UP_AABB, NORTH_AABB, SOUTH_AABB, WEST_AABB, EAST_AABB
	};
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		double minX = CENTER_AABB.minX, minY = CENTER_AABB.minY, minZ = CENTER_AABB.minZ, 
				maxX = CENTER_AABB.maxX, maxY = CENTER_AABB.maxY, maxZ = CENTER_AABB.maxZ;

		state = getActualState(state, source, pos);
		if(state.getValue(CONNECTED_DOWN).isConnection()) minY = 0;
		if(state.getValue(CONNECTED_UP).isConnection()) maxY = 1;
		if(state.getValue(CONNECTED_NORTH).isConnection()) minZ = 0;
		if(state.getValue(CONNECTED_SOUTH).isConnection()) maxZ = 1;
		if(state.getValue(CONNECTED_WEST).isConnection()) minX = 0;
		if(state.getValue(CONNECTED_EAST).isConnection()) maxX = 1;

		AxisAlignedBB aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		return aabb;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return getBoundingBox(state, worldIn, pos).offset(pos);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		if(!isActualState)
			state = getActualState(state, worldIn, pos);

		addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER_AABB);
		if(state.getValue(CONNECTED_DOWN).isConnection())addCollisionBoxToList(pos, entityBox, collidingBoxes, DOWN_AABB);
		if(state.getValue(CONNECTED_UP).isConnection())addCollisionBoxToList(pos, entityBox, collidingBoxes, UP_AABB);
		if(state.getValue(CONNECTED_NORTH).isConnection())addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
		if(state.getValue(CONNECTED_SOUTH).isConnection())addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
		if(state.getValue(CONNECTED_WEST).isConnection())addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
		if(state.getValue(CONNECTED_EAST).isConnection())addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
	}

}
