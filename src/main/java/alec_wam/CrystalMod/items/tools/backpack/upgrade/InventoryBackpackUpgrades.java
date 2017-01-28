package alec_wam.CrystalMod.items.tools.backpack.upgrade;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryBackpackUpgrades extends InventoryBackpack{

	public static final int UPGRADE_ITEM_SIZE = 4;
	//Upgrade Item slots
	public static final int SLOT_FILTER_HOPPER = 0;
	public static final int SLOT_FILTER_RESTOCKING = 1;
	public static final int SLOT_FILTER_VOID = 2;
	private ItemStack[] upgradeItems;
	
	public InventoryBackpackUpgrades(EntityPlayer player, ItemStack backpack, int size) {
		super(player, backpack, size, "Upgrades");
		upgradeItems = new ItemStack[UPGRADE_ITEM_SIZE];
		readFromNBT(ItemNBTHelper.getCompound(backpack));
	}

	public InventoryBackpackUpgrades(ItemStack backpack, int size) {
		super(backpack, size, "Upgrades");
		upgradeItems = new ItemStack[UPGRADE_ITEM_SIZE];
		readFromNBTNoPlayer(ItemNBTHelper.getCompound(backpack));
	}
	
	@Override
	public int getSizeInventory()
    {
        return size+(upgradeItems == null ? 0 : upgradeItems.length);
    }
	
	@Override
	public ItemStack getStackInSlot(int index){
		if(index >=0 && index < size){
			return slots[index];
		}
		int upgradeIndex = index-size;
		return getUpgradeStack(upgradeIndex);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index >=0 & index < size){
			slots[index] = stack;
			markDirty();
			return;
		} 
		
		int upgradeIndex = index-size;
		setUpgradeStack(upgradeIndex, stack);
	}
	
	public ItemStack getUpgradeStack(int index){
		if(index < 0 || index >= upgradeItems.length)return ItemStackTools.getEmptyStack();
		return upgradeItems[index];
	}
	
	public void setUpgradeStack(int index, ItemStack stack){
		if(index < 0 || index >= upgradeItems.length)return;
		upgradeItems[index] = stack;
		markDirty();
	}
	
	@Override
	public void clear(){
		super.clear();
		if(upgradeItems !=null)Arrays.fill(upgradeItems, ItemStackTools.getEmptyStack());
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
