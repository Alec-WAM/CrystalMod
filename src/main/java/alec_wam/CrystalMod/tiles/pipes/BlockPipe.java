package alec_wam.CrystalMod.tiles.pipes;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockPipe extends ContainerBlock {

	public enum ConnectionType implements IStringSerializable {
		NONE, PIPE, EXTERNAL;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	public static final EnumProperty<ConnectionType> UP = EnumProperty.create("up", ConnectionType.class);
	public static final EnumProperty<ConnectionType> DOWN = EnumProperty.create("down", ConnectionType.class);
	public static final EnumProperty<ConnectionType> NORTH = EnumProperty.create("north", ConnectionType.class);
	public static final EnumProperty<ConnectionType> SOUTH = EnumProperty.create("south", ConnectionType.class);
	public static final EnumProperty<ConnectionType> EAST = EnumProperty.create("east", ConnectionType.class);
	public static final EnumProperty<ConnectionType> WEST = EnumProperty.create("west", ConnectionType.class);
	
	public final NetworkType type;
	public BlockPipe(NetworkType type, Properties builder) {
		super(builder);
		this.type = type;
		this.setDefaultState(this.stateContainer.getBaseState().with(UP, ConnectionType.NONE).with(NORTH, ConnectionType.NONE).with(EAST, ConnectionType.NONE).with(SOUTH, ConnectionType.NONE).with(WEST, ConnectionType.NONE));
	}

	public static ConnectionType getConnection(BlockState state, Direction facing){
		switch(facing){
			default : case UP :
				return state.get(UP);
			case DOWN :
				return state.get(DOWN);
			case NORTH :
				return state.get(NORTH);
			case SOUTH : 
				return state.get(SOUTH);
			case EAST : 
				return state.get(EAST);
			case WEST :
				return state.get(WEST);
		}
	}
	
	public enum PipePart {
		 CENTER, PIPE, CONNECTOR;
	}
	private static final VoxelShape SHAPE_MIDDLE = BlockUtil.makeVoxelShape(new AxisAlignedBB(5.0F, 5.0F, 5.0F, 11.0F, 11.0F, 11.0F));
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileEntityPipeBase){
			TileEntityPipeBase pipe = (TileEntityPipeBase)tile;
			VoxelShape finalShape = BlockUtil.makeVoxelShape(new AxisAlignedBB(5.0F, 5.0F, 5.0F, 11.0F, 11.0F, 11.0F));
			float min = 5.6F;
			float max = 10.4F;
			float middleSize = 5.0F;
			float minSmall = 4.0F;
	        float maxSmall = 12.0F;
	        float minLarge = 3.0F;
	        float maxLarge = 13.0F;
			AxisAlignedBB bbPipe = new AxisAlignedBB(min, 16.0F - middleSize, min, max, 16.0F, max);
			AxisAlignedBB bbConSmall = new AxisAlignedBB(minSmall, 14.0F, minSmall, maxSmall, 15.0F, maxSmall);
			AxisAlignedBB bbConLarge = new AxisAlignedBB(minLarge, 15.0F, minLarge, maxLarge, 16.0F, maxLarge);
			for(Direction facing : Direction.values()){
				if(pipe.isConnectedTo(facing) || pipe.hasExternalConnection(facing)){
					VoxelShape pipeBar = BlockUtil.makeVoxelShape(BlockUtil.rotateBoundingBox(bbPipe, facing, 16.0F));
					finalShape = VoxelShapes.or(finalShape, pipeBar);
				}
				if(pipe.hasExternalConnection(facing)){
					VoxelShape connectorBig = BlockUtil.makeVoxelShape(BlockUtil.rotateBoundingBox(bbConLarge, facing, 16.0F));
					VoxelShape connectorSmall = BlockUtil.makeVoxelShape(BlockUtil.rotateBoundingBox(bbConSmall, facing, 16.0F));
					VoxelShape connector = VoxelShapes.or(connectorBig, connectorSmall);
					finalShape = VoxelShapes.or(finalShape, connector);
				}
			}
			return finalShape;
		}
		return SHAPE_MIDDLE;
	}
	
	public static class PipeHitData {
		public Direction face;
		public PipePart part;
		public PipeHitData(Direction facing, PipePart part){
			this.face = facing;
			this.part = part;
		}
	}
	
	@Override
	public RayTraceResult getRayTraceResult(BlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityPipeBase){
			TileEntityPipeBase pipe = (TileEntityPipeBase)tile;
			float min = 5.6F;
			float max = 10.4F;
			float middleSize = 5.0F;
			float minSmall = 4.0F;
	        float maxSmall = 12.0F;
	        float minLarge = 3.0F;
	        float maxLarge = 13.0F;
			AxisAlignedBB bbPipe = new AxisAlignedBB(min, 16.0F - middleSize, min, max, 16.0F, max);
			AxisAlignedBB bbConSmall = new AxisAlignedBB(minSmall, 14.0F, minSmall, maxSmall, 15.0F, maxSmall);
			AxisAlignedBB bbConLarge = new AxisAlignedBB(minLarge, 15.0F, minLarge, maxLarge, 16.0F, maxLarge);
			for(Direction facing : Direction.values()){
				if(pipe.isConnectedTo(facing) || pipe.hasExternalConnection(facing)){
					VoxelShape pipeBar = BlockUtil.makeVoxelShape(BlockUtil.rotateBoundingBox(bbPipe, facing, 16.0F));
					
					if(pipe.hasExternalConnection(facing)){
						VoxelShape connectorBig = BlockUtil.makeVoxelShape(BlockUtil.rotateBoundingBox(bbConLarge, facing, 16.0F));
						VoxelShape connectorSmall = BlockUtil.makeVoxelShape(BlockUtil.rotateBoundingBox(bbConSmall, facing, 16.0F));
						VoxelShape connector = VoxelShapes.or(connectorBig, connectorSmall);
						RayTraceResult res = connector.rayTrace(start, end, pos);
						if (res != null) {
							res.hitInfo = new PipeHitData(facing, PipePart.CONNECTOR);
							return res;
						}
					}
					
					RayTraceResult res = pipeBar.rayTrace(start, end, pos);
					if (res != null) {
						res.hitInfo = new PipeHitData(facing, PipePart.PIPE);
						return res;
					}
				}
			}
		}
	    
		return original;
    }
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		try {
			return type.tileClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, WEST, SOUTH);
	}
	
	@Override
	public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
    {
		TileEntity tile = world.getTileEntity(pos);		
		if(tile instanceof TileEntityPipeBase){
			TileEntityPipeBase pipe = (TileEntityPipeBase)tile;
			Map<Direction, ConnectionType> map = Maps.newHashMap();
			for(Direction facing : Direction.values()){
				if(pipe.isConnectedTo(facing) && pipe.getConnectionSetting(facing) != PipeConnectionMode.DISABLED){
					map.put(facing, ConnectionType.PIPE);
				} else if(pipe.hasExternalConnection(facing) && pipe.getConnectionSetting(facing) != PipeConnectionMode.DISABLED){
					map.put(facing, ConnectionType.EXTERNAL);
				} else {
					map.put(facing, ConnectionType.NONE);
				}
			}
			return state.with(UP, map.get(Direction.UP))
					.with(DOWN, map.get(Direction.DOWN))
					.with(NORTH, map.get(Direction.NORTH))
					.with(SOUTH, map.get(Direction.SOUTH))
					.with(EAST, map.get(Direction.EAST))
					.with(WEST, map.get(Direction.WEST));
		}
		return state;
    }
	
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor){
		super.onNeighborChange(state, world, pos, neighbor);
		TileEntity tile = world.getTileEntity(pos);		
		if(tile instanceof TileEntityPipeBase){
			((TileEntityPipeBase)tile).rebuildConnections = true;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileEntityPipeBase) {
				TileEntityPipeBase pipe = (TileEntityPipeBase)tileentity;
				List<ItemStack> drops = pipe.getDrops();
				for(ItemStack stack : drops) {
					if (!stack.isEmpty()) {
						InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
					}
				}
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		} 
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
    {
		Direction side = ray.getFace();
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityPipeBase){
			TileEntityPipeBase pipe = (TileEntityPipeBase)tile;
			//TODO Handle custom raytrace
			float f = player.rotationPitch;
			float f1 = player.rotationYaw;
			Vec3d vec3d = player.getEyePosition(1.0F);
			float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
			float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
			float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
			float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
			float f6 = f3 * f4;
			float f7 = f2 * f4;
			double d0 = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();;
			Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);			
			RayTraceResult result = getRayTraceResult(state, worldIn, pos, vec3d, vec3d1, ray);			
			if(result !=null){
				PipeHitData hitData = null;
				if(result.hitInfo instanceof PipeHitData){
					hitData = (PipeHitData)result.hitInfo;
					if(hitData.part == PipePart.CONNECTOR){
						if(ToolUtil.isHoldingWrench(player, hand)){
							if(worldIn.isRemote)return true;
							pipe.incrsConnectionMode(hitData.face);
							return true;
						} else {
							return pipe.openConnector(player, hand, hitData.face);
						}
					}
					if(hitData.part == PipePart.PIPE){
						if(ToolUtil.isHoldingWrench(player, hand)){
							if(worldIn.isRemote)return true;
							PipeNetworkBuilder.unlinkPipes(pipe, hitData.face);
							return true;
						}
					}
				} else {
					hitData = new PipeHitData(side, null);
					if(ToolUtil.isHoldingWrench(player, hand)){
						if(player.isSneaking()){
							if(worldIn.isRemote)return true;
							return ToolUtil.breakBlockWithWrench(worldIn, pos, player, hand);
						}
						if(pipe.getConnectionSetting(side) == PipeConnectionMode.DISABLED){
							if(worldIn.isRemote)return true;
							PipeNetworkBuilder.linkPipes(pipe, side);
							return true;
						}
					}
				}
				return pipe.onActivated(worldIn, player, hand, hitData);
			} 
		}
		return false;
    }
	
}
