package alec_wam.CrystalMod.tiles.machine.crafting;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.crate.BlockContainerVariant;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockCraftingMachine extends BlockContainerVariant<EnumCraftingMachine> {
	//TODO Add Auto-Eject for output
	public static final BooleanProperty RUNNING = BooleanProperty.create("running");
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public BlockCraftingMachine(EnumCraftingMachine type, BlockVariantGroup<EnumCraftingMachine, BlockCraftingMachine> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.setDefaultState(getDefaultState().with(FACING, EnumFacing.NORTH).with(RUNNING, Boolean.valueOf(false)));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(FACING, RUNNING);
	}
	
	@Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
		EnumFacing enumfacing = context.getPlacementHorizontalFacing().getOpposite();
		return this.getDefaultState().with(FACING, enumfacing);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockReader world, BlockPos pos)
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
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(player.isSneaking() && ToolUtil.isHoldingWrench(player, hand)){
    		return ToolUtil.breakBlockWithWrench(worldIn, pos, player, hand);
    	}
		if(tile instanceof TileEntityMachine){
			TileEntityMachine machine = (TileEntityMachine)tile;
			if(worldIn.isRemote)return true;
			if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
	        {
	            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
	            NetworkHooks.openGui(entityPlayerMP, machine, buf -> buf.writeBlockPos(pos));
	        }
			return true;
		}
        return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
    }
	
	@Override
	public int getLightValue(IBlockState state) {
      return state.get(RUNNING) ? 14 : 0;
	}

}
