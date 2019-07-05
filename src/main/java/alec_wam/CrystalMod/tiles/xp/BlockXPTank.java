package alec_wam.CrystalMod.tiles.xp;

import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.compatibility.FluidConversion;
import alec_wam.CrystalMod.tiles.ContainerBlockCustom;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class BlockXPTank extends ContainerBlockCustom {
	//TODO Make BlockItem FluidHandler
	public static final BooleanProperty ENDER = BooleanProperty.create("ender");
	
	public BlockXPTank(Properties properties) {
		super(properties);
		this.setDefaultState(getDefaultState().with(ENDER, Boolean.FALSE));
	}

	private static final VoxelShape SHAPE_BOTTOM = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
	private static final VoxelShape SHAPE_TOP = Block.makeCuboidShape(1.0D, 14.0D, 1.0D, 15.0D, 15.0D, 15.0D);
	private static final VoxelShape SHAPE_BAR_NS = VoxelShapes.or(Block.makeCuboidShape(4.0D, 1.0D, 0.0D, 12.0D, 13.0D, 1.0D), Block.makeCuboidShape(4.0D, 1.0D, 15.0D, 12.0D, 13.0D, 16.0D));
	private static final VoxelShape SHAPE_BAR_EW = VoxelShapes.or(Block.makeCuboidShape(15.0D, 1.0D, 4.0D, 16.0D, 13.0D, 12.0D), Block.makeCuboidShape(0.0D, 1.0D, 4.0D, 1.0D, 13.0D, 12.0D));
	private static final VoxelShape SHAPE_BARS = VoxelShapes.or(SHAPE_BAR_NS, SHAPE_BAR_EW);
	private static final VoxelShape SHAPE_GLASS = Block.makeCuboidShape(1.0D, 1.0D, 1.0D, 15.0D, 13.0D, 15.0D);
	private static final VoxelShape SHAPE_TANK = VoxelShapes.or(VoxelShapes.or(SHAPE_BOTTOM, SHAPE_TOP), VoxelShapes.or(SHAPE_GLASS, SHAPE_BARS));
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE_TANK;
	}
	
	@Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
		//TODO Add Fluid Content Info
		/*if(type != EnumCrystalColorSpecialWithCreative.CREATIVE){
			int largeNumber = (Fluid.BUCKET_VOLUME*TileEntityTank.TIER_BUCKETS[type.ordinal()]);
			tooltip.add(new TranslationTextComponent("crystalmod.info.tank.storage", NumberFormat.getNumberInstance(Locale.US).format(largeNumber)));
		}*/
    }

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		ItemStack stack = context.getItem();
		if(ItemNBTHelper.getBoolean(stack, "IsEnder", false)){
			return this.getDefaultState().with(ENDER, true);
		}
		return this.getDefaultState();
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		//TODO Maybe add waterlogged
		builder.add(ENDER);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityXPTank();
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityXPTank) {
        	TileEntityXPTank tank = (TileEntityXPTank)tile;
        	if(tank.xpCon.getFluidAmount() > 0){
        		return 8;
        	}
        }

        return super.getLightValue(state, world, pos);
    }
	
	@Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityXPTank) {
        	TileEntityXPTank tank = (TileEntityXPTank) tile;
            return (tank.xpCon.getFluidAmount() * 15 / tank.xpCon.getCapacity());
        }

        return 0;
    }
	
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileEntityXPTank)) return false;
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			if(player.isSneaking() && ToolUtil.isHoldingWrench(player, hand)){
        		return ToolUtil.breakBlockWithWrench(world, pos, player, hand);
        	}            
			
			if(held.getItem() == Items.ENDER_EYE && !state.get(ENDER)){
				world.setBlockState(pos, state.with(ENDER, true), 3);
				if(!player.abilities.isCreativeMode){
					held.shrink(1);
				}
				world.playSound(null, pos, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.BLOCKS, 0.5F, 1.0F/*world.rand.nextFloat() * 0.1F + 0.9F*/);
				return true;
			}
			
			if(held.getItem() == Items.STICK){
				TileEntityXPTank tank = (TileEntityXPTank)tile;
				tank.xpCon.givePlayerXp(player, 100);
				return true;
			}
			
			if(held.getItem() == Items.APPLE){
				TileEntityXPTank tank = (TileEntityXPTank)tile;
				tank.xpCon.drainPlayerXpToReachContainerLevel(player, TileEntityXPTank.maxLevels);
				return true;
			}

			LazyOptional<IFluidHandlerItem> containerFluidHandler = FluidConversion.getHandlerFromItem(held);
        	if (containerFluidHandler !=null)
        	{
        		if(FluidConversion.interactWithFluidHandler(player, hand, world, pos, ray.getFace())){
        			return true;
        		}
        	}
		}
        return false;
    }

}
