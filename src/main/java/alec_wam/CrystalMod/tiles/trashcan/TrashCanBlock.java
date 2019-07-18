package alec_wam.CrystalMod.tiles.trashcan;

import alec_wam.CrystalMod.compatibility.FluidConversion;
import alec_wam.CrystalMod.tiles.ContainerBlockCustom;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class TrashCanBlock extends ContainerBlockCustom {
	protected static final VoxelShape SHAPE_BOTTOM = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 11.0D, 14.0D);
	protected static final VoxelShape SHAPE_LID = Block.makeCuboidShape(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D);
	protected static final VoxelShape SHAPE_HANDLE = Block.makeCuboidShape(6.0D, 15.0D, 6.0D, 10.0D, 16.0D, 10.0D);
	public static final VoxelShape SHAPE_ALL = VoxelShapes.or(SHAPE_BOTTOM, VoxelShapes.or(SHAPE_LID, SHAPE_HANDLE));
	
	public TrashCanBlock(Properties properties) {
		super(properties);
	}
	
	@Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE_ALL;
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileEntityTrashCan)) return false;
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			if(player.isSneaking() && ToolUtil.isHoldingWrench(player, hand)){
        		return ToolUtil.breakBlockWithWrench(world, pos, player, hand);
        	}            

			LazyOptional<IFluidHandlerItem> containerFluidHandler = FluidConversion.getHandlerFromItem(held);//FluidUtil.getFluidHandler(held);
        	if (containerFluidHandler !=null)
        	{
        		if(FluidConversion.interactWithFluidHandler(player, hand, world, pos, ray.getFace())){
        			return true;
        		}
        	}
		}
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityTrashCan();
	}

}
