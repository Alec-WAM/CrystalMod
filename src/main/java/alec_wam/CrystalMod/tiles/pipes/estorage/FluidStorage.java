package alec_wam.CrystalMod.tiles.pipes.estorage;

import io.netty.handler.codec.EncoderException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import alec_wam.CrystalMod.network.CompressedDataInput;
import alec_wam.CrystalMod.network.CompressedDataOutput;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.Lang;

public class FluidStorage {
	
	private List<FluidStackData> fluids = new ArrayList<FluidStackData>();
	private final EStorageNetwork network;
	
	public FluidStorage(EStorageNetwork network){
		this.network = network;
	}
	
	public int addFluid(FluidStack stack, boolean sim){
		return this.addFluid(stack, sim, true);
	}
	
	public int addFluid(FluidStack stack, boolean sim, boolean sendUpdate) {
		if (stack == null)
			return 0;
		int inserted = 0;
		Iterator<List<NetworkedHDDInterface>> i1 = network.interfaces.values().iterator();
		while(i1.hasNext()){
			FluidStack insertCopy = stack.copy();
			final List<NetworkedHDDInterface> list = i1.next();
			Iterator<NetworkedHDDInterface> ii = list.iterator();
			//FIRST PASS
			while( ii.hasNext())
			{
				final NetworkedHDDInterface inter = ii.next();
				if (inter.getInterface() != null) {
					if(inter.getInterface().getNetworkInventory() !=null){
						int amt = inter.getInterface().getNetworkInventory().insertFluid(network, insertCopy, true, sim, sendUpdate);
						insertCopy.amount-=amt;
						inserted+=amt;
						if(insertCopy.amount <= 0){
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
						int amt = inter.getInterface().getNetworkInventory().insertFluid(network, insertCopy, false, sim, sendUpdate);
						insertCopy.amount-=amt;
						inserted+=amt;
						if(insertCopy.amount <= 0){
							return inserted;
						}
					}
				}
			}
		}

		if(network.masterNetwork !=null){
			return network.masterNetwork.getFluidStorage().addFluid(stack, sim, sendUpdate);
		}
		return inserted;
	}
	
	public FluidStack removeFluids(FluidStack[] fluidStacks){
		for (FluidStack fluidStack : fluidStacks) {
			FluidStackData data = getFluidData(fluidStack);
			if(data == null || data.stack == null)continue;
			int removedFake = removeFluid(data, fluidStack.amount, true);
			if (removedFake >= fluidStack.amount) {
				FluidStack stack = data.stack.copy();
				stack.amount = fluidStack.amount;
				removeFluid(getFluidData(fluidStack), fluidStack.amount, false);
				return stack;
			}
			return null;
		}
		return null;
	}
	
	public FluidStack removeFluid(FluidStack stack, boolean sim){
		return this.removeFluid(stack, sim, true);
	}
	
	public FluidStack removeFluid(FluidStack stack, boolean sim, boolean sendUpdate){
		FluidStackData data = getFluidData(stack);
		if(data == null || data.stack == null)return null;
		FluidStack ret = data.stack.copy();
		int removedFake = removeFluid(data, stack.amount, sim, sendUpdate);
		
		ret.amount = removedFake;
		if(ret.amount <=0){
			return null;
		}
		return ret;
	}
	
	public int removeFluid(FluidStackData data, int amount, boolean sim){
		return this.removeFluid(data, amount, sim, true);
	}
	
	public int removeFluid(FluidStackData data, int amount, boolean sim, boolean sendUpdate) {
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
						int extract = inter.getInterface().getNetworkInventory().extractFluid(network, data.stack, amount, true, sendUpdate);
						if(extract >= 0){
							return sim ? extract : inter.getInterface().getNetworkInventory().extractFluid(network, data.stack, amount, false, sendUpdate);
						}
					}
				}
			}
		}

		if(network.masterNetwork !=null){
			return network.masterNetwork.getFluidStorage().removeFluid(data, amount, sim, sendUpdate);
		}
		return 0;
	}
	
	public FluidStackData getFluidData(FluidStack stack) {
		Iterator<FluidStackData> iData = fluids.iterator();
		while(iData.hasNext()) {
			FluidStackData data = iData.next();
			if (data.stack != null && FluidUtil.canCombine(stack, data.stack)) {
				return data;
			}
		}
		if(network.masterNetwork !=null){
			return network.masterNetwork.getFluidStorage().getFluidData(stack);
		}
		return null;
	}
	
	public void scanNetworkForFluids(){
		fluids.clear();
		Iterator<List<NetworkedHDDInterface>> i1 = network.interfaces.values().iterator();
		while(i1.hasNext()){
			Iterator<NetworkedHDDInterface> ii = i1.next().iterator();
			//FIRST PASS
			while( ii.hasNext())
			{
				final NetworkedHDDInterface inter = ii.next();
				if(inter !=null && inter.getInterface() != null) {
					if(inter.getInterface().getNetworkInventory() !=null){
						Iterator<FluidStackData> data = inter.getInterface().getNetworkInventory().getFluids(this).iterator();
						while(data.hasNext()){
							fluids.add(data.next());
						}
					}
				}
			}
		}
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
	
	public boolean addToList(FluidStackData fluidData){
		List<FluidStackData> copy = Lists.newArrayList(fluids.iterator());
		Iterator<FluidStackData> ii = copy.iterator();
		while(ii.hasNext()){
			FluidStackData data = ii.next();
			if (data.sameIgnoreStack(fluidData)) {
				data.stack = fluidData.stack;
				if (data.stack == null) {
					fluids.remove(data);
				}
				return true;
			}
		}
		if (fluidData != null && fluidData.stack !=null) {
			if(getFluidData(fluidData.stack) == null){
				fluids.add(fluidData);
				return true;
			}
		}
		return false;
	}
	
	public List<FluidStackData> clearListAtPos(BlockPos pos, int dim){
		List<FluidStackData> changed = Lists.newArrayList();
		List<FluidStackData> copy = Lists.newArrayList(fluids.iterator());
		Iterator<FluidStackData> ii = copy.iterator();
		while(ii.hasNext()){
			FluidStackData data = ii.next();
			if (data.interPos != null && data.interPos.equals(pos) && data.interDim == dim) {
				fluids.remove(data);
				changed.add(data);
			}
		}
		return changed;
	}
	
	public static class FluidStackData {
		public FluidStack stack;
		public int index;
		public BlockPos interPos;
		public int interDim;
		
		public FluidStackData(){
			
		}
		
		public FluidStackData(FluidStack stack) {
			this.stack = stack;
			this.index = 0;
			this.interPos = BlockPos.ORIGIN;
			this.interDim = 0;
		}

		public FluidStackData(FluidStack stack, int index, BlockPos interPos, int dim) {
			this.stack = stack;
			this.index = index;
			this.interPos = interPos;
			this.interDim = dim;
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
			cdo.writeInt(index);
			cdo.writeInt(interPos.getX());
			cdo.writeInt(interPos.getY());
			cdo.writeInt(interPos.getZ());
			cdo.writeInt(interDim);
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
			newData.index = cdi.readInt();
			int x = cdi.readInt();
			int y = cdi.readInt();
			int z = cdi.readInt();
			newData.interPos = new BlockPos(x, y, z);
			newData.interDim = cdi.readInt();
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
		
		public boolean sameIgnoreStack(FluidStackData data){
			return (interPos == data.interPos) && (interDim == data.interDim) && (index == data.index);
		}
	}
	
}
