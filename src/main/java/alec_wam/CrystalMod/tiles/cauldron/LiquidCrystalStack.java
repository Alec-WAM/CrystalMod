package alec_wam.CrystalMod.tiles.cauldron;

import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.nbt.NBTTagCompound;

public class LiquidCrystalStack {

	private CrystalColors.Special color;
	public int shardCount;
	
	public LiquidCrystalStack(CrystalColors.Special color, int size){
		this.color = color;
		this.shardCount = size;
	}
	
	public CrystalColors.Special getColor(){
		return color;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setString("Color", color.name());
		nbtTagCompound.setInteger("Size", shardCount);
		return nbtTagCompound;
	}
	
	public static LiquidCrystalStack loadFromNBT(NBTTagCompound nbtTagCompound){
		if(nbtTagCompound == null || !nbtTagCompound.hasKey("Color"))return null;
		String nbtColor = nbtTagCompound.getString("Color");
		CrystalColors.Special color = null;
		for(CrystalColors.Special c : CrystalColors.Special.values()){
			if(c.name().equalsIgnoreCase(nbtColor)){
				color = c;
				break;
			}
		}
		if(color == null)return null;
		LiquidCrystalStack stack = new LiquidCrystalStack(color, nbtTagCompound.getInteger("Size"));
		return stack;
	}

	public LiquidCrystalStack copy() {
		return new LiquidCrystalStack(color, shardCount);
	}
	
}
