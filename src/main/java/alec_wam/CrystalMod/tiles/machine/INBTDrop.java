package alec_wam.CrystalMod.tiles.machine;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTDrop {
	
	public void writeToStack(NBTTagCompound nbt);
	
	public void readFromStack(NBTTagCompound nbt);
	
}
