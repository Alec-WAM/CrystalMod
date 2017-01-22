package alec_wam.CrystalMod.items.tools.backpack.upgrade;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import scala.actors.threadpool.Arrays;

public class InventoryBackpackUpgrades extends InventoryBackpack{

	public static final int UPGRADE_ITEM_SIZE = 4;
	//Upgrade Item slots
	public static final int SLOT_FILTER_HOPPER = 0;
	public static final int SLOT_FILTER_RESTOCKING = 1;
	public static final int SLOT_FILTER_VOID = 2;
	private NonNullList<ItemStack> upgradeItems;
	
	public InventoryBackpackUpgrades(EntityPlayer player, ItemStack backpack, int size) {
		super(player, backpack, size, "Upgrades");
		upgradeItems = NonNullList.withSize(UPGRADE_ITEM_SIZE, ItemStackTools.getEmptyStack());
		readFromNBT(ItemNBTHelper.getCompound(backpack));
	}

	public InventoryBackpackUpgrades(ItemStack backpack, int size) {
		super(backpack, size, "Upgrades");
		upgradeItems = NonNullList.withSize(UPGRADE_ITEM_SIZE, ItemStackTools.getEmptyStack());
		readFromNBTNoPlayer(ItemNBTHelper.getCompound(backpack));
	}
	
	@Override
	public int getSizeInventory()
    {
        return size+(upgradeItems == null ? 0 : upgradeItems.size());
    }
	
	@Override
	public ItemStack getStackInSlot(int index){
		if(index >=0 && index < size){
			return super.getStackInSlot(index);
		}
		int upgradeIndex = index-size;
		return getUpgradeStack(upgradeIndex);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index >=0 & index < size){
			super.setInventorySlotContents(index, stack);
			return;
		} 
		
		int upgradeIndex = index-size;
		setUpgradeStack(upgradeIndex, stack);
	}
	
	public ItemStack getUpgradeStack(int index){
		if(index < 0 || index >= upgradeItems.size())return ItemStackTools.getEmptyStack();
		return upgradeItems.get(index);
	}
	
	public void setUpgradeStack(int index, ItemStack stack){
		if(index < 0 || index >= upgradeItems.size())return;
		upgradeItems.set(index, stack);
		markDirty();
	}
	
	@Override
	public void clear(){
		super.clear();
		if(upgradeItems !=null)upgradeItems.clear();
	}
	
	public boolean hasUpgrade(BackpackUpgrade upgrade){
		return getUpgradeIndex(upgrade) > -1;
	}
	
	public int getUpgradeIndex(BackpackUpgrade upgrade){
		for(int i = 0; i < size; i++){
			ItemStack stack = getStackInSlot(i);
			if(ItemStackTools.isValid(stack)){
				if(stack.getItem() instanceof ItemBackpackUpgrade){
					if(stack.getMetadata() == upgrade.ordinal()){
						return i;
					}
				}
			}
		}
		return -1;
	}
	
	public int getUpgradeCount(){
		int count = 0;
		for(int i = 0; i < getSizeInventory(); i++){
			ItemStack stack = getStackInSlot(i);
			if(ItemStackTools.isValid(stack)){
				if(stack.getItem() instanceof ItemBackpackUpgrade){
					count++;
				}
			}
		}
		return count;
	}
	
	public BackpackUpgrade[] getTabs(){
		List<BackpackUpgrade> tabs = Lists.newArrayList();
		for(int i = 0; i < getSizeInventory(); i++){
			ItemStack stack = getStackInSlot(i);
			if(ItemStackTools.isValid(stack)){
				if(stack.getItem() instanceof ItemBackpackUpgrade){
					BackpackUpgrade upgrade = BackpackUpgrade.byMetadata(stack.getMetadata());
					if(upgrade == BackpackUpgrade.POCKETS)continue;
					tabs.add(upgrade);
				}
			}
		}
		return tabs.toArray(new BackpackUpgrade[0]);
	}
    
}
