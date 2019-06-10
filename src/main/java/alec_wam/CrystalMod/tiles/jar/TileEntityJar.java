package alec_wam.CrystalMod.tiles.jar;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.blocks.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.INBTDrop;
import alec_wam.CrystalMod.tiles.TileEntityModStatic;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.Direction;

public class TileEntityJar extends TileEntityModStatic implements INBTDrop {
	//Empty Potion Type
	private Potion potion = Potions.field_185229_a;
	private int potionCount;
	private EnumMap<Direction, Boolean> labelMap = Maps.newEnumMap(Direction.class);
	
	private boolean shulker;
	
	public TileEntityJar() {
		this(WoodType.OAK);
	}
	
	public TileEntityJar(WoodType variant) {
		super(ModBlocks.jarGroup.getTileType(variant));
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		if(potion != Potions.field_185229_a){
			nbt.putString("Potion", potion.getRegistryName().toString());
		}
		nbt.putInt("Count", potionCount);
		nbt.putBoolean("IsShulker", isShulkerLamp());
		for(Direction facing : Direction.Plane.HORIZONTAL){
			nbt.putBoolean("Label."+facing.getName().toUpperCase(), labelMap.getOrDefault(facing, false));
		}
	}
	
	@Override
	public void writeToItemNBT(ItemStack stack){
		CompoundNBT nbt = new CompoundNBT();
		if(potion != Potions.field_185229_a){
			nbt.putString("Potion", potion.getRegistryName().toString());
		}
		if(potionCount > 0)nbt.putInt("Count", potionCount);
		if(isShulkerLamp())nbt.putBoolean("IsShulker", isShulkerLamp());
		for(Direction facing : Direction.Plane.HORIZONTAL){
			if(labelMap.getOrDefault(facing, false))nbt.putBoolean("Label."+facing.getName().toUpperCase(), true);
		}
		ItemNBTHelper.getCompound(stack).put("TILE_DATA", nbt);
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		potion = PotionUtils.getPotionTypeFromNBT(nbt);
		potionCount = nbt.getInt("Count");
		setShulkerLamp(nbt.getBoolean("IsShulker"));
		for(Direction facing : Direction.Plane.HORIZONTAL){
			if(nbt.contains("Label."+facing.getName().toUpperCase())){
				labelMap.put(facing, nbt.getBoolean("Label."+facing.getName().toUpperCase()));
			} else {
				labelMap.put(facing, false);
			}
		}
	}
	
	@Override
	public void readFromItemNBT(ItemStack stack){
		CompoundNBT nbt = ItemNBTHelper.getCompound(stack).getCompound("TILE_DATA");
		potion = PotionUtils.getPotionTypeFromNBT(nbt);
		potionCount = nbt.getInt("Count");
		setShulkerLamp(nbt.getBoolean("IsShulker"));
		for(Direction facing : Direction.Plane.HORIZONTAL){
			if(nbt.contains("Label."+facing.getName().toUpperCase())){
				labelMap.put(facing, nbt.getBoolean("Label."+facing.getName().toUpperCase()));
			} else {
				labelMap.put(facing, false);
			}
		}
		if(hasWorld() && isShulkerLamp()){
			//TODO Look into this
			//getWorld().checkLightFor(LightType.BLOCK, pos);
		}
	}
	
	public Potion getPotion(){
		return potion;
	}
	
	public void setPotionType(Potion type){
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
	
	public boolean hasLabel(Direction facing){
		return labelMap.getOrDefault(facing, false);
	}
	
	public void setHasLabel(Direction facing, boolean value){
		labelMap.put(facing, value);
	}

	public EnumMap<Direction, Boolean> getLabelMap() {
		return labelMap;
	}
}
