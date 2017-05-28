package alec_wam.CrystalMod.items.enchancements;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnhancementJumpBoost implements IEnhancement {

	@Override
	public ResourceLocation getID(){
		return CrystalMod.resourceL("jumpboost");
	}
	
	@Override
	public ItemStack getDisplayItem(){
		return new ItemStack(Items.RABBIT_FOOT);
	}
	
	@Override
	public boolean canApply(ItemStack stack) {
		if(ItemStackTools.isValid(stack)){
    		if(stack.getItem() instanceof ItemArmor){
    			return ((ItemArmor)stack.getItem()).armorType == EntityEquipmentSlot.LEGS;
    		}
		}
		return false;
	}

	@Override
	public boolean isApplied(ItemStack stack) {
		if(ItemStackTools.isValid(stack)){
    		if(stack.getItem() instanceof ItemArmor){
    			ItemArmor armor = (ItemArmor)stack.getItem();
    			if(armor.armorType == EntityEquipmentSlot.LEGS){
    				return ItemNBTHelper.verifyExistance(stack, getNBTID());
    			}
    		}
		}
		return false;
	}

	@Override
	public ItemStack apply(ItemStack stack) {
		ItemStack copy = stack.copy();
		ItemNBTHelper.setBoolean(copy, getNBTID(), true);
		return copy;
	}

	@Override
	public ItemStack remove(ItemStack stack) {
		ItemStack copy = stack.copy();
		ItemNBTHelper.getCompound(copy).removeTag(getNBTID());
		return copy;
	}

	@Override
	public String getNBTID() {
		return "CrystalMod.JumpBoost";
	}

	@Override
	public NonNullList<ItemStack> getRequiredItems() {
		NonNullList<ItemStack> list = NonNullList.create();
		list.add(new ItemStack(Items.RABBIT_FOOT));
		list.add(new ItemStack(Items.CARROT));
		return list;
	}

	@Override
	public boolean removeItemsFromPlayer(EntityPlayer player) {
		boolean removedFoot = false;
		boolean removedCarrot = false;
		search : for(int i = 0; i < player.inventory.getSizeInventory(); i++){
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(ItemStackTools.isValid(stack) && stack.getItem() == Items.RABBIT_FOOT){
				ItemStackTools.incStackSize(stack, -1);
				if(ItemStackTools.isEmpty(stack)){
					player.inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
				}
				removedFoot = true;
			}
			if(ItemStackTools.isValid(stack) && stack.getItem() == Items.CARROT){
				ItemStackTools.incStackSize(stack, -1);
				if(ItemStackTools.isEmpty(stack)){
					player.inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
				}
				removedCarrot = true;
			}
			
			if(removedFoot && removedCarrot){
				break search;
			}
		}
		
		if(removedFoot && !removedCarrot){
			player.inventory.addItemStackToInventory(new ItemStack(Items.RABBIT_FOOT));
		}
		
		if(!removedFoot && removedCarrot){
			player.inventory.addItemStackToInventory(new ItemStack(Items.CARROT));
		}
		
		return removedFoot && removedCarrot;
	}

	@Override
	public boolean returnItemsToPlayer(EntityPlayer player) {
		final ItemStack foot = new ItemStack(Items.RABBIT_FOOT);
		if(!player.inventory.addItemStackToInventory(foot)){
			ItemUtil.spawnItemInWorldWithoutMotion(player.world, foot, new BlockPos(player));
		}
		final ItemStack carrot = new ItemStack(Items.CARROT);
		if(!player.inventory.addItemStackToInventory(carrot)){
			ItemUtil.spawnItemInWorldWithoutMotion(player.world, carrot, new BlockPos(player));
		}
		return true;
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public void addToolTip(ItemStack stack, List<String> list) {
		list.add(TextFormatting.GREEN+""+TextFormatting.UNDERLINE+"Jump Boost");
	}
	


}
