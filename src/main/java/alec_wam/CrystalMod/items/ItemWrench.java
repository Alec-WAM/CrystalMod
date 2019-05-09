package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ItemWrench extends Item {

	public ItemWrench(Properties properties) {
		super(properties);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, EntityPlayer player)
    {
		return true;
    }

	@Override  
	public EnumActionResult onItemUseFirst(ItemStack stack, ItemUseContext context) {
		World world = context.getWorld();
		final IBlockState blockState = world.getBlockState(context.getPos());
		if(!context.isPlacerSneaking()){
			IProperty<?> facingProp = null;
			for (IProperty<?> prop : blockState.getProperties())
	        {
	            if ((prop.getName().equals("facing") || prop.getName().equals("rotation")) && prop.getValueClass() == EnumFacing.class)
	            {
	            	facingProp = prop;
	            	break;
	            }
	            if ((prop.getName().equals("axis")) && prop.getValueClass() == EnumFacing.Axis.class)
	            {
	            	facingProp = prop;
	            	break;
	            }
	        }
			if(facingProp !=null){
				IBlockState newState = blockState.cycle(facingProp);
				if(blockState !=newState){
					world.setBlockState(context.getPos(), newState, 2);
					return EnumActionResult.SUCCESS;				
				}
			}
		} else {
			if(blockState.getBlock() == Blocks.SHULKER_BOX){
				
				boolean success = ToolUtil.breakBlockWithWrench(world, context.getPos(), context.getPlayer(), stack);
				return success ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
			}
		}
		return EnumActionResult.PASS;
	}

}
