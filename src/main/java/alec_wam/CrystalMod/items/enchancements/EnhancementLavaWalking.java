package alec_wam.CrystalMod.items.enchancements;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.entities.accessories.horseshoes.ItemHorseShoe;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
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

public class EnhancementLavaWalking implements IEnhancement {

	@Override
	public ResourceLocation getID(){
		return CrystalMod.resourceL("lavawalking");
	}
	
	@Override
	public ItemStack getDisplayItem(){
		return new ItemStack(Items.LAVA_BUCKET);
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
		return "CrystalMod.LavaWalking";
	}

	@Override
	public NonNullList<ItemStack> getRequiredItems() {
		NonNullList<ItemStack> stacks = NonNullList.create();
		stacks.add(new ItemStack(ModBlocks.crysineMushroom, 3));
		stacks.add(new ItemStack(Items.LAVA_BUCKET));
		return stacks;
	}

	@Override
	public boolean removeItemsFromPlayer(EntityPlayer player) {
		int removedMushrooms = 0;
		boolean removedLava = false;
		search : for(int i = 0; i < player.inventory.getSizeInventory(); i++){
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(ItemStackTools.isValid(stack) && stack.getItem() == Item.getItemFromBlock(ModBlocks.crysineMushroom) && removedMushrooms < 3){
				int size = Math.min((3-removedMushrooms), ItemStackTools.getStackSize(stack));
				ItemStackTools.incStackSize(stack, -size);
				if(ItemStackTools.isEmpty(stack)){
					player.inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
				}
				removedMushrooms+=size;
			}
			if(ItemStackTools.isValid(stack) && stack.getItem() == Items.LAVA_BUCKET){
				ItemStackTools.incStackSize(stack, -1);
				if(ItemStackTools.isEmpty(stack)){
					player.inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
				}
				removedLava = true;
			}
			
			if(removedMushrooms == 3 && removedLava){
				break search;
			}
		}
		
		if(removedMushrooms > 0 && !removedLava){
			player.inventory.addItemStackToInventory(new ItemStack(ModBlocks.crysineMushroom, removedMushrooms));
		}
		
		if(removedMushrooms != 3 && removedLava){
			player.inventory.addItemStackToInventory(new ItemStack(Items.LAVA_BUCKET));
		}
		
		return removedMushrooms == 3 && removedLava;
	}

	@Override
	public boolean returnItemsToPlayer(EntityPlayer player) {
		final ItemStack returnStack = new ItemStack(ModBlocks.crysineMushroom, 3);
		if(!player.inventory.addItemStackToInventory(returnStack)){
			ItemUtil.spawnItemInWorldWithoutMotion(player.world, returnStack, new BlockPos(player));
		}
		final ItemStack returnStackLava = new ItemStack(Items.LAVA_BUCKET);
		if(!player.inventory.addItemStackToInventory(returnStackLava)){
			ItemUtil.spawnItemInWorldWithoutMotion(player.world, returnStackLava, new BlockPos(player));
		}
		return true;
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public void addToolTip(ItemStack stack, List<String> list) {
		list.add(TextFormatting.RED+""+TextFormatting.UNDERLINE+"Lava Walking");
	}
	


}
