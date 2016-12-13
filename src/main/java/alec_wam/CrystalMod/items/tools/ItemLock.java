package alec_wam.CrystalMod.items.tools;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLock extends Item {

	public ItemLock(){
		super();
		this.setMaxStackSize(16);
		this.setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "lock");
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		tooltip.add(Lang.localize("tooltip.lock"));
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		EnumHand opp = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack heldOpp = playerIn.getHeldItem(opp);
		if(ItemStackTools.isValid(heldOpp)){
			if(heldOpp.getItem() instanceof ItemBackpackBase){
				IBackpack type = ((ItemBackpackBase)heldOpp.getItem()).getBackpack();
				if(type.canLock(heldOpp, playerIn)){
					BackpackUtil.setOwner(heldOpp, playerIn.getUniqueID());
					if(!playerIn.capabilities.isCreativeMode){
						playerIn.setHeldItem(hand, ItemUtil.consumeItem(itemStackIn));
					}
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
    }
	
}