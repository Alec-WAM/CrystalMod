package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.tiles.crate.EnumMiscUpgrades;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFusionPedestal extends BlockPedestal {
	
	public static final BooleanProperty AUTO = BooleanProperty.create("auto");
	public BlockFusionPedestal(Properties builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP).with(WATERLOGGED, Boolean.valueOf(false)).with(AUTO, Boolean.valueOf(false)));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, AUTO);
	}	
	
	public static final String NBT_PEDESTAL_POS = "Pedestal";
	//TODO Render Linked pedestals when holding a linked wand
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityFusionPedestal){
			TileEntityFusionPedestal pedestal = (TileEntityFusionPedestal)tile;
			ItemStack stack = player.getHeldItem(hand);
			if(stack.getItem() == ModItems.miscUpgrades.getItem(EnumMiscUpgrades.FUSION_AUTO)){
				world.setBlockState(pos, state.with(AUTO, true), 2);
				if(!player.playerAbilities.isCreativeMode){
					stack.shrink(1);
					player.setHeldItem(hand, stack);
				}
				return true;
			}
			if(stack.getItem() == ModItems.fusionWand){
				if(player.isSneaking()){
					CompoundNBT nbt = ItemNBTHelper.getCompound(stack);
					CompoundNBT posTag = NBTUtil.writeBlockPos(pos);
					nbt.put(NBT_PEDESTAL_POS, posTag);
					stack.setTag(nbt);
					player.setHeldItem(hand, stack);
					return true;
				}
				//Allows placing of wand on pedestal if it is empty
				if(ItemStackTools.isValid(pedestal.getStack())){
					pedestal.startCrafting(player);
					return true;
				}
			}
		}		
		return super.onBlockActivated(state, world, pos, player, hand, ray);
    }
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityFusionPedestal();
	}
	//TODO Re-add
	/*@Override
	public void getDrops(BlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
    {
        super.getDrops(state, drops, world, pos, fortune);
        if(state.get(AUTO)){
        	drops.add(new ItemStack(ModItems.miscUpgrades.getItem(EnumMiscUpgrades.FUSION_AUTO)));
        }
     }*/

}
