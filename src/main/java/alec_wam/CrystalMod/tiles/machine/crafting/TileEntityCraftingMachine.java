package alec_wam.CrystalMod.tiles.machine.crafting;

import alec_wam.CrystalMod.tiles.machine.TileEntityMachineIO;
import alec_wam.CrystalMod.util.InventoryIOHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandler;

public abstract class TileEntityCraftingMachine extends TileEntityMachineIO {

	public TileEntityCraftingMachine(TileEntityType<?> tileEntityTypeIn, String name, int size) {
		super(tileEntityTypeIn, name, size);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn) {
		return index == 0 && isItemValidInput(itemStackIn);
	}
	
	@Override
	public boolean canExtract(int index, int amt) {
		return index == 1;
	}
	
	public abstract boolean isItemValidInput(ItemStack stack);
	
	//Similar to Pipes
	public int getItemIOCount(){
		return 4;
	}
	
	@Override
	public boolean pullItemsIn(IItemHandler handler, Direction from) {		
		for(int i = 0; i < handler.getSlots(); i++){
			ItemStack stack = handler.getStackInSlot(i);
			ItemStack thisStack = getStackInSlot(0);
			if(ItemStackTools.isEmpty(thisStack) || ItemUtil.canCombine(stack, thisStack)){
				if(isItemValidInput(stack)){
					int size = getItemIOCount();
					int space = ItemStackTools.isEmpty(thisStack) ? getInventoryStackLimit() : thisStack.getMaxStackSize() - ItemStackTools.getStackSize(thisStack);
					int removeAmt = Math.min(size, space);
					if(removeAmt > 0){
						ItemStack simStack = handler.extractItem(i, removeAmt, true);
						if(ItemStackTools.isValid(simStack)){
							ItemStack removedItem = handler.extractItem(i, removeAmt, false);
							if(ItemStackTools.isValid(thisStack)){
								ItemStackTools.incStackSize(thisStack, ItemStackTools.getStackSize(removedItem));
								setInventorySlotContents(0, thisStack);
							} else {
								setInventorySlotContents(0, removedItem);								
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public int[] getOutputSlots() {
		return new int[] {1};
	}
	
	@Override
	public boolean pushOutputItems(IItemHandler handler, Direction to){
		if(InventoryIOHelper.isFull(handler))return false;
		int[] outputSlots = getOutputSlots();
		for(int s = 0; s < outputSlots.length; s++){
			int index = outputSlots[s];
			ItemStack output = getStackInSlot(index);
			if(ItemStackTools.isValid(output)){
				final int startSize = Math.min(getItemIOCount(), ItemStackTools.getStackSize(output));
				ItemStack insertCopy = ItemUtil.copy(output, startSize);
				for(int i = 0; i < handler.getSlots(); i++){
					ItemStack simInsert = handler.insertItem(i, insertCopy, true);
					if(ItemStackTools.getStackSize(insertCopy) != ItemStackTools.getStackSize(simInsert)){
						insertCopy = handler.insertItem(i, insertCopy, false);
					}
				}
				int newSize = ItemStackTools.getStackSize(insertCopy);
				if(newSize != startSize){
					ItemStackTools.incStackSize(output, -(startSize - newSize));
					setInventorySlotContents(index, output);
					return true;
				}
			}
		}
		return false;
	}

}
