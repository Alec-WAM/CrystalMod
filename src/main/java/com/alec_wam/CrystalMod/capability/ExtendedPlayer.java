package com.alec_wam.CrystalMod.capability;

import java.awt.Color;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.alec_wam.CrystalMod.CrystalMod;

public class ExtendedPlayer {

	public final static String EXT_PROP_NAME = CrystalMod.MODID+"PlayerProperties";
	
	private boolean hasFlag;
	private int flagColor = Color.WHITE.getRGB();
	
	public float wingAnimTime;
	public float prevWingAnimTime;
	
	
	public ExtendedPlayer() {
	}
    
	public NBTTagCompound writeToNBT() {
		NBTTagCompound properties = new NBTTagCompound();
		return properties;
	}

	public void readFromNBT(NBTTagCompound properties) {
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
}
