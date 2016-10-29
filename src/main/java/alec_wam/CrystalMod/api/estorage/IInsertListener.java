package alec_wam.CrystalMod.api.estorage;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IInsertListener {

	public void onItemInserted(ItemStack stack);
	
	public void onItemExtracted(ItemStack stack, int amount);
	
	public void onFluidInserted(FluidStack stack);
	
	public void onFluidExtracted(FluidStack stack, int amount);
	
}
