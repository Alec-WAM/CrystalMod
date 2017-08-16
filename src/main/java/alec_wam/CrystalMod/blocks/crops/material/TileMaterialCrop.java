package alec_wam.CrystalMod.blocks.crops.material;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.data.watchable.WatchableInteger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class TileMaterialCrop extends TileEntityMod implements IMessageHandler {

	private IMaterialCrop crop;
	//Amount of items at the end of growth
	private int cropYield;
	private WatchableInteger growthTime = new WatchableInteger();
	//Prevents Triple Combination
	private boolean isCombo;
		
	/**
	 * @return the crop
	 */
	public IMaterialCrop getCrop() {
		return crop;
	}

	/**
	 * @param crop the crop to set
	 */
	public void setCrop(IMaterialCrop crop) {
		this.crop = crop;
	}

	/**
	 * @return the cropYield
	 */
	public int getCropYield() {
		return cropYield;
	}

	/**
	 * @param time the time to set
	 */
	public void setGrowthTime(int time) {
		this.growthTime.setValue(time);
	}
	
	/**
	 * @return the growthTime
	 */
	public int getGrowthTime() {
		return growthTime.getValue();
	}

	public void setCombo(boolean combo) {
		isCombo = combo;
	}
	
	/**
	 * @return the isCombo
	 */
	public boolean isCombo() {
		return isCombo;
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		if(crop !=null){
			nbt.setString("CropID", crop.getUnlocalizedName());
		}
		nbt.setInteger("CropCount", cropYield);
		nbt.setInteger("GrowthTime", growthTime.getValue());
		nbt.setBoolean("IsCombo", isCombo);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		if(nbt.hasKey("CropID")){
			crop = CrystalModAPI.getCrop(nbt.getString("CropID"));
		}
		cropYield = nbt.getInteger("CropCount");
		growthTime.setValue(nbt.getInteger("GrowthTime"));
		isCombo = nbt.getBoolean("IsCrop");
	}
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			if(this.crop == null)return;
			if(this.cropYield == 0){
				this.calculateYield();
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
			}
			//Measures in Seconds
			if(getWorld().getTotalWorldTime() % 20 == 0){
				final int timeNeeded = crop.getGrowthTime(getWorld(), getPos());
				if(this.growthTime.getValue() < timeNeeded){
					this.growthTime.add(1);
				}
				if(this.growthTime.getValue() > timeNeeded){
					this.growthTime.setValue(timeNeeded);
				}			
			}
			if(this.growthTime.getValue() !=this.growthTime.getLastValue() && this.shouldDoWorkThisTick(4)){
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Growth", this.growthTime.getValue());
				this.growthTime.setLastValue(this.growthTime.getValue());
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Message", nbt), this);
			}
		}
	}
	
	public boolean isGrown(){
		if(crop == null)return false;
		int needed = crop.getGrowthTime(getWorld(), getPos());
		return (needed - growthTime.getValue()) <= 0;
	}
	
	public int getTimeRemaining(){
		if(crop == null)return -1;
		int needed = crop.getGrowthTime(getWorld(), getPos());
		return (needed - growthTime.getValue());
	}
	
	public void calculateYield(){
		if(crop == null){
			cropYield = -1;
			return;
		}
				
		int min = crop.getMinYield(getWorld(), getPos());
		int max = crop.getMaxYield(getWorld(), getPos());
		this.cropYield = MathHelper.getInt(getWorld().rand, min, max);
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageData.hasKey("Growth")){
			this.growthTime.setValue(messageData.getInteger("Growth"));
		}
	}

	public List<ItemStack> getDrops(boolean dropSeed, int fortune) {
		List<ItemStack> list = Lists.newArrayList();
		if(crop !=null){
			if(dropSeed)list.add(ItemMaterialSeed.getSeed(crop));
			if(isGrown()){
				for(int i = 0; i < 1 + fortune; i++){
					if(getWorld().rand.nextInt(crop.getExtraSeedDropChance(getWorld(), getPos())) <= 0){
						list.add(ItemMaterialSeed.getSeed(crop));
					}
				}
				list.addAll(crop.getDrops(getWorld(), getPos(), cropYield, fortune));
			}
		}
		return list;
	}
	
	@Override
	public boolean canRenderBreaking()
    {
		return true;
    }
	
}
