package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
import alec_wam.CrystalMod.util.ModLogger;

public class CraftingProcessNormal extends CraftingProcessBase {
	
	public static final String ID = "Normal";
	private static final String NBT_TO_INSERT = "ToInsert";
	
	private List<ItemStack> toInsert;
	
	public CraftingProcessNormal(EStorageNetwork network, CraftingPattern pattern, List<ItemStack> toInsert) {
		super(network, pattern);
		this.toInsert = new LinkedList<ItemStack>();
		for(ItemStack stack : toInsert){
			if(stack !=null){
				this.toInsert.add(stack.copy());
			}
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
            if (!items.removeCheck(stack, stack.stackSize, filter, true)) {
                FluidStack fluidInItem = FluidUtil.getFluidTypeFromItem(stack);
                ItemStack container = FluidUtil.getEmptyContainer(stack);
                if (fluidInItem != null && container !=null && items.hasItem(container)) {
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
		ItemStackList actualInputs = new ItemStackList();
		for (ItemStack insertStack : getToInsert()) {
            FluidStack fluidInItem = FluidUtil.getFluidTypeFromItem(insertStack);
            if (fluidInItem != null) {
            	ItemStack empty = FluidUtil.getEmptyContainer(insertStack);
            	if(empty !=null)network.getItemStorage().removeItem(empty, ItemStorage.getExtractFilter(pattern.isOredict()), false);
                network.getFluidStorage().removeFluid(fluidInItem, false);
                actualInputs.add(insertStack.copy());
            } else {
            	ItemStack extract = network.getItemStorage().removeItem(insertStack, ItemStorage.getExtractFilter(pattern.isOredict()), false);
            	if(extract !=null){
            		actualInputs.add(extract);
            	} else {
            		toInsertItems.addAll(actualInputs.getStacks());
            		started = false;
            		return;
            	}
            }
        }
		
		ItemStack[] took = new ItemStack[9];
        for (int i = 0; i < getToInsert().size(); i++) {
            ItemStack input = getToInsert().get(i);
            if (input != null) {
                ItemStack actualInput = actualInputs.get(input, pattern.isOredict());
                ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, input.stackSize);
                took[i] = taken;
                if(taken !=null)actualInputs.remove(taken, true);
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
				 toInsert = new ArrayList<ItemStack>(toInsertList.tagCount());
				 for (int i = 0; i < toInsertList.tagCount(); ++i) {
					 ItemStack insertStack = ItemStack.loadItemStackFromNBT(toInsertList.getCompoundTagAt(i));
					 if (insertStack != null) {
						 toInsert.add(insertStack);
					 }
				 }
			 }
			 return true;
		 }
		 return false;
	 }

}
