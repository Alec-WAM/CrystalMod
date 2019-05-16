package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.api.tile.IFusionPedestal;
import alec_wam.CrystalMod.api.tile.IPedestal;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.init.SoundEvents;
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
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class BlockPedestal extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {

	public static final DirectionProperty FACING = BlockDirectional.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final VoxelShape[] SHAPE_ARRAY = new VoxelShape[6];
	static{
		for(int i = 0; i < 6; i++){
			SHAPE_ARRAY[i] = buildShape(EnumFacing.byIndex(i));
		}
	}
	public BlockPedestal(Properties builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.UP).with(WATERLOGGED, Boolean.valueOf(false)));
	}
	
	public static VoxelShape buildShape(EnumFacing facing){
		AxisAlignedBB bbBottomLower = new AxisAlignedBB(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);
		AxisAlignedBB bbBottomUpper = new AxisAlignedBB(3.0D, 1.0D, 3.0D, 13.0D, 2.0D, 13.0D);
		AxisAlignedBB bbMiddle = new AxisAlignedBB(4.0D, 2.0D, 4.0D, 12.0D, 7.0D, 12.0D);
		AxisAlignedBB bbTopLower = new AxisAlignedBB(3.0D, 7.0D, 3.0D, 13.0D, 8.0D, 13.0D);
		AxisAlignedBB bbTopUpper = new AxisAlignedBB(1.0D, 8.0D, 1.0D, 15.0D, 10.0D, 15.0D);
		
		if(facing != EnumFacing.UP){
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
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return SHAPE_ARRAY[state.get(FACING).getIndex()];
	}
	
	@Override
	public RayTraceResult getRayTraceResult(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IPedestal){
			IPedestal pedestal = (IPedestal)tile;
			boolean hasItem = ItemStackTools.isValid(pedestal.getStack());
			if(hasItem){
				EnumFacing facing = state.get(FACING);
				AxisAlignedBB bb = new AxisAlignedBB(8.0D - 2, 8.0D - 2, 8.0D - 2, 8.0D + 2, 8.0D + 2, 8.0D + 2);
				AxisAlignedBB realBB = bb.offset(facing.getXOffset() * 5, facing.getYOffset() * 5, facing.getZOffset() * 5);
				VoxelShape itemShape = BlockUtil.makeVoxelShape(realBB);
				RayTraceResult res = itemShape.func_212433_a(start, end, pos);
				if (res != null) {
					res.hitInfo = "Item";
					return res;
				}
			}
		}
	    
		return original;
    }
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}	

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
    	return BlockRenderLayer.CUTOUT;
    }

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) 
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
					IItemHandler handler = ItemUtil.getExternalItemHandler(world, pos, EnumFacing.UP);
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
			RayTraceResult result = BlockUtil.rayTrace(world, player, RayTraceFluidMode.NEVER);
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
	
	@Override
	public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
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
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
		EnumFacing enumfacing = context.isPlacerSneaking() ? BlockUtil.getFacingFromContext(context, true) : context.getFace();
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
