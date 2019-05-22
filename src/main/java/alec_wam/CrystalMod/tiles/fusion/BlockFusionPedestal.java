package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.tiles.crate.EnumMiscUpgrades;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFusionPedestal extends BlockPedestal {
	
	public static final BooleanProperty AUTO = BooleanProperty.create("auto");
	public BlockFusionPedestal(Properties builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.UP).with(WATERLOGGED, Boolean.valueOf(false)).with(AUTO, Boolean.valueOf(false)));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(FACING, WATERLOGGED, AUTO);
	}	
	
	public static final String NBT_PEDESTAL_POS = "Pedestal";
	//TODO Render Linked pedestals when holding a linked wand
	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityFusionPedestal){
			TileEntityFusionPedestal pedestal = (TileEntityFusionPedestal)tile;
			ItemStack stack = player.getHeldItem(hand);
			if(stack.getItem() == ModItems.miscUpgrades.getItem(EnumMiscUpgrades.FUSION_AUTO)){
				world.setBlockState(pos, state.with(AUTO, true), 2);
				if(!player.abilities.isCreativeMode){
					stack.shrink(1);
					player.setHeldItem(hand, stack);
				}
				return true;
			}
			if(stack.getItem() == ModItems.fusionWand){
				if(player.isSneaking()){
					NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
					NBTTagCompound posTag = NBTUtil.writeBlockPos(pos);
					nbt.setTag(NBT_PEDESTAL_POS, posTag);
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
		return super.onBlockActivated(state, world, pos, player, hand, side, hitX, hitY, hitZ);
    }
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityFusionPedestal();
	}
	
	@Override
	public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
    {
        super.getDrops(state, drops, world, pos, fortune);
        if(state.get(AUTO)){
        	drops.add(new ItemStack(ModItems.miscUpgrades.getItem(EnumMiscUpgrades.FUSION_AUTO)));
        }
     }

}
