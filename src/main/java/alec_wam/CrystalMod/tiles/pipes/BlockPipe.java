package alec_wam.CrystalMod.tiles.pipes;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockPipe extends BlockContainer {

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

	public static ConnectionType getConnection(IBlockState state, EnumFacing facing){
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
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
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
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, WEST, SOUTH);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockReader world, BlockPos pos)
    {
		TileEntity tile = world.getTileEntity(pos);		
		if(tile instanceof TileEntityPipeBase){
			TileEntityPipeBase pipe = (TileEntityPipeBase)tile;
			Map<EnumFacing, ConnectionType> map = Maps.newHashMap();
			for(EnumFacing facing : EnumFacing.values()){
				if(pipe.isConnectedTo(facing) && pipe.getConnectionSetting(facing) != PipeConnectionMode.DISABLED){
					map.put(facing, ConnectionType.PIPE);
				} else if(pipe.hasExternalConnection(facing) && pipe.getConnectionSetting(facing) != PipeConnectionMode.DISABLED){
					map.put(facing, ConnectionType.EXTERNAL);
				} else {
					map.put(facing, ConnectionType.NONE);
				}
			}
			return state.with(UP, map.get(EnumFacing.UP))
					.with(DOWN, map.get(EnumFacing.DOWN))
					.with(NORTH, map.get(EnumFacing.NORTH))
					.with(SOUTH, map.get(EnumFacing.SOUTH))
					.with(EAST, map.get(EnumFacing.EAST))
					.with(WEST, map.get(EnumFacing.WEST));
		}
		return state;
    }
	
	@Override
	public void onNeighborChange(IBlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor){
		super.onNeighborChange(state, world, pos, neighbor);
		TileEntity tile = world.getTileEntity(pos);		
		if(tile instanceof TileEntityPipeBase){
			((TileEntityPipeBase)tile).rebuildConnections = true;
		}
	}
	
	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if(worldIn.isRemote)return true;
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityPipeBase){
			TileEntityPipeBase pipe = (TileEntityPipeBase)tile;
			
			if(ToolUtil.isHoldingWrench(player, hand)){
				pipe.incrsConnectionMode(side);
				return true;
			}
			
			if(pipe.getNetwork() !=null){
				if(player.isSneaking()){
					ChatUtil.sendChat(player, "" + pipe.getNetwork().getSize());
				} else {
					ChatUtil.sendChat(player, "" + pipe.getConnectionSetting(side));
					pipe.rebuildConnections = true;
				}
				return true;
			} else {
				ChatUtil.sendChat(player, "Null network");
				return true;
			}
		}
		return false;
    }
	
}
