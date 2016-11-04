package alec_wam.CrystalMod.api.tools;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import com.google.common.base.Strings;

public class UpgradeData implements INBTSerializable<NBTTagCompound> {
	private ResourceLocation upgradeID;
	private int amount;
	
	public UpgradeData(NBTTagCompound nbt){
		deserializeNBT(nbt);
	}
	
	public UpgradeData(ResourceLocation id, int amount){
		this.upgradeID = id;
		this.amount = amount;
	}
	
	public ResourceLocation getUpgradeID(){
		return upgradeID;
	}
	
	public int getAmount(){
		return amount;
	}
	
	public void setAmount(int value){
		amount = value;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("ID", upgradeID.toString());
		nbt.setInteger("Amount", amount);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.upgradeID = new ResourceLocation(nbt.getString("ID"));
		this.amount = nbt.getInteger("Amount");
	}
	
	public boolean isValid(){
		return upgradeID !=null && !Strings.isNullOrEmpty(upgradeID.toString());
	}
}
