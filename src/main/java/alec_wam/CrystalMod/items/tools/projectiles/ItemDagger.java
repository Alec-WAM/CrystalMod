package alec_wam.CrystalMod.items.tools.projectiles;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemDagger extends Item {

	public ItemDagger(){
		setMaxStackSize(16);
		setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "dagger");
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(!worldIn.isRemote) {
			EntityDagger projectile = new EntityDagger(worldIn, playerIn, 2.0f, 0.0f);
			worldIn.spawnEntity(projectile);
			
			if(!playerIn.capabilities.isCreativeMode){
				stack = ItemUtil.consumeItem(stack);
			}
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
	
}
