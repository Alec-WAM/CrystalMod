package alec_wam.CrystalMod.items.enchancements;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.entities.accessories.horseshoes.ItemHorseShoe;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnhancementWaterWalking implements IEnhancement {

	@Override
	public ResourceLocation getID(){
		return CrystalMod.resourceL("waterwalking");
	}
	
	@Override
	public ItemStack getDisplayItem(){
		return new ItemStack(Items.WATER_BUCKET);
	}
	
	@Override
	public boolean canApply(ItemStack stack, EntityPlayer player) {
		if(ItemStackTools.isValid(stack)){
    		if(stack.getItem() instanceof ItemArmor){
    			ItemArmor armor = (ItemArmor)stack.getItem();
    			return armor.armorType == EntityEquipmentSlot.FEET;
    		}
    		if(stack.getItem() instanceof ItemHorseShoe){
    			return true;
    		}
		}
		return false;
	}

	@Override
	public boolean isApplied(ItemStack stack) {
		if(ItemStackTools.isValid(stack)){
    		if(stack.getItem() instanceof ItemArmor){
    			ItemArmor armor = (ItemArmor)stack.getItem();
    			if(armor.armorType == EntityEquipmentSlot.FEET){
    				return ItemNBTHelper.verifyExistance(stack, getNBTID());
    			}
    		}
    		if(stack.getItem() instanceof ItemHorseShoe){
    			return ItemNBTHelper.verifyExistance(stack, getNBTID());
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
		return "CrystalMod.WaterWalking";
	}

	@Override
	public NonNullList<ItemStack> getRequiredItems() {
		return NonNullList.withSize(1, new ItemStack(Blocks.WATERLILY, 2));
	}

	@Override
	public boolean removeItemsFromPlayer(EntityPlayer player) {
		int needed = 2;
		int remaining = needed;
		int removed = 0;
		search : for(int i = 0; i < player.inventory.getSizeInventory(); i++){
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(ItemStackTools.isValid(stack) && stack.getItem() == Item.getItemFromBlock(Blocks.WATERLILY)){
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
			player.inventory.addItemStackToInventory(new ItemStack(Blocks.WATERLILY, removed));
		}
		
		return remaining == 0;
	}

	@Override
	public boolean returnItemsToPlayer(EntityPlayer player) {
		final ItemStack returnStack = new ItemStack(Blocks.WATERLILY, 2);
		if(!player.inventory.addItemStackToInventory(returnStack)){
			ItemUtil.spawnItemInWorldWithoutMotion(player.world, returnStack, new BlockPos(player));
		}
		return true;
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public void addToolTip(ItemStack stack, List<String> list) {
		list.add(TextFormatting.AQUA+""+TextFormatting.UNDERLINE+"Water Walking");
	}
	


}
