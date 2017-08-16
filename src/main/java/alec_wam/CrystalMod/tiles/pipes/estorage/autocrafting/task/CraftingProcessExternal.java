package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import java.util.Deque;

import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

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
                ItemStack actualStack = ItemStackTools.getEmptyStack();//items.get(stack, pattern.isOredict());

                ItemStackData found = items.getItemData(stack);
                if(found == null && pattern.isOredict()){
                	found = items.getOreItemData(stack);
                }
                
                if(found !=null && !ItemStackTools.isNullStack(found.stack)){
                	actualStack = found.stack.copy();
                }
                
                boolean canInsert = ItemStackTools.isNullStack(ItemHandlerHelper.insertItem(inventory, ItemHandlerHelper.copyStackWithSize(actualStack, ItemStackTools.getStackSize(stack)), true));
                if (!ItemStackTools.isValid(actualStack) || !items.removeCheck(actualStack, ItemStackTools.getStackSize(stack), ItemStorage.getExtractFilter(pattern.isOredict()), true) || !canInsert) {
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
			 if(!ItemStackTools.isNullStack(tookStack))ItemHandlerHelper.insertItem(inventory, tookStack, false);
		 }
	}
    
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString(NBT_TYPE, ID);
		return super.writeToNBT(tag);
    }
}
