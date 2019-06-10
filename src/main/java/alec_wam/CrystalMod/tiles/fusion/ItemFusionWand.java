package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemFusionWand extends Item {

	public ItemFusionWand(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack held = playerIn.getHeldItem(handIn);
		if(playerIn.isSneaking()){
			if(ItemNBTHelper.verifyExistance(held, BlockFusionPedestal.NBT_PEDESTAL_POS)){
				ItemNBTHelper.getCompound(held).remove(BlockFusionPedestal.NBT_PEDESTAL_POS);
				return new ActionResult<>(ActionResultType.SUCCESS, held); 
			}
		}
		return new ActionResult<>(ActionResultType.PASS, held);
	}
	
}
