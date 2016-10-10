package com.alec_wam.CrystalMod.blocks;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystal extends EnumBlock<BlockCrystal.CrystalBlockType> {

	public static final PropertyEnum<CrystalBlockType> TYPE = PropertyEnum.<CrystalBlockType>create("type", CrystalBlockType.class);
	
	public BlockCrystal() {
		super(Material.ROCK, TYPE, CrystalBlockType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, CrystalBlockType.BLUE));
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(CrystalBlockType type : CrystalBlockType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
    
    public static enum CrystalBlockType implements IStringSerializable, IEnumMeta {
		BLUE("blue"),
		RED("red"),
		GREEN("green"),
		DARK("dark"),
		PURE("pure"),
		BLUE_CHISELED("blue_chiseled"),
		RED_CHISELED("red_chiseled"),
		GREEN_CHISELED("green_chiseled"),
		DARK_CHISELED("dark_chiseled"),
		PURE_CHISELED("pure_chiseled"),
		BLUE_BRICK("blue_brick"),
		RED_BRICK("red_brick"),
		GREEN_BRICK("green_brick"),
		DARK_BRICK("dark_brick"),
		PURE_BRICK("pure_brick");

		private final String unlocalizedName;
		public final int meta;

	    CrystalBlockType(String name) {
	      meta = ordinal();
	      unlocalizedName = name;
	    }

	    @Override
	    public String getName() {
	      return unlocalizedName;
	    }

	    @Override
	    public int getMeta() {
	      return meta;
	    }
    	
    }

}
