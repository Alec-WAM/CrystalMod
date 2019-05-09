package alec_wam.CrystalMod.tiles.jar;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.INBTDrop;
import alec_wam.CrystalMod.tiles.TileEntityModStatic;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumLightType;

public class TileEntityJar extends TileEntityModStatic implements INBTDrop {

	private PotionType potion = PotionTypes.EMPTY;
	private int potionCount;
	private EnumMap<EnumFacing, Boolean> labelMap = Maps.newEnumMap(EnumFacing.class);
	
	private boolean shulker;
	
	public TileEntityJar() {
		super(ModBlocks.TILE_JAR);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(potion != PotionTypes.EMPTY){
			nbt.setString("Potion", potion.getRegistryName().toString());
		}
		nbt.setInt("Count", potionCount);
		nbt.setBoolean("IsShulker", isShulkerLamp());
		for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL){
			nbt.setBoolean("Label."+facing.getName().toUpperCase(), labelMap.getOrDefault(facing, false));
		}
	}
	
	@Override
	public void writeToItemNBT(ItemStack stack){
		NBTTagCompound nbt = new NBTTagCompound();
		if(potion != PotionTypes.EMPTY){
			nbt.setString("Potion", potion.getRegistryName().toString());
		}
		if(potionCount > 0)nbt.setInt("Count", potionCount);
		if(isShulkerLamp())nbt.setBoolean("IsShulker", isShulkerLamp());
		for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL){
			if(labelMap.getOrDefault(facing, false))nbt.setBoolean("Label."+facing.getName().toUpperCase(), true);
		}
		ItemNBTHelper.getCompound(stack).setTag("TILE_DATA", nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		potion = PotionUtils.getPotionTypeFromNBT(nbt);
		potionCount = nbt.getInt("Count");
		setShulkerLamp(nbt.getBoolean("IsShulker"));
		for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL){
			if(nbt.hasKey("Label."+facing.getName().toUpperCase())){
				labelMap.put(facing, nbt.getBoolean("Label."+facing.getName().toUpperCase()));
			} else {
				labelMap.put(facing, false);
			}
		}
	}
	
	@Override
	public void readFromItemNBT(ItemStack stack){
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack).getCompound("TILE_DATA");
		potion = PotionUtils.getPotionTypeFromNBT(nbt);
		potionCount = nbt.getInt("Count");
		setShulkerLamp(nbt.getBoolean("IsShulker"));
		for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL){
			if(nbt.hasKey("Label."+facing.getName().toUpperCase())){
				labelMap.put(facing, nbt.getBoolean("Label."+facing.getName().toUpperCase()));
			} else {
				labelMap.put(facing, false);
			}
		}
		if(hasWorld() && isShulkerLamp()){
			getWorld().checkLightFor(EnumLightType.BLOCK, pos);
		}
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
		//System.out.println("Pass: "+pass);
        return pass == 0 || pass == 1;
    }
}
