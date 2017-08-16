package alec_wam.CrystalMod.tiles.cauldron;

import alec_wam.CrystalMod.tiles.cauldron.TileEntityCrystalCauldron.LiquidCrystalColor;
import net.minecraft.nbt.NBTTagCompound;

public class LiquidCrystalStack {

	private LiquidCrystalColor color;
	public int shardCount;
	
	public LiquidCrystalStack(LiquidCrystalColor color, int size){
		this.color = color;
		this.shardCount = size;
	}
	
	public LiquidCrystalColor getColor(){
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
		LiquidCrystalColor color = null;
		for(LiquidCrystalColor c : LiquidCrystalColor.values()){
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
