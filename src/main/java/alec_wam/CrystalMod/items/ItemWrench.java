package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.IProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ItemWrench extends Item {

	public ItemWrench(Properties properties) {
		super(properties);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player)
    {
		return true;
    }

	@Override  
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		World world = context.getWorld();
		final BlockState blockState = world.getBlockState(context.getPos());
		if(!context.isPlacerSneaking()){
			IProperty<?> facingProp = null;
			for (IProperty<?> prop : blockState.getProperties())
	        {
	            if ((prop.getName().equals("facing") || prop.getName().equals("rotation")) && prop.getValueClass() == Direction.class)
	            {
	            	facingProp = prop;
	            	break;
	            }
	            if ((prop.getName().equals("axis")) && prop.getValueClass() == Direction.Axis.class)
	            {
	            	facingProp = prop;
	            	break;
	            }
	        }
			if(facingProp !=null){
				BlockState newState = blockState.cycle(facingProp);
				if(blockState !=newState){
					world.setBlockState(context.getPos(), newState, 2);
					return ActionResultType.SUCCESS;				
				}
			}
		} else {
			if(blockState.getBlock() == Blocks.SHULKER_BOX){
				
				boolean success = ToolUtil.breakBlockWithWrench(world, context.getPos(), context.getPlayer(), stack);
				return success ? ActionResultType.SUCCESS : ActionResultType.PASS;
			}
		}
		return ActionResultType.PASS;
	}

}
