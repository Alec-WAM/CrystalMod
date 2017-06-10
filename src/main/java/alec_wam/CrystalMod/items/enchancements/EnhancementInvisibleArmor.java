package alec_wam.CrystalMod.items.enchancements;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnhancementInvisibleArmor implements IEnhancement {

	@Override
	public ResourceLocation getID(){
		return CrystalMod.resourceL("invisiblearmor");
	}
	
	@Override
	public ItemStack getDisplayItem(){
		return new ItemStack(Blocks.GLASS_PANE);
	}
	
	@Override
	public boolean canApply(ItemStack stack, EntityPlayer player) {
		if(ItemStackTools.isValid(stack)){
    		if(stack.getItem() instanceof ItemArmor){
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
		return "CrystalMod.InvisArmor";
	}

	@Override
	public NonNullList<ItemStack> getRequiredItems() {
		NonNullList<ItemStack> list = NonNullList.create();
		list.add(new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata()));
		list.add(new ItemStack(Items.GOLDEN_CARROT));
		return list;
	}

	@Override
	public boolean removeItemsFromPlayer(EntityPlayer player) {
		boolean removedPure = false;
		boolean removedCarrot = false;
		search : for(int i = 0; i < player.inventory.getSizeInventory(); i++){
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(ItemStackTools.isValid(stack) && stack.getItem() == ModItems.crystals && stack.getMetadata() == CrystalType.PURE.getMetadata()){
				ItemStackTools.incStackSize(stack, -1);
				if(ItemStackTools.isEmpty(stack)){
					player.inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
				}
				removedPure = true;
			}
			if(ItemStackTools.isValid(stack) && stack.getItem() == Items.GOLDEN_CARROT){
				ItemStackTools.incStackSize(stack, -1);
				if(ItemStackTools.isEmpty(stack)){
					player.inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
				}
				removedCarrot = true;
			}
			
			if(removedPure && removedCarrot){
				break search;
			}
		}
		
		if(removedPure && !removedCarrot){
			player.inventory.addItemStackToInventory(new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata()));
		}
		
		if(!removedPure && removedCarrot){
			player.inventory.addItemStackToInventory(new ItemStack(Items.GOLDEN_CARROT));
		}
		
		return removedPure && removedCarrot;
	}

	@Override
	public boolean returnItemsToPlayer(EntityPlayer player) {
		final ItemStack pure = new ItemStack(ModItems.crystals, 1, CrystalType.PURE.getMetadata());
		if(!player.inventory.addItemStackToInventory(pure)){
			ItemUtil.spawnItemInWorldWithoutMotion(player.world, pure, new BlockPos(player));
		}
		final ItemStack carrot = new ItemStack(Items.GOLDEN_CARROT);
		if(!player.inventory.addItemStackToInventory(carrot)){
			ItemUtil.spawnItemInWorldWithoutMotion(player.world, carrot, new BlockPos(player));
		}
		return true;
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public void addToolTip(ItemStack stack, List<String> list) {
		list.add(TextFormatting.WHITE+""+TextFormatting.UNDERLINE+"Invisible");
	}
	


}
