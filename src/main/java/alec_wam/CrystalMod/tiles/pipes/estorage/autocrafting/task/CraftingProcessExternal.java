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
	public void update(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids) {
		 IItemHandler inventory = getPattern().getCrafter().getFacingInventory();
		 for (ItemStack insertStack : getToInsert()) {
			 ItemStackData data = network.getItemStorage().getItemData(insertStack);
			 if(data == null && pattern.isOredict()){
				 data = network.getItemStorage().getOreItemData(insertStack);
			 }
			 if(data !=null){
				 ItemStack tookStack = network.getItemStorage().removeItem(insertStack, false);
				 ItemHandlerHelper.insertItem(inventory, tookStack, false);
			 }
		 }
	}
    
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString(NBT_TYPE, ID);
		return super.writeToNBT(tag);
    }
}
