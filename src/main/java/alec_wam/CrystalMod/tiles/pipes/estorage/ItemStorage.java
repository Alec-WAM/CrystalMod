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
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.network.CompressedDataInput;
import alec_wam.CrystalMod.network.CompressedDataOutput;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

public class ItemStorage {

	private List<ItemStackData> items = new ArrayList<ItemStackData>();
	private final EStorageNetwork network;
	
	public ItemStorage(EStorageNetwork network){
		this.network = network;
	}
	
	public int addItem(ItemStack stack, boolean sim){
		return addItem(stack, sim, true);
	}
	
	public int addItem(ItemStack stack, boolean sim, boolean sendUpdate) {
		if (stack == null)
			return 0;
		int inserted = 0;
		Iterator<List<NetworkedHDDInterface>> i1 = network.interfaces.values().iterator();
		while(i1.hasNext()){
			ItemStack insertCopy = stack.copy();
			final List<NetworkedHDDInterface> list = i1.next();
			Iterator<NetworkedHDDInterface> ii = list.iterator();
			//FIRST PASS
			while( ii.hasNext())
			{
				final NetworkedHDDInterface inter = ii.next();
				if (inter.getInterface() != null) {
					if(inter.getInterface().getNetworkInventory() !=null){
						int amt = inter.getInterface().getNetworkInventory().insertItem(network, insertCopy, true, sim, sendUpdate);
						insertCopy.stackSize-=amt;
						inserted+=amt;
						if(insertCopy.stackSize <= 0){
							return inserted;
						}
					}
				}
			}
			
			//SECOND PASS
			ii = list.iterator();
			while(ii.hasNext()){
				final NetworkedHDDInterface inter = ii.next();
				if (inter.getInterface() != null) {
					if(inter.getInterface().getNetworkInventory() !=null){
						int amt = inter.getInterface().getNetworkInventory().insertItem(network, insertCopy, false, sim, sendUpdate);
						insertCopy.stackSize-=amt;
						inserted+=amt;
						if(insertCopy.stackSize <= 0){
							return inserted;
						}
					}
				}
			}
		}

		if(network.masterNetwork !=null){
			return network.masterNetwork.getItemStorage().addItem(stack, sim, sendUpdate);
		}
		return inserted;
	}

	public ItemStack removeItems(ItemStack[] itemStacks){
		for (ItemStack itemStack : itemStacks) {
			ItemStackData data = getItemData(itemStack);
			if(data == null || data.stack == null || data.isCrafting)continue;
			int removedFake = removeItem(data, itemStack.stackSize, true);
			if (removedFake >= itemStack.stackSize) {
				ItemStack stack = data.stack.copy();
				stack.stackSize = itemStack.stackSize;
				removeItem(getItemData(itemStack), itemStack.stackSize, false);
				return stack;
			}
			return null;
		}
		return null;
	}
	
	public ItemStack removeItem(ItemStack stack, boolean sim){
		return this.removeItem(stack, sim, true);
	}
	
	public ItemStack removeItem(ItemStack stack, boolean sim, boolean sendUpdate){
		ItemStackData data = getItemData(stack);
		if(data == null || data.stack == null || data.isCrafting)return null;
		ItemStack ret = data.stack.copy();
		int removedFake = removeItem(data, stack.stackSize, sim, sendUpdate);
		
		ret.stackSize = removedFake;
		if(ret.stackSize <=0){
			return null;
		}
		return ret;
	}
	
	public int removeItem(ItemStackData data, int amount, boolean sim){
		return this.removeItem(data, amount, sim, true);
	}
	
	public int removeItem(ItemStackData data, int amount, boolean sim, boolean sendUpdate) {
		if (data == null || data.stack == null || network == null)
			return 0;
		Iterator<List<NetworkedHDDInterface>> i1 = network.interfaces.values().iterator();
		while(i1.hasNext()){
			Iterator<NetworkedHDDInterface> ii = i1.next().iterator();
			while( ii.hasNext())
			{
				final NetworkedHDDInterface inter = ii.next();
				if (network.sameDimAndPos(inter, data.interPos, data.interDim)) {
					if(inter.getInterface().getNetworkInventory() !=null){
						int extract = inter.getInterface().getNetworkInventory().extractItem(network, data.stack, amount, true, sendUpdate);
						if(extract >= 0){
							return sim ? extract : inter.getInterface().getNetworkInventory().extractItem(network, data.stack, amount, false, sendUpdate);
						}
					}
				}
			}
		}

		if(network.masterNetwork !=null){
			return network.masterNetwork.getItemStorage().removeItem(data, amount, sim, sendUpdate);
		}
		return 0;
	}

	public ItemStackData getItemData(ItemStack stack) {
		Iterator<ItemStackData> iData = items.iterator();
		while(iData.hasNext()) {
			ItemStackData data = iData.next();
			if (data.stack != null && ItemUtil.canCombine(stack, data.stack)) {
				return data;
			}
		}
		if(network.masterNetwork !=null){
			return network.masterNetwork.getItemStorage().getItemData(stack);
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
		if(network.masterNetwork !=null){
			return network.masterNetwork.getItemStorage().getOreItemData(stack);
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
	
	public void scanNetworkForItems(){
		items.clear();
		Iterator<List<NetworkedHDDInterface>> i1 = network.interfaces.values().iterator();
		while(i1.hasNext()){
			Iterator<NetworkedHDDInterface> ii = i1.next().iterator();
			//FIRST PASS
			while( ii.hasNext())
			{
				final NetworkedHDDInterface inter = ii.next();
				if(inter !=null && inter.getInterface() != null) {
					if(inter.getInterface().getNetworkInventory() !=null){
						Iterator<ItemStackData> data = inter.getInterface().getNetworkInventory().getItems(this).iterator();
						while(data.hasNext()){
							items.add(data.next());
						}
					}
				}
			}
		}
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
	
	public boolean addToList(ItemStackData itemData){
		List<ItemStackData> copy = Lists.newArrayList(items.iterator());
		Iterator<ItemStackData> ii = copy.iterator();
		while(ii.hasNext()){
			ItemStackData data = ii.next();
			if (data.sameIgnoreStack(itemData)) {
				data.stack = itemData.stack;
				if (data.stack == null) {
					items.remove(data);
				}
				return true;
			}
		}
		if (itemData != null && itemData.stack !=null) {
			if(getItemData(itemData.stack) == null){
				items.add(itemData);
				return true;
			}
		}
		return false;
	}
	
	public List<ItemStackData> clearListAtPos(BlockPos pos, int dim){
		List<ItemStackData> changed = Lists.newArrayList();
		List<ItemStackData> copy = Lists.newArrayList(items.iterator());
		Iterator<ItemStackData> ii = copy.iterator();
		while(ii.hasNext()){
			ItemStackData data = ii.next();
			if (data.interPos != null && data.interPos.equals(pos) && data.interDim == dim) {
				items.remove(data);
				changed.add(data);
			}
		}
		return changed;
	}
	
	public static class ItemStackData {
		public ItemStack stack;
		public int index;
		public BlockPos interPos;
		public int interDim;
		public boolean isCrafting;

		private String oreDictString;
		private String modIdString;
		
		public ItemStackData(){
			
		}
		
		public ItemStackData(ItemStack stack) {
			this.stack = stack;
			this.index = 0;
			this.interPos = BlockPos.ORIGIN;
			this.interDim = 0;
		}

		public ItemStackData(ItemStack stack, int index, BlockPos interPos, int dim) {
			this.stack = stack;
			this.index = index;
			this.interPos = interPos;
			this.interDim = dim;
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
			cdo.writeInt(index);
			cdo.writeInt(interPos.getX());
			cdo.writeInt(interPos.getY());
			cdo.writeInt(interPos.getZ());
			cdo.writeInt(interDim);
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
			newData.index = cdi.readInt();
			int x = cdi.readInt();
			int y = cdi.readInt();
			int z = cdi.readInt();
			newData.interPos = new BlockPos(x, y, z);
			newData.interDim = cdi.readInt();
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
		
		public boolean sameIgnoreStack(ItemStackData data){
			return (interPos == data.interPos) && (interDim == data.interDim) && (index == data.index);
		}
	}
	
}
