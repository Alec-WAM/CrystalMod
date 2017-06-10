package alec_wam.CrystalMod.items.enchancements;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.items.ItemDragonWings;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnhancementDragonWings implements IEnhancement {

	@Override
	public ResourceLocation getID(){
		return CrystalMod.resourceL("dragonwings");
	}
	
	@Override
	public ItemStack getDisplayItem(){
		return new ItemStack(ModItems.wings);
	}
	
	@Override
	public boolean canApply(ItemStack stack, EntityPlayer player) {
		if(ItemStackTools.isValid(stack)){
    		if(stack.getItem() instanceof ItemArmor){
    			ItemArmor armor = (ItemArmor)stack.getItem();
    			return armor.armorType == EntityEquipmentSlot.CHEST;
    		}
		}
		return false;
	}

	@Override
	public boolean isApplied(ItemStack stack) {
		if(ItemStackTools.isValid(stack)){
    		if(stack.getItem() instanceof ItemArmor){
    			ItemArmor armor = (ItemArmor)stack.getItem();
    			if(armor.armorType == EntityEquipmentSlot.CHEST){
    				return ItemNBTHelper.verifyExistance(stack, getNBTID());
    			}
    		}
		}
		return false;
	}

	@Override
	public ItemStack apply(ItemStack stack, EntityPlayer player) {
		ItemStack copy = stack.copy();
		ItemNBTHelper.setBoolean(copy, getNBTID(), true);
		return copy;
	}

	@Override
	public ItemStack remove(ItemStack stack, EntityPlayer player) {
		ItemStack copy = stack.copy();
		ItemNBTHelper.getCompound(copy).removeTag(getNBTID());
		return copy;
	}

	@Override
	public String getNBTID() {
		return ItemDragonWings.UPGRADE_NBT;
	}

	@Override
	public NonNullList<ItemStack> getRequiredItems() {
		return NonNullList.withSize(1, new ItemStack(ModItems.wings));
	}

	@Override
	public boolean removeItemsFromPlayer(EntityPlayer player) {
		int needed = 1;
		int remaining = needed;
		int removed = 0;
		search : for(int i = 0; i < player.inventory.getSizeInventory(); i++){
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(ItemStackTools.isValid(stack) && stack.getItem() == ModItems.wings){
				int remove = Math.min(remaining, ItemStackTools.getStackSize(stack));
				ItemStackTools.incStackSize(stack, -remove);
				if(ItemStackTools.isEmpty(stack)){
					player.inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
				}
				remaining-=remove;
				removed+=remove;
				if(remaining <=0){
					break search;
				}
			}
		}
		
		if(remaining > 0 && removed > 0){
			player.inventory.addItemStackToInventory(new ItemStack(ModItems.wings, removed));
		}
		
		return remaining == 0;
	}

	@Override
	public boolean returnItemsToPlayer(EntityPlayer player) {
		final ItemStack returnStack = new ItemStack(ModItems.wings);
		if(!player.inventory.addItemStackToInventory(returnStack)){
			ItemUtil.spawnItemInWorldWithoutMotion(player.world, returnStack, new BlockPos(player));
		}
		return true;
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public void addToolTip(ItemStack stack, List<String> list) {
		list.add(TextFormatting.DARK_PURPLE+""+TextFormatting.UNDERLINE+"Dragon Wings");
	}

}
