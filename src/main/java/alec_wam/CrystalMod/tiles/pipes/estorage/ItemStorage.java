package alec_wam.CrystalMod.tiles.pipes.estorage;

import io.netty.handler.codec.EncoderException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.api.estorage.ICraftingTask;
import alec_wam.CrystalMod.api.estorage.IInsertListener;
import alec_wam.CrystalMod.api.estorage.INetworkContainer;
import alec_wam.CrystalMod.api.estorage.INetworkInventory;
import alec_wam.CrystalMod.api.estorage.INetworkInventory.ExtractFilter;
import alec_wam.CrystalMod.network.CompressedDataInput;
import alec_wam.CrystalMod.network.CompressedDataOutput;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.CraftingProcessBase;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.task.CraftingProcessExternal;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;

public class ItemStorage {

	private List<ItemStackData> items = new ArrayList<ItemStackData>();
	private List<NetworkedItemProvider> inventories = new ArrayList<NetworkedItemProvider>();
	private final EStorageNetwork network;
	
	public ItemStorage(EStorageNetwork network){
		this.network = network;
	}
	
	public synchronized void invalidate() {
		inventories.clear();

		Iterator<List<NetworkedItemProvider>> i1 = network.interfaces.values().iterator();
		while(i1.hasNext()){
			Iterator<NetworkedItemProvider> ii = i1.next().iterator();
			while( ii.hasNext())
			{
				final NetworkedItemProvider inter = ii.next();
				if(inter !=null && inter.getInterface() != null) {
					if(inter.getInterface().getNetworkInventory() !=null){
						inventories.add(inter);
					}
				}
			}
		}
		
        items.clear();
        
        Iterator<NetworkedItemProvider> ii = inventories.iterator();
        ItemStackList masterList = new ItemStackList();
        while(ii.hasNext()){
        	ItemStackList list = ii.next().getInterface().getNetworkInventory().getItems();
			if(list !=null){
				for(ItemStack stack : list.getStacks()){
					masterList.add(stack);
				}
			}
        }
        
        for(ItemStack stack : masterList.getStacks()){
        	items.add(new ItemStackData(stack));
        }

        for (INetworkContainer panel : network.watchers) {
			panel.sendItemsToAll(items);
		}
    }
	
	public ItemStack addItem(ItemStack stack, boolean sim) {
		return addItem(stack, stack.stackSize, sim);
	}
	
	public ItemStack addItem(ItemStack stack, int amount, boolean sim) {
		if (stack == null)
			return ItemHandlerHelper.copyStackWithSize(stack, amount);
		
		final int ogSize = amount;
		
		ItemStack remainder = ItemHandlerHelper.copyStackWithSize(stack, amount);
		Iterator<List<NetworkedItemProvider>> ii = network.interfaces.values().iterator();
		boolean breakLoop = false;
		master : while(ii.hasNext() && !breakLoop){
			Iterator<NetworkedItemProvider> i1 = ii.next().iterator();
			while(i1.hasNext() && !breakLoop){
				INetworkInventory inventory = i1.next().getInterface().getNetworkInventory();
				if(inventory !=null){
					remainder = inventory.insertItem(network, remainder, remainder.stackSize, sim);
					if(remainder == null || remainder.stackSize <= 0){
						breakLoop = true;
						break master;
					}
				}
			}
		}
		
		// If the stack size of the remainder is negative, it means of the original size abs(remainder.stackSize) items have been voided
        int insert;
        if(remainder == null){
        	insert = ogSize;
        }
        else if (remainder.stackSize < 0) {
        	insert = ogSize + remainder.stackSize;
            remainder = null;
        } else {
        	insert = ogSize - remainder.stackSize;
        }
        
        if (!sim && insert > 0) {
        	network.notifyInsert(ItemHandlerHelper.copyStackWithSize(stack, insert));
    		invalidate();
        	if(network.craftingController != null){
	            for (ICraftingTask task : network.craftingController.getCraftingTasks()) {
	                for (CraftingProcessBase process : task.getToProcess()) {
	                    if (process.onReceiveOutput(ItemHandlerHelper.copyStackWithSize(stack, insert))) {
	                    	return remainder;
	                    }
	                }
	            }
        	}
        }
		
		return remainder;
	}

	public static ExtractFilter NORMAL = new ExtractFilter(){

		@Override
		public boolean canExtract(ItemStack stack1, ItemStack stack2) {
			return ItemUtil.canCombine(stack1, stack2);
		}
		
	};
	
	public static ExtractFilter ORE = new ExtractFilter(){

		@Override
		public boolean canExtract(ItemStack stack1, ItemStack stack2) {
			return ItemUtil.stackMatchUseOre(stack1, stack2);
		}
		
	};
	
	public boolean removeCheck(ItemStack stack, int amt, ExtractFilter filter, boolean sim){
		ItemStack extract = removeItem(stack, amt, filter, sim);
		return extract !=null && extract.stackSize == amt;
	}
	
	public ItemStack removeItems(ItemStack[] itemStacks, ExtractFilter filter){
		for (ItemStack itemStack : itemStacks) {
			ItemStack removedFake = removeItem(itemStack, filter, true);
			if(removedFake == null)continue;
			return removeItem(itemStack, filter, false);
		}
		return null;
	}
	
	public ItemStack removeItem(ItemStack stack, boolean sim){
		return removeItem(stack, NORMAL, sim);
	}
	
	public ItemStack removeItem(ItemStack stack, ExtractFilter filter, boolean sim){
		return removeItem(stack, stack.stackSize, filter, sim);
	}
	
	public ItemStack removeItem(ItemStack stack, int amt, ExtractFilter filter, boolean sim){
		if(stack == null || stack.getItem() == null)return null;
		final int needed = amt;
		int received = 0;
		ItemStack ret = null;
		Iterator<List<NetworkedItemProvider>> ii = network.interfaces.values().iterator();
		boolean breakLoop = false;
		master : while(ii.hasNext() && !breakLoop){
			Iterator<NetworkedItemProvider> i1 = ii.next().iterator();
			while(i1.hasNext() && !breakLoop){
				NetworkedItemProvider inter = i1.next();
				ItemStack took = inter.getInterface().getNetworkInventory().extractItem(network, stack, amt, filter, sim);
				if(took !=null){
					if(ret == null){
						ret = took;
					} else {
						ret.stackSize+=took.stackSize;
					}
					received+=took.stackSize;
				}
				if (needed == received) {
					breakLoop = true;
	                break master;
	            }
			}
		}

        if(!sim && ret !=null){
        	invalidate();
			Iterator<IInsertListener> iter = network.listeners.iterator();
			while (iter.hasNext()) {
				iter.next().onItemExtracted(ret, ret.stackSize);
			}
		}
		return ret;
	}
	
	/*public int removeItem(ItemStackData data, int amount, boolean sim) {
		if (data == null || data.stack == null || network == null)
			return 0;
		Iterator<NetworkedItemProvider> i1 = inventories.iterator();
		while(i1.hasNext()){
			NetworkedItemProvider inter = i1.next();
			if (network.sameDimAndPos(inter, data.interPos, data.interDim)) {
				if(inter.getInterface().getNetworkInventory() !=null){
					int extract = inter.getInterface().getNetworkInventory().extractItem(network, data.stack, amount, true, true);
					if(extract >= 0){
						return sim ? extract : inter.getInterface().getNetworkInventory().extractItem(network, data.stack, amount, false, true);
					}
				}
			}
		}
		return 0;
	}*/
	
	public boolean hasItem(ItemStack stack){
		return hasItem(stack, false);
	}
	
	public boolean hasItem(ItemStack stack, boolean useOre){
		ItemStackData data = getItemData(stack);
		return data !=null && !data.isCrafting && data.getAmount() > 0;
	}

	public List<ItemStackData> getAllItemData(ItemStack stack) {
		List<ItemStackData> list = Lists.newArrayList();
		Iterator<ItemStackData> iData = items.iterator();
		while(iData.hasNext()) {
			ItemStackData data = iData.next();
			if (data.stack != null && ItemUtil.canCombine(stack, data.stack)) {
				list.add(data);
			}
		}
		return list;
	}
	
	public ItemStackData getItemData(ItemStack stack) {
		Iterator<ItemStackData> iData = items.iterator();
		while(iData.hasNext()) {
			ItemStackData data = iData.next();
			if (data.stack != null && ItemUtil.canCombine(stack, data.stack)) {
				return data;
			}
		}
		return null;
	}
	
	public ItemStackData getOreItemData(ItemStack stack) {
		Iterator<ItemStackData> iData = items.iterator();
		while(iData.hasNext()) {
			ItemStackData data = iData.next();
			if (data.stack != null && ItemUtil.stackMatchUseOre(stack, data.stack)) {
				return data;
			}
		}
		return null;
	}
	
	public List<ItemStackData> getAllOreItemData(ItemStack stack) {
		List<ItemStackData> list = Lists.newArrayList();
		Iterator<ItemStackData> iData = items.iterator();
		while(iData.hasNext()) {
			ItemStackData data = iData.next();
			if (data.stack != null && ItemUtil.stackMatchUseOre(stack, data.stack)) {
				list.add(data);
			}
		}
		return list;
	}
	
	public void setItemList(List<ItemStackData> newItems){
		if(newItems == null){
			items.clear();
			return;
		}
		items = newItems;
	}
	
	public List<ItemStackData> getItemList(){
		return items;
	}
	
	public static class ItemStackData {
		public ItemStack stack;
		public boolean isCrafting;

		private String oreDictString;
		private String modIdString;
		
		public ItemStackData(){
			
		}
		
		public ItemStackData(ItemStack stack) {
			this.stack = stack;
		}

		public void toBytes(CompressedDataOutput cdo) throws IOException {
			if (stack == null) {
				cdo.writeShort(-1);
			} else {
				cdo.writeShort(Item.getIdFromItem(stack.getItem()));
				cdo.writeVariable(stack.stackSize);
				cdo.writeShort(stack.getMetadata());
				NBTTagCompound nbttagcompound = null;

				if (stack.getItem().isDamageable()
						|| stack.getItem().getShareTag()) {
					nbttagcompound = stack.getTagCompound();
				}

				if (nbttagcompound == null) {
					cdo.writeByte(0);
				} else {
					cdo.writeByte(1);
					try {
						CompressedStreamTools.write(nbttagcompound, cdo);
					} catch (IOException ioexception) {
						throw new EncoderException(ioexception);
					}
				}
			}
			cdo.writeBoolean(isCrafting);
		}

		public static ItemStackData fromBytes(CompressedDataInput cdi)
				throws IOException {
			ItemStackData newData = new ItemStackData();
			ItemStack itemstack = null;
			int i = cdi.readShort();
			if (i >= 0) {
				int j = cdi.readVariable();
				int k = cdi.readShort();
				itemstack = new ItemStack(Item.getItemById(i), j, k);

				NBTTagCompound nbt = null;
				byte b0 = cdi.readByte();

				if (b0 == 0) {
					nbt = null;
				} else {
					try {
						nbt = CompressedStreamTools.read(cdi,
								new NBTSizeTracker(2097152L));
					} catch (Exception e) {
						System.out.println("Error Loading NBT to "
								+ itemstack.getDisplayName());
					}
				}
				itemstack.setTagCompound(nbt);
			}
			newData.stack = itemstack;
			newData.isCrafting = cdi.readBoolean();
			return newData;
		}

		public String getUnlocName() {
			return findUnlocName();
		}

		public String getLowercaseUnlocName(Locale locale) {
			return Lang.translateToLocal(getUnlocName()).toLowerCase(
					locale);
		}

		private String findUnlocName() {
			if (stack == null)
				return "null";
			String name = "";
			try {
				name = stack.getDisplayName();
				if (name == null || name.isEmpty()) {
					name = stack.getItem().getUnlocalizedName();
					if (name == null || name.isEmpty()) {
						name = stack.getItem().getClass().getName();
					}
				}
			} catch (Throwable ex) {
				name = "Exception: " + ex.getMessage();
			}
			return name;
		}

		public String getModId() {
			return findModId();
		}

		private String findModId() {
			if(!Strings.isNullOrEmpty(modIdString)){
				return modIdString;
			}
			if (stack == null || stack.getItem() == null)
				return "";
			ResourceLocation resourceInput = Item.REGISTRY.getNameForObject(stack.getItem());
			if (resourceInput != null
					&& resourceInput.getResourceDomain() != null) {
				return modIdString = resourceInput.getResourceDomain();
			} else {
				return "";
			}
		}
		
		public String getOreDic(){
			return findOreNames();
		}
		
		private String findOreNames(){
			if(!Strings.isNullOrEmpty(oreDictString)){
				return oreDictString;
			}
			if (stack == null)
				return "";
			StringBuilder oreDictStringBuilder = new StringBuilder();
			for (int oreId : OreDictionary.getOreIDs(stack)) {
				String oreName = OreDictionary.getOreName(oreId).toLowerCase(Locale.ENGLISH);
				oreDictStringBuilder.append(oreName).append(' ');
			}
			return oreDictString = oreDictStringBuilder.toString();
		}
		
		public int getAmount(){
			return stack == null ? 0 : stack.stackSize;
		}
	}

	public static ExtractFilter getExtractFilter(boolean oredict) {
		return oredict ? ORE : NORMAL;
	}
	
}
