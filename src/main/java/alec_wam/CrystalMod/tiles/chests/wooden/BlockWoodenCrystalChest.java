package alec_wam.CrystalMod.tiles.chests.wooden;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.crate.BlockContainerVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockWoodenCrystalChest extends BlockContainerVariant<WoodenCrystalChestType> implements IBucketPickupHandler, ILiquidContainer {
	public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
	   
	public BlockWoodenCrystalChest(WoodenCrystalChestType type, BlockVariantGroup<WoodenCrystalChestType, BlockWoodenCrystalChest> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
	}
	
	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return SHAPE;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
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
		builder.add(FACING, WATERLOGGED);
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileEntityWoodenCrystalChest){
			TileEntityWoodenCrystalChest chest = (TileEntityWoodenCrystalChest)tile;
			if(worldIn.isRemote)return true;
			if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
	        {
	            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
	
	            NetworkHooks.openGui(entityPlayerMP, chest, buf -> buf.writeBlockPos(pos));
	        }
			return true;
		}
        return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
    }
	
	@Override
	public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileEntityWoodenCrystalChest) {
				InventoryHelper.dropInventoryItems(worldIn, pos, ((TileEntityWoodenCrystalChest)tileentity));
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		} 
	}
	
	@Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
		EnumFacing enumfacing = context.getPlacementHorizontalFacing();

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
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		int powerInput = 0;
		
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile != null && tile instanceof TileEntityWoodenCrystalChest){
			return Container.calcRedstoneFromInventory((TileEntityWoodenCrystalChest)tile);
		}
		
		return powerInput;
	}
}
