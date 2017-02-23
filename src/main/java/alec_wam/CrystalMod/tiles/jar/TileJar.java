package alec_wam.CrystalMod.tiles.jar;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.tiles.TileEntityModStatic;
import alec_wam.CrystalMod.tiles.machine.INBTDrop;
import net.minecraft.init.PotionTypes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;

public class TileJar extends TileEntityModStatic implements INBTDrop {

	private PotionType potion = PotionTypes.EMPTY;
	private int potionCount;
	private EnumMap<EnumFacing, Boolean> labelMap = Maps.newEnumMap(EnumFacing.class);
	
	private boolean shulker;
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(potion != PotionTypes.EMPTY){
			nbt.setString("Potion", potion.getRegistryName().toString());
		}
		nbt.setInteger("Count", potionCount);
		nbt.setBoolean("IsShulker", isShulkerLamp());
		for(EnumFacing facing : EnumFacing.HORIZONTALS){
			nbt.setBoolean("Label."+facing.getName().toUpperCase(), labelMap.getOrDefault(facing, false));
		}
	}
	
	public void writeToStack(NBTTagCompound nbt){
		if(potion != PotionTypes.EMPTY){
			nbt.setString("Potion", potion.getRegistryName().toString());
		}
		if(potionCount > 0)nbt.setInteger("Count", potionCount);
		if(isShulkerLamp())nbt.setBoolean("IsShulker", isShulkerLamp());
		for(EnumFacing facing : EnumFacing.HORIZONTALS){
			if(labelMap.getOrDefault(facing, false))nbt.setBoolean("Label."+facing.getName().toUpperCase(), true);
		}
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		potion = PotionUtils.getPotionTypeFromNBT(nbt);
		potionCount = nbt.getInteger("Count");
		setShulkerLamp(nbt.getBoolean("IsShulker"));
		for(EnumFacing facing : EnumFacing.HORIZONTALS){
			if(nbt.hasKey("Label."+facing.getName().toUpperCase())){
				labelMap.put(facing, nbt.getBoolean("Label."+facing.getName().toUpperCase()));
			} else {
				labelMap.put(facing, false);
			}
		}
	}
	
	public void readFromStack(NBTTagCompound nbt){
		readCustomNBT(nbt);
	}
	
	public PotionType getPotion(){
		return potion;
	}
	
	public void setPotionType(PotionType type){
		this.potion = type;
	}
	
	public int getPotionCount(){
		return potionCount;
	}
	
	public void setPotionCount(int count){
		this.potionCount = count;
	}
	
	public boolean isShulkerLamp(){
		return shulker;
	}
	
	public void setShulkerLamp(boolean shulker){
		this.shulker = shulker;
	}
	
	public boolean hasLabel(EnumFacing facing){
		return labelMap.getOrDefault(facing, false);
	}
	
	public void setHasLabel(EnumFacing facing, boolean value){
		labelMap.put(facing, value);
	}

	public EnumMap<EnumFacing, Boolean> getLabelMap() {
		return labelMap;
	}
	
	@Override
	public boolean shouldRenderInPass(int pass)
    {
        return pass == 0 || pass == 1;
    }
}
