package alec_wam.CrystalMod.capability;

import java.awt.Color;
import java.util.UUID;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.guide.GuiGuideBase;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.data.watchable.WatchableInteger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExtendedPlayer {

	public final static String EXT_PROP_NAME = CrystalMod.MODID+"PlayerProperties";
	
	public boolean needsSync;
	
	/**Keeps track of jumps for Double Jump**/
	public boolean hasJumped;
	
	private boolean hasFlag;
	private int flagColor = Color.WHITE.getRGB();
	
	public float wingAnimTime;
	public float prevWingAnimTime;
	
	@SideOnly(Side.CLIENT)
	public GuiGuideBase lastOpenBook;
	
	private ExtendedPlayerInventory inventory = new ExtendedPlayerInventory();
	private @Nonnull ItemStack openBackpack = ItemStackTools.getEmptyStack();
	
	private UUID playerDisguiseUUID;
	private boolean isMini = false;
	
	private int enhancementXP;
	
	/**Explosives**/
	private int screenFlashTime;
	private int maxFlashTime;
	public boolean hasFailed;
	
	private WatchableInteger radiationTime = new WatchableInteger();
	public int redstoneCoreDelay;
	
	private WatchableInteger intellectTimer = new WatchableInteger();	
	
	
	public ExtendedPlayer() {
	}
    
	public NBTTagCompound writeToNBT() {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setTag("Inventory", inventory.serializeNBT());
		
		if(ItemStackTools.isValid(openBackpack)){
			properties.setTag("OpenBackpack", openBackpack.serializeNBT());
		}
		
		properties.setInteger("EnhancementXP", enhancementXP);		
		properties.setInteger("RadiationTime", radiationTime.getValue());
		properties.setInteger("IntellectTimer", intellectTimer.getValue());
		
		properties.setBoolean("Mini", isMini);
		if(playerDisguiseUUID !=null){
			properties.setTag("DisguiseUUID", NBTUtil.createUUIDTag(playerDisguiseUUID));
		}
		return properties;
	}

	public void readFromNBT(NBTTagCompound properties) {
		inventory.deserializeNBT(properties.getCompoundTag("Inventory"));
		if(properties.hasKey("OpenBackpack")){
			try{
				setOpenBackpack(ItemStackTools.loadFromNBT(properties.getCompoundTag("OpenBackpack")));
			}catch(Exception e){
				setOpenBackpack(ItemStackTools.getEmptyStack());
			}
		} else {
			setOpenBackpack(ItemStackTools.getEmptyStack());
		}
		enhancementXP = properties.getInteger("EnhancementXP");				
		radiationTime.setValue(properties.getInteger("RadiationTime"));	
		intellectTimer.setValue(properties.getInteger("IntellectTimer"));
		
		isMini = properties.getBoolean("Mini");
		if(properties.hasKey("DisguiseUUID")){
			playerDisguiseUUID = NBTUtil.getUUIDFromTag(properties.getCompoundTag("DisguiseUUID"));
		} else {
			playerDisguiseUUID = null;
		}
	}
	
	public NBTTagCompound buildSyncPacket(){
		NBTTagCompound properties = new NBTTagCompound();
		properties.setBoolean("Mini", isMini);
		if(playerDisguiseUUID !=null){
			properties.setTag("DisguiseUUID", NBTUtil.createUUIDTag(playerDisguiseUUID));
		}
		return properties;
	}
	
	public void unpackSyncPacket(NBTTagCompound properties){
		isMini = properties.getBoolean("Mini");
		if(properties.hasKey("DisguiseUUID")){
			playerDisguiseUUID = NBTUtil.getUUIDFromTag(properties.getCompoundTag("DisguiseUUID"));
		} else {
			playerDisguiseUUID = null;
		}
	}

	/**
	 * @return hasFlag
	 */
	public boolean hasFlag() {
		return hasFlag;
	}

	/**
	 * @param hasFlag the hasFlag to set
	 */
	public void setHasFlag(boolean hasFlag) {
		this.hasFlag = hasFlag;
	}

	/**
	 * @return the flagColor
	 */
	public int getFlagColor() {
		return flagColor;
	}

	/**
	 * @param color the flagColor to set
	 */
	public void setFlagColor(int color) {
		this.flagColor = color;
	}

	public void copyFrom(ExtendedPlayer oldPlayer) {
	}
	
	private EntityPlayer player;
	public ExtendedPlayer setPlayer(EntityPlayer player){
		this.player = player;
		return this;
	}

    public EntityPlayer getPlayer(){
    	return player;
    }

	public ExtendedPlayerInventory getInventory() {
		return inventory;
	}
	
	public ItemStack getOpenBackpack(){
		return openBackpack;
	}
	
	public void setOpenBackpack(ItemStack stack){
		openBackpack = stack;
	}
	
	public UUID getPlayerDisguiseUUID() {
		return playerDisguiseUUID;
	}
	
	public void setPlayerDisguiseUUID(UUID uuid) {
		playerDisguiseUUID = uuid;
	}
	
	public boolean isMini() {
		return isMini;
	}
	
	public void setMini(boolean mini) {
		isMini = mini;
	}

	public int getScreenFlashTime() {
		return screenFlashTime;
	}
	
	public int getMaxScreenFlashTime() {
		return maxFlashTime;
	}

	public void setScreenFlashTime(int screenFlashTime) {
		maxFlashTime = this.screenFlashTime = screenFlashTime;
	}
	
	public void subtractFlashTime(){
		this.screenFlashTime--;
	}

	public int getEnhancementXP() {
		return enhancementXP;
	}

	public void setEnhancementXP(int enhancementXP) {
		this.enhancementXP = enhancementXP;
	}
	
	//Radiation
	public int getRadiation() {
		return radiationTime.getValue();
	}
	
	public int getLastRadiation() {
		return radiationTime.getLastValue();
	}

	public void setRadiation(int value) {
		this.radiationTime.setValue(value);
	}
	
	public void setLastRadiation(int value) {
		this.radiationTime.setLastValue(value);
	}
	
	//Intellect
	public int getIntellectTime() {
		return intellectTimer.getValue();
	}
	
	public int getLastIntellectTime() {
		return intellectTimer.getLastValue();
	}

	public void setIntellectTime(int value) {
		this.intellectTimer.setValue(value);
	}
	
	public void setLastIntellectTime(int value) {
		this.intellectTimer.setLastValue(value);
	}
	
	
}
