package alec_wam.CrystalMod.api.pedistals;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IPedistal {

	public @Nullable ItemStack getStack();
	
	public void setStack(@Nullable ItemStack stack);

	public EnumFacing getRotation();
	
}
