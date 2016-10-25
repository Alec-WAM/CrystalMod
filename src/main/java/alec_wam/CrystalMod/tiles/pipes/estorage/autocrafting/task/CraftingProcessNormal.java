package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import java.util.Deque;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.FluidStackList;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ItemStackList;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ModLogger;

public class CraftingProcessNormal extends CraftingProcessBase {
	
	public static final String ID = "Normal";
	
	public CraftingProcessNormal(EStorageNetwork network, CraftingPattern pattern) {
		super(network, pattern);
	}
	
	public CraftingProcessNormal(EStorageNetwork network) {
		super(network);
	}
	
	 public boolean canStartProcessing(ItemStorage items, FluidStackList fluids) {
		List<FluidStack> removed = Lists.newArrayList();
        for (ItemStack stack : getToInsert()) {
        	ItemStackData data = items.getItemData(stack);
            if(data == null && pattern.isOredict()){
            	data = items.getOreItemData(stack);
            }
            if (data == null || data.getAmount() <= 0 || items.removeItem(data, stack.stackSize, true) !=stack.stackSize) {
                FluidStack fluidInItem = FluidUtil.getFluidTypeFromItem(stack);
                ItemStack container = FluidUtil.getEmptyContainer(stack);
                if (fluidInItem != null && container !=null && items.hasItem(container)) {
                    FluidStack fluidData = fluids.get(fluidInItem);
                    if (fluidData != null && fluids.remove(fluidData, true) && items.removeItem(container, true, pattern.isOredict()) !=null) {
                    	removed.add(fluidData);
                        continue;
                    }
                }
                for(FluidStack remove : removed)fluids.add(remove);
                return false;
            }
        }
        for(FluidStack remove : removed)fluids.add(remove);
        return true;
    }

	@Override
	public void update(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids) {
		ItemStackList actualInputs = new ItemStackList();
		for (ItemStack insertStack : getToInsert()) {
            FluidStack fluidInItem = FluidUtil.getFluidTypeFromItem(insertStack);
            if (fluidInItem != null) {
            	ItemStack empty = FluidUtil.getEmptyContainer(insertStack);
            	if(empty !=null)network.getItemStorage().removeItem(empty, false, pattern.isOredict());
                network.getFluidStorage().removeFluid(fluidInItem, false);
                ModLogger.info("Normal Extract Fluid "+fluidInItem);
                actualInputs.add(insertStack.copy());
            } else {
            	ItemStack extract = network.getItemStorage().removeItem(insertStack, false, pattern.isOredict());
            	if(extract !=null){
            		actualInputs.add(extract);
            	}
            }
        }
		
		ItemStack[] took = new ItemStack[9];
        for (int i = 0; i < pattern.getInputs().size(); i++) {
            ItemStack input = pattern.getInputs().get(i);
            if (input != null) {
                ItemStack actualInput = actualInputs.get(input, pattern.isOredict());
                ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, input.stackSize);
                took[i] = taken;
                actualInputs.remove(taken, true);
            }
        }

        for (ItemStack byproduct : (pattern.isOredict()? pattern.getByproducts(took) : pattern.getByproducts())) {
            if(byproduct !=null){
            	toInsertItems.add(byproduct.copy());
            }
        }

        for (ItemStack output : (pattern.isOredict() ? pattern.getOutputs(took) : pattern.getOutputs())) {
            if(output !=null){
            	toInsertItems.add(output.copy());
            }
        }
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString(NBT_TYPE, ID);
        return super.writeToNBT(tag);
    }

}
