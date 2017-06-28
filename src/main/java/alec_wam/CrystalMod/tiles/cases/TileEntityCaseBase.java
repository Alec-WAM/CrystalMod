package alec_wam.CrystalMod.tiles.cases;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityCaseBase extends TileEntityInventory implements IMessageHandler {

	public TileEntityCaseBase() {
		super("Case", 5);
	}

	public static String NBT_INVENTORY = "Inventory";
	public void writeToStack(ItemStack stack){
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
		NBTTagCompound invNBT = new NBTTagCompound();
		ItemStackHelper.saveAllItems(invNBT, inventory);
		nbt.setTag(NBT_INVENTORY, invNBT);
		stack.setTagCompound(nbt);
	}
	
	public void readFromStack(ItemStack stack){
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
		if(nbt.hasKey(NBT_INVENTORY)){
			NBTTagCompound invNBT = nbt.getCompoundTag(NBT_INVENTORY);
			ItemStackHelper.loadAllItems(invNBT, inventory);
		}
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack){
		if(ItemStackTools.isValid(stack)){
			if(stack.getItem() == Item.getItemFromBlock(ModBlocks.storageCase)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public abstract void onOpened();
	public abstract void onClosed();

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("Open")){
			onOpened();
		}
		if(messageId.equalsIgnoreCase("Close")){
			onClosed();
		}
	}
}
