package alec_wam.CrystalMod.items.tools.bat;

import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import alec_wam.CrystalMod.api.tools.IBatUpgrade;
import alec_wam.CrystalMod.api.tools.UpgradeData;

public abstract class BatUpgrade implements IBatUpgrade {

	private ResourceLocation id;
	private int IPL;
	private int ML;
	private boolean halfValue;
	
	public BatUpgrade(ResourceLocation id, int IPL, int ML){
		this(id, IPL, ML, false);
	}
	
	public BatUpgrade(ResourceLocation id, int IPL, int ML, boolean half){
		this.id = id;
		this.IPL = IPL;
		this.ML = ML;
		this.halfValue = half;
	}

	@Override
	public ResourceLocation getID(){
		return id;
	}
	
	@Override
	public float getValue(UpgradeData data) {
		float value = (int)(data.getAmount() / getItemsPerLevel());
		/*if(halfValue && value > 0.0f){
			value*=0.5f;
		}*/
		return value;
	}

	@Override
	public int getItemsPerLevel() {
		return IPL;
	}

	@Override
	public int getMaxLevel() {
		return ML;
	}
	
	@Override
	public boolean blocksDamage(ItemStack stack, UpgradeData value){
		return false;
	}
	
	@Override
	public UpgradeData getCreativeListData(){
		return new UpgradeData(id, (getItemsPerLevel() * getMaxLevel()));
	}

	@Override
	public UpgradeData handleUpgrade(ItemStack bat, ItemStack[] ingred) {
		int amt = 0;
		for(ItemStack stack : ingred){
			int value = getUpgradeValue(stack);
			if(value > 0){
				amt+=value;
			}
		}
		if(amt > 0){
			return new UpgradeData(getID(), amt);
		}
		return null;
	}
	
	@Override
	public boolean canBeAdded(ItemStack bat, List<IBatUpgrade> upgrades, UpgradeData dataToBeAdded){
		return true;
	}
	
	@Override
	public void afterUpgradeAdded(ItemStack bat, ItemStack[] items,	UpgradeData data) {}
	
	public String getLevelInfo(UpgradeData data){
		float value = getValue(data);
		String val = ""+value;
		if(val.contains(".0")){
			val = val.substring(0, val.lastIndexOf("."));
		} else {
			val = ""+(value*2);
		}
		return val + " / " + getMaxLevel();
	}
	
	public String getBasicLevelInfo(int amount){
		return amount+" / "+ (getItemsPerLevel() * getMaxLevel()) + " ("+getItemsPerLevel()+")";
	}

}
