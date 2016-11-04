package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.ItemUtil;

public class CraftingProcessExternal extends CraftingProcessBase {
    
	public static final String ID = "External";
	
    public CraftingProcessExternal(EStorageNetwork network) {
		super(network);
	}
    
    public CraftingProcessExternal(EStorageNetwork network, CraftingPattern pattern) {
		super(network, pattern);
	}

    @Override
    public boolean canStartProcessing(ItemStorage items, FluidStackList fluids) {
    	IItemHandler inventory = getPattern().getCrafter().getFacingInventory();
        if (inventory != null) {
            for (ItemStack stack : getToInsert()) {
                ItemStack actualStack = null;//items.get(stack, pattern.isOredict());

                ItemStackData found = items.getItemData(stack);
                if(found == null && pattern.isOredict()){
                	found = items.getOreItemData(stack);
                }
                
                if(found !=null && found.stack !=null){
                	actualStack = found.stack.copy();
                }
                
                boolean canInsert = ItemHandlerHelper.insertItem(inventory, ItemHandlerHelper.copyStackWithSize(actualStack, stack.stackSize), true) == null;
                if (actualStack == null || actualStack.stackSize == 0 || !items.removeCheck(actualStack, stack.stackSize, ItemStorage.getExtractFilter(pattern.isOredict()), true) || !canInsert) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
	@Override
	public void update(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids) {
		 IItemHandler inventory = getPattern().getCrafter().getFacingInventory();
		 for (ItemStack insertStack : getToInsert()) {
			 ItemStack tookStack = network.getItemStorage().removeItem(insertStack, ItemStorage.getExtractFilter(pattern.isOredict()), false);
			 if(tookStack !=null)ItemHandlerHelper.insertItem(inventory, tookStack, false);
		 }
	}
    
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString(NBT_TYPE, ID);
		return super.writeToNBT(tag);
    }
}
