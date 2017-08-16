package alec_wam.CrystalMod.api.estorage;

import alec_wam.CrystalMod.api.FluidStackList;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface INetworkInventory {

	public static enum EnumUpdateType {
		UPDATE_ALL, REMOVE;
	}
	
	public static abstract class ExtractFilter{
		public abstract boolean canExtract(ItemStack stack1, ItemStack stack2);
	}
	
	public static abstract class FluidExtractFilter{
		public abstract boolean canExtract(FluidStack stack1, FluidStack stack2);
	}
	
	public ItemStackList getItems();
	
	public ItemStack insertItem(EStorageNetwork network, ItemStack stack, int amount, boolean sim);
	
	public ItemStack extractItem(EStorageNetwork network, ItemStack stack, int amount, ExtractFilter filter, boolean sim);

	public FluidStackList getFluids();
	
	public int insertFluid(EStorageNetwork network, FluidStack stack, boolean matching, boolean sim);
	
	public FluidStack extractFluid(EStorageNetwork network, FluidStack stack, int amount, FluidExtractFilter filter, boolean sim);
	
}
