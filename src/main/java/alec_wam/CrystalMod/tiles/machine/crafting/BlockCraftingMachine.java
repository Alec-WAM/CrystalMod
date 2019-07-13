package alec_wam.CrystalMod.tiles.machine.crafting;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.crate.ContainerBlockVariant;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class BlockCraftingMachine extends ContainerBlockVariant<EnumCraftingMachine> {
	public static final BooleanProperty RUNNING = BooleanProperty.create("running");
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public BlockCraftingMachine(EnumCraftingMachine type, BlockVariantGroup<EnumCraftingMachine, BlockCraftingMachine> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(RUNNING, Boolean.valueOf(false)));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, RUNNING);
	}
	
	@Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction enumfacing = context.getPlacementHorizontalFacing().getOpposite();
		return this.getDefaultState().with(FACING, enumfacing);
	}
	
	@Override
	public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
    {
		TileEntity tile = world.getTileEntity(pos);		
		if(tile instanceof TileEntityMachine){
			TileEntityMachine machine = (TileEntityMachine)tile;
			return state.with(RUNNING, Boolean.valueOf(machine.isRunning()));
		}
		return state;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(player.isSneaking() && ToolUtil.isHoldingWrench(player, hand)){
    		return ToolUtil.breakBlockWithWrench(worldIn, pos, player, hand);
    	}
		if(tile instanceof TileEntityMachine){
			TileEntityMachine machine = (TileEntityMachine)tile;
			if(worldIn.isRemote)return true;
			if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer))
	        {
	            ServerPlayerEntity entityPlayerMP = (ServerPlayerEntity) player;
	            GuiHandler.openCustomGui(GuiHandler.TILE_NORMAL, entityPlayerMP, machine, buf -> buf.writeBlockPos(pos));
	        }
			return true;
		}
        return super.onBlockActivated(state, worldIn, pos, player, hand, ray);
    }
	
	@Override
	public int getLightValue(BlockState state) {
      return state.get(RUNNING) ? 14 : 0;
	}

}
