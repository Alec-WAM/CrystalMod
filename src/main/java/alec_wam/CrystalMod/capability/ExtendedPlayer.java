package alec_wam.CrystalMod.capability;

import java.awt.Color;
import java.util.UUID;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.entities.disguise.DisguiseType;
import alec_wam.CrystalMod.items.guide.GuiGuideBase;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.data.watchable.WatchableInteger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExtendedPlayer {

	public final static String EXT_PROP_NAME = CrystalMod.MODID+"PlayerProperties";
	
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
	
	private DisguiseType lastDiguise;
	private DisguiseType currentDiguise = DisguiseType.NONE;
	private UUID lastPlayerDisguiseUUID;
	private UUID playerDisguiseUUID;
	
	private int enhancementXP;
	
	/**Explosives**/
	private int screenFlashTime;
	private int maxFlashTime;
	
	private WatchableInteger radiationTime = new WatchableInteger();
	
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

	public DisguiseType getLastDiguise() {
		return lastDiguise;
	}

	public void setLastDiguise(DisguiseType lastDiguise) {
		this.lastDiguise = lastDiguise;
	}

	public DisguiseType getCurrentDiguise() {
		return currentDiguise;
	}

	public void setCurrentDiguise(DisguiseType currentDiguise) {
		this.lastDiguise = this.currentDiguise;
		this.currentDiguise = currentDiguise;
	}

	public UUID getLastPlayerDisguiseUUID() {
		return lastPlayerDisguiseUUID;
	}
	
	public UUID getPlayerDisguiseUUID() {
		return playerDisguiseUUID;
	}
	
	public void setPlayerDisguiseUUID(UUID uuid) {
		lastPlayerDisguiseUUID = playerDisguiseUUID;
		playerDisguiseUUID = uuid;
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
}
