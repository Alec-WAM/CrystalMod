package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.api.estorage.INetworkInventory.ExtractFilter;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.StringUtils;

public class CraftingProcessNormal extends CraftingProcessBase {
	
	public static final String ID = "Normal";
	private static final String NBT_TO_INSERT = "ToInsert";
	
	private NonNullList<ItemStack> toInsert;
	
	public CraftingProcessNormal(EStorageNetwork network, CraftingPattern pattern, List<ItemStack> toInsert) {
		super(network, pattern);
		this.toInsert = NonNullList.withSize(9, ItemStackTools.getEmptyStack());
		for(int i = 0; i < toInsert.size(); i++){
			ItemStack stack = toInsert.get(i);
			this.toInsert.set(i, ItemStackTools.safeCopy(stack));
		}
	}
	
	public CraftingProcessNormal(EStorageNetwork network) {
		super(network);
	}
	
	@Override
	public List<ItemStack> getToInsert() {
		return toInsert;
	}
	
	public boolean canStartProcessing(ItemStorage items, FluidStackList fluids) {
		List<FluidStack> removed = Lists.newArrayList();
        for (ItemStack stack : getToInsert()) {
        	ExtractFilter filter = ItemStorage.getExtractFilter(pattern.isOredict());
            if (!items.removeCheck(stack, ItemStackTools.getStackSize(stack), filter, true)) {
                FluidStack fluidInItem = FluidUtil.getFluidTypeFromItem(stack);
                ItemStack container = FluidUtil.getEmptyContainer(stack);
                if (fluidInItem != null && !ItemStackTools.isNullStack(container) && items.hasItem(container)) {
                    FluidStack fluidData = fluids.get(fluidInItem);
                    if (fluidData != null && fluids.remove(fluidData, true) && items.removeCheck(container, 1, filter, true)) {
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
		boolean debugData = false;
		ItemStackList actualInputs = new ItemStackList();
		if(debugData) ModLogger.info("Update Process with "+getToInsert().size()+" items");
		for (ItemStack insertStack : getToInsert()) {
			if(ItemStackTools.isNullStack(insertStack))continue;
            FluidStack fluidInItem = FluidUtil.getFluidTypeFromItem(insertStack);
            if (fluidInItem != null) {
            	ItemStack empty = FluidUtil.getEmptyContainer(insertStack);
            	if(!ItemStackTools.isNullStack(empty))network.getItemStorage().removeItem(empty, ItemStorage.getExtractFilter(pattern.isOredict()), false);
                network.getFluidStorage().removeFluid(fluidInItem, false);
                actualInputs.add(insertStack.copy());
            } else {
            	ItemStack extract = network.getItemStorage().removeItem(insertStack, ItemStorage.getExtractFilter(pattern.isOredict()), false);
            	if(ItemStackTools.isValid(extract)){
            		actualInputs.add(extract);
            	} else {
            		toInsertItems.addAll(actualInputs.getStacks());
            		started = false;
            		ModLogger.info("Returned Process Missing "+insertStack + " / "+extract);
            		return;
            	}
            }
        }
		
		NonNullList<ItemStack> took = NonNullList.withSize(9, ItemStackTools.getEmptyStack());
        for (int i = 0; i < getToInsert().size(); i++) {
            ItemStack input = getToInsert().get(i);
            if (ItemStackTools.isValid(input)) {
                ItemStack actualInput = actualInputs.get(input, pattern.isOredict());
                ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, Math.max(1, ItemStackTools.getStackSize(input)));
                if(debugData) ModLogger.info("Took2 "+taken+" "+input+" "+actualInput);
                took.set(i, taken);
                if(ItemStackTools.isValid(taken))actualInputs.remove(taken, true);
            } else {
            	took.set(i, ItemStackTools.getEmptyStack());
            }
        }

        if(debugData) ModLogger.info("List: "+StringUtils.makeListReadable(took));
        
        if(debugData){
	        for(ItemStack t : took){
	        	ModLogger.info("Using: "+t);
	        }
        }
        
        for (ItemStack byproduct : (pattern.isOredict()? pattern.getByproducts(took) : pattern.getByproducts())) {
            if(!ItemStackTools.isNullStack(byproduct)){
            	toInsertItems.add(byproduct.copy());
            }
        }

        for (ItemStack output : (pattern.isOredict() ? pattern.getOutputs(took) : pattern.getOutputs())) {
        	if(debugData) ModLogger.info("Output: "+output);
            if(!ItemStackTools.isNullStack(output)){
            	toInsertItems.add(output.copy());
            }
        }
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString(NBT_TYPE, ID);
        super.writeToNBT(tag);
        NBTTagList toInsertList = new NBTTagList();
        for (ItemStack insert : toInsert) {
        	 toInsertList.appendTag(insert.serializeNBT());
        }
        tag.setTag(NBT_TO_INSERT, toInsertList);
        return tag;
    }
	
	 public boolean readFromNBT(NBTTagCompound tag) {
		 if(super.readFromNBT(tag)){
			 if (tag.hasKey(NBT_TO_INSERT)) {
				 NBTTagList toInsertList = tag.getTagList(NBT_TO_INSERT, Constants.NBT.TAG_COMPOUND);
				 toInsert = NonNullList.withSize(toInsertList.tagCount(), ItemStackTools.getEmptyStack());
				 for (int i = 0; i < toInsertList.tagCount(); ++i) {
					 ItemStack insertStack = ItemStackTools.loadFromNBT(toInsertList.getCompoundTagAt(i));
					 if (!ItemStackTools.isNullStack(insertStack)) {
						 toInsert.add(insertStack);
					 }
				 }
			 }
			 return true;
		 }
		 return false;
	 }

}
