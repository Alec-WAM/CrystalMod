package alec_wam.CrystalMod.capability;

import java.awt.Color;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.guide.GuiGuideBase;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;

public class ExtendedPlayer {

	public final static String EXT_PROP_NAME = CrystalMod.MODID+"PlayerProperties";
	
	private boolean hasFlag;
	private int flagColor = Color.WHITE.getRGB();
	
	public float wingAnimTime;
	public float prevWingAnimTime;
	
	@SideOnly(Side.CLIENT)
	public GuiGuideBase lastOpenBook;
	
	private ExtendedPlayerInventory inventory = new ExtendedPlayerInventory();
	private ItemStack openBackpack;
	
	public ExtendedPlayer() {
	}
    
	public NBTTagCompound writeToNBT() {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setTag("Inventory", inventory.serializeNBT());
		
		if(ItemStackTools.isValid(openBackpack)){
			properties.setTag("OpenBackpack", openBackpack.serializeNBT());
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
}
