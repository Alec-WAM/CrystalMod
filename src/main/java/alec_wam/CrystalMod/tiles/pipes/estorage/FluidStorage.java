package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.estorage.INetworkContainer;
import alec_wam.CrystalMod.api.estorage.INetworkInventory.FluidExtractFilter;
import alec_wam.CrystalMod.network.CompressedDataInput;
import alec_wam.CrystalMod.network.CompressedDataOutput;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.Lang;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class FluidStorage {
	
	private List<FluidStackData> fluids = new ArrayList<FluidStackData>();
	private List<NetworkedItemProvider> inventories = new ArrayList<NetworkedItemProvider>();
	private final EStorageNetwork network;
	
	public FluidStorage(EStorageNetwork network){
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
		
		fluids.clear();
        
        Iterator<NetworkedItemProvider> ii = inventories.iterator();
        FluidStackList masterList = new FluidStackList();
        while(ii.hasNext()){
        	FluidStackList list = ii.next().getInterface().getNetworkInventory().getFluids();
			if(list !=null){
				for(FluidStack stack : list.getStacks()){
					masterList.add(stack);
				}
			}
        }
        
        for(FluidStack stack : masterList.getStacks()){
        	fluids.add(new FluidStackData(stack));
        }

        for (INetworkContainer panel : network.watchers) {
			panel.sendFluidsToAll(fluids);
		}
    }
	
	public int addFluid(FluidStack stack, boolean sim) {
		if (stack == null)
			return 0;
		int inserted = 0;
		Iterator<NetworkedItemProvider> i1 = inventories.iterator();
		while(i1.hasNext()){
			FluidStack insertCopy = stack.copy();
			//FIRST PASS
			final NetworkedItemProvider inter = i1.next();
			if (insertCopy.amount > 0) {
				if(inter.getInterface().getNetworkInventory() !=null){
					int amt = inter.getInterface().getNetworkInventory().insertFluid(network, insertCopy, true, sim);
					insertCopy.amount-=amt;
					inserted+=amt;
					if(insertCopy.amount <= 0){
						return inserted;
					}
				}
			}
			if (insertCopy.amount > 0) {
				if(inter.getInterface().getNetworkInventory() !=null){
					int amt = inter.getInterface().getNetworkInventory().insertFluid(network, insertCopy, false, sim);
					insertCopy.amount-=amt;
					inserted+=amt;
					if(insertCopy.amount <= 0){
						return inserted;
					}
				}
			}
		}
		return inserted;
	}
	
	public static FluidExtractFilter NORMAL = new FluidExtractFilter(){

		@Override
		public boolean canExtract(FluidStack stack1, FluidStack stack2) {
			return FluidUtil.canCombine(stack1, stack2);
		}
		
	};
	
	public boolean removeCheck(FluidStack stack, int amt, FluidExtractFilter filter, boolean sim){
		FluidStack extract = removeFluid(stack, amt, filter, sim);
		return extract !=null && extract.amount == amt;
	}
	
	public FluidStack removeFluids(FluidStack[] fluidStacks, FluidExtractFilter filter){
		for (FluidStack fluidStack : fluidStacks) {
			FluidStack removed = removeFluid(fluidStack, filter, true);
			if(removed == null)continue;
			return removeFluid(fluidStack, filter, false);
		}
		return null;
	}
	
	public FluidStack removeFluid(FluidStack stack, boolean sim) {
		if(stack == null)return null;
		return removeFluid(stack, stack.amount, NORMAL, sim);
	}
	
	public FluidStack removeFluid(FluidStack stack, FluidExtractFilter filter, boolean sim) {
		if(stack == null)return null;
		return removeFluid(stack, stack.amount, filter, sim);
	}
	
	public FluidStack removeFluid(FluidStack stack, int amount, FluidExtractFilter filter, boolean sim) {
		/*if (data == null || data.stack == null || network == null)
			return 0;
		Iterator<NetworkedItemProvider> i1 = inventories.iterator();
		while(i1.hasNext()){
			final NetworkedItemProvider inter = i1.next();
			if (network.sameDimAndPos(inter, data.interPos, data.interDim)) {
				if(inter.getInterface().getNetworkInventory() !=null){
					int extract = inter.getInterface().getNetworkInventory().extractFluid(network, data.stack, amount, true, true);
					if(extract >= 0){
						return sim ? extract : inter.getInterface().getNetworkInventory().extractFluid(network, data.stack, amount, false, true);
					}
				}
			}
		}
		return 0;*/
		
		if(stack == null || stack.getFluid() == null)return null;
		final int needed = amount;
		int received = 0;
		FluidStack ret = null;
		Iterator<List<NetworkedItemProvider>> ii = network.interfaces.values().iterator();
		boolean breakLoop = false;
		master : while(ii.hasNext() && !breakLoop){
			Iterator<NetworkedItemProvider> i1 = ii.next().iterator();
			while(i1.hasNext() && !breakLoop){
				NetworkedItemProvider inter = i1.next();
				FluidStack took = inter.getInterface().getNetworkInventory().extractFluid(network, stack, amount, filter, sim);
				if(took !=null){
					if(ret == null){
						ret = took;
					} else {
						ret.amount+=took.amount;
					}
					received+=took.amount;
				}
				if (needed == received) {
					breakLoop = true;
	                break master;
	            }
			}
		}
		return ret;
	}
	
	public FluidStackData getFluidData(FluidStack stack) {
		Iterator<FluidStackData> iData = fluids.iterator();
		while(iData.hasNext()) {
			FluidStackData data = iData.next();
			if (data.stack != null && FluidUtil.canCombine(stack, data.stack)) {
				return data;
			}
		}
		return null;
	}
	
	public void setFluidList(List<FluidStackData> newFluids){
		if(newFluids == null){
			fluids.clear();
			return;
		}
		fluids = newFluids;
	}
	
	public List<FluidStackData> getFluidList(){
		return fluids;
	}
	
	public static class FluidStackData {
		public FluidStack stack;
		
		public FluidStackData(){
			
		}
		
		public FluidStackData(FluidStack stack) {
			this.stack = stack;
		}

		public void toBytes(CompressedDataOutput cdo) throws IOException {
			if (stack == null) {
				cdo.writeShort(-1);
			} else {
				cdo.writeShort(1);
				NBTTagCompound nbttagcompound = stack.writeToNBT(new NBTTagCompound());
				cdo.writeByte(1);
				try {
					CompressedStreamTools.write(nbttagcompound, cdo);
				} catch (IOException ioexception) {
					throw new EncoderException(ioexception);
				}
			}
		}

		public static FluidStackData fromBytes(CompressedDataInput cdi) throws IOException {
			FluidStackData newData = new FluidStackData();
			FluidStack fluidstack = null;
			int i = cdi.readShort();
			if (i >= 0) {
				

				NBTTagCompound nbt = null;
				byte b0 = cdi.readByte();

				if (b0 == 0) {
					nbt = null;
				} else {
					try {
						nbt = CompressedStreamTools.read(cdi,
								new NBTSizeTracker(2097152L));
					} catch (Exception e) {
						System.out.println("Error Loading Fluid NBT");
					}
				}
				
				if(nbt !=null)fluidstack = FluidStack.loadFluidStackFromNBT(nbt);
			}
			newData.stack = fluidstack;
			if(newData.stack == null)return null;
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
				name = stack.getUnlocalizedName();
				if (name == null || name.isEmpty()) {
					name = stack.getFluid().getUnlocalizedName(stack);
					if (name == null || name.isEmpty()) {
						name = stack.getFluid().getUnlocalizedName();
					}
				}
			} catch (Throwable ex) {
				name = "Exception: " + ex.getMessage();
			}
			return name;
		}
		
		public int getAmount(){
			return stack == null ? 0 : stack.amount;
		}
	}
	
}
