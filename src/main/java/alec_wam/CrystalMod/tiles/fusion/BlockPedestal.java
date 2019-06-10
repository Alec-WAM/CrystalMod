package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.api.tile.IFusionPedestal;
import alec_wam.CrystalMod.api.tile.IPedestal;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class BlockPedestal extends ContainerBlock implements IBucketPickupHandler, ILiquidContainer {

	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final VoxelShape[] SHAPE_ARRAY = new VoxelShape[6];
	static{
		for(int i = 0; i < 6; i++){
			SHAPE_ARRAY[i] = buildShape(Direction.byIndex(i));
		}
	}
	public BlockPedestal(Properties builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP).with(WATERLOGGED, Boolean.valueOf(false)));
	}
	
	public static VoxelShape buildShape(Direction facing){
		AxisAlignedBB bbBottomLower = new AxisAlignedBB(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);
		AxisAlignedBB bbBottomUpper = new AxisAlignedBB(3.0D, 1.0D, 3.0D, 13.0D, 2.0D, 13.0D);
		AxisAlignedBB bbMiddle = new AxisAlignedBB(4.0D, 2.0D, 4.0D, 12.0D, 7.0D, 12.0D);
		AxisAlignedBB bbTopLower = new AxisAlignedBB(3.0D, 7.0D, 3.0D, 13.0D, 8.0D, 13.0D);
		AxisAlignedBB bbTopUpper = new AxisAlignedBB(1.0D, 8.0D, 1.0D, 15.0D, 10.0D, 15.0D);
		
		if(facing != Direction.UP){
			bbBottomLower = BlockUtil.rotateBoundingBox(bbBottomLower, facing, 16.0F);
			bbBottomUpper = BlockUtil.rotateBoundingBox(bbBottomUpper, facing, 16.0F);
			bbMiddle = BlockUtil.rotateBoundingBox(bbMiddle, facing, 16.0F);
			bbTopLower = BlockUtil.rotateBoundingBox(bbTopLower, facing, 16.0F);
			bbTopUpper = BlockUtil.rotateBoundingBox(bbTopUpper, facing, 16.0F);
		}
		
		VoxelShape bottom = VoxelShapes.or(BlockUtil.makeVoxelShape(bbBottomLower), BlockUtil.makeVoxelShape(bbBottomUpper));
		VoxelShape middle = BlockUtil.makeVoxelShape(bbMiddle);
		VoxelShape middle_bottom = VoxelShapes.or(bottom, middle);
		VoxelShape top = VoxelShapes.or(BlockUtil.makeVoxelShape(bbTopLower), BlockUtil.makeVoxelShape(bbTopUpper));
		VoxelShape all = VoxelShapes.or(middle_bottom, top);	
		return all;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE_ARRAY[state.get(FACING).getIndex()];
	}
	
	@Override
	public RayTraceResult getRayTraceResult(BlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IPedestal){
			IPedestal pedestal = (IPedestal)tile;
			boolean hasItem = ItemStackTools.isValid(pedestal.getStack());
			if(hasItem){
				Direction facing = state.get(FACING);
				AxisAlignedBB bb = new AxisAlignedBB(8.0D - 2, 8.0D - 2, 8.0D - 2, 8.0D + 2, 8.0D + 2, 8.0D + 2);
				AxisAlignedBB realBB = bb.offset(facing.getXOffset() * 5, facing.getYOffset() * 5, facing.getZOffset() * 5);
				VoxelShape itemShape = BlockUtil.makeVoxelShape(realBB);
				RayTraceResult res = itemShape.rayTrace(start, end, pos);
				if (res != null) {
					res.hitInfo = "Item";
					return res;
				}
			}
		}
	    
		return original;
    }
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}	

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
    	return BlockRenderLayer.CUTOUT;
    }
	
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) 
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IPedestal){
			IPedestal pedestal = (IPedestal)tile;
			ItemStack pedestalStack = pedestal.getStack();
			boolean locked = false;
			if(tile instanceof IFusionPedestal){
				locked = ((IFusionPedestal)tile).isCrafting();
			}
			if(locked)return false;
			ItemStack heldItem = player.getHeldItem(hand);
			if(ItemStackTools.isValid(heldItem)){
				if(ItemStackTools.isEmpty(pedestalStack) || ItemStackTools.isValid(pedestalStack) && ItemUtil.canCombine(heldItem, pedestalStack)){
					if (world.isRemote) {
			            return true;
			        }
					
					final ItemStack original = heldItem;
					ItemStack insertStack = heldItem;
					if(ItemStackTools.isEmpty(pedestal.getStack())){
						insertStack = ItemUtil.copy(heldItem, 1);
					}
					IItemHandler handler = ItemUtil.getExternalItemHandler(world, pos, Direction.UP);
					if(handler == null)return false;
					int remove = -ItemStackTools.getStackSize(insertStack);
					ItemStack insert = ItemHandlerHelper.insertItem(handler, insertStack, false);
					remove+=ItemStackTools.getStackSize(insert);
					
					boolean changed = remove != 0;
					player.setHeldItem(hand, ItemStackTools.incStackSize(original, remove));
					if(changed){
						world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, 0.95f);
					}
					return changed;
				}
			} 
			RayTraceResult result = BlockUtil.rayTrace(world, player, RayTraceContext.FluidMode.NONE);
			if(ItemStackTools.isValid(pedestalStack) && result !=null){
				if(result.hitInfo !=null && result.hitInfo instanceof String && ((String)result.hitInfo).equals("Item")){
					if (world.isRemote) {
			            return true;
			        }
					
					//Are we looking at the item? If so then remove the item from the pedestal
					ItemStack drop = ItemStackTools.safeCopy(pedestalStack);
					pedestal.setStack(ItemStackTools.getEmptyStack());
					ItemHandlerHelper.giveItemToPlayer(player, drop, player.inventory.currentItem);
					return true;
				}
			}
			return false;
		}
        return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityPedestal();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() != this) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof IPedestal) {
				InventoryHelper.spawnItemStack(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), ((IPedestal)tileentity).getStack());
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		} 
	}
	
	@Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction enumfacing = context.isPlacerSneaking() ? BlockUtil.getFacingFromContext(context, true) : context.getFace();
		IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
		return this.getDefaultState().with(FACING, enumfacing).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
	}
	
	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
		if (state.get(WATERLOGGED)) {
			worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public IFluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
		return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
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
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}	
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		int powerInput = 0;
		
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile != null && tile instanceof IFusionPedestal){
			IFusionPedestal fusion = (IFusionPedestal)tile;
			if(fusion.isCrafting()){
				return 15;
			}
		}
		
		if (tile != null && tile instanceof IPedestal){
			return ItemStackTools.isValid(((IPedestal)tile).getStack()) ? 1 : 0;
		}
		
		return powerInput;
	}

}
