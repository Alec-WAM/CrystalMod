package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;

public interface INetworkInventory {

	public List<ItemStackData> getItems(ItemStorage storage);
	
	public int insertItem(EStorageNetwork network, ItemStack stack, boolean matching, boolean sim, boolean sendUpdate);
	
	public int extractItem(EStorageNetwork network, ItemStack stack, int amount, boolean sim, boolean sendUpdate);
	
	public void updateItems(EStorageNetwork network,  int index);

	public List<FluidStackData> getFluids(FluidStorage storage);
	
	public int insertFluid(EStorageNetwork network, FluidStack stack, boolean matching, boolean sim, boolean sendUpdate);
	
	public int extractFluid(EStorageNetwork network, FluidStack stack, int amount, boolean sim, boolean sendUpdate);
	
	public void updateFluids(EStorageNetwork network, int index);
	
}
