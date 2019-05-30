package alec_wam.CrystalMod.tiles.energy.engine;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.crate.BlockContainerVariant;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockEngine extends BlockContainerVariant<EnumEngineType> implements IBucketPickupHandler, ILiquidContainer {
	//TODO Add Comparator Handling
	public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	protected static final VoxelShape SHAPE_NS = Block.makeCuboidShape(16.0 * 0.06, 0.0, 16.0 * 0.03, 16.0 * 0.94, 16.0 * 0.75, 16.0 * 0.97);
	protected static final VoxelShape SHAPE_EW = Block.makeCuboidShape(16.0 * 0.03, 0.0, 16.0 * 0.06, 16.0 * 0.97, 16.0 * 0.75, 16.0 * 0.94);	
	   
	public BlockEngine(EnumEngineType type, BlockVariantGroup<EnumEngineType, BlockEngine> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, Boolean.valueOf(false)).with(ACTIVE, Boolean.valueOf(false)));
	}
	
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
    	if(stack.hasTag() && ItemNBTHelper.verifyExistance(stack, "EngineData")){
    		NBTTagCompound engineData = ItemNBTHelper.getCompound(stack).getCompound("EngineData");
    		int energy = engineData.getInt("Energy");
    		String energyString = NumberFormat.getNumberInstance(Locale.US).format(energy);
			tooltip.add(new TextComponentTranslation("crystalmod.info.energy", energyString));
    	}
    }
	
	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		EnumFacing facing = state.get(FACING);
		if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH){
			return SHAPE_NS;
		}
		return SHAPE_EW;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

    @Override
    public int getLightValue(IBlockState state) 
    {
    	return state.get(ACTIVE) ? 14 : 0;
    }
	
	@Override
    public boolean isBlockNormalCube(IBlockState state) {
		return false;
    }

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	/**
	 * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	 * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific face passed in.
	 *  
	 * @param facingState The state that is currently at the position offset of the provided face to the stateIn at
	 * currentPos
	 */
	@SuppressWarnings("deprecation")
	@Override
	public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(FACING, ACTIVE, WATERLOGGED);
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(player.isSneaking() && ToolUtil.isHoldingWrench(player, hand)){
    		return ToolUtil.breakBlockWithWrench(worldIn, pos, player, hand);
    	} 
		if(tile instanceof TileEntityEngineBase){
			TileEntityEngineBase engine = (TileEntityEngineBase)tile;
			if(worldIn.isRemote)return true;
			if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
	        {
	            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
	
	            NetworkHooks.openGui(entityPlayerMP, engine, buf -> buf.writeBlockPos(pos));
	        }
			return true;
		}
        return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
    }
	
	@Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
		EnumFacing enumfacing = context.getPlacementHorizontalFacing().getOpposite();

	    IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
		return this.getDefaultState().with(FACING, enumfacing).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
	}
	
	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
		if (state.get(WATERLOGGED)) {
			worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public IFluidState getFluidState(IBlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
		return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
		if (!state.get(WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
			if (!worldIn.isRemote()) {
				worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
				worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}	
}
