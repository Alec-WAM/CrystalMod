package com.alec_wam.CrystalMod.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICustomModel {

	@SideOnly(Side.CLIENT)
	public void initModel();
	
}
