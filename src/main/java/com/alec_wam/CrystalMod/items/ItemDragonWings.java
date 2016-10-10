package com.alec_wam.CrystalMod.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.ICustomModel;
import com.alec_wam.CrystalMod.proxy.ClientProxy;

public class ItemDragonWings extends Item implements ICustomModel {

	public static final String UPGRADE_NBT = "CrystalMod.DragonWings";
	
	public ItemDragonWings(){
		super();
		setMaxStackSize(1);
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "dragonWings");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
		ClientProxy.CUSTOM_RENDERS.add(getRegistryName().getResourcePath());
	}
	
}
