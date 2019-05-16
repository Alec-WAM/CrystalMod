package alec_wam.CrystalMod.api.tile;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IPedestal {

	public @Nullable ItemStack getStack();
	
	public void setStack(@Nullable ItemStack stack);

	public EnumFacing getRotation();
	
}
