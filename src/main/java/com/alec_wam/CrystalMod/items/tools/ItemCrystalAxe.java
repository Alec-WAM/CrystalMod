package com.alec_wam.CrystalMod.items.tools;

import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.ICustomModel;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.google.common.collect.Maps;

public class ItemCrystalAxe extends ItemAxe implements ICustomModel {

	public ItemCrystalAxe(ToolMaterial material) {
		super(material, 8.0F, -3.0F);
		this.setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "crystalaxe");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final Map<String, ModelResourceLocation> models = Maps.newHashMap();
		for(String color : new String[]{"blue", "red", "green", "dark", "pure"}){
			ModelResourceLocation loc = new ModelResourceLocation("crystalmod:tool/axe", "color="+color);
			models.put(color, loc);
			ModelBakery.registerItemVariants(this, loc);
		}
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	String color = ItemNBTHelper.getString(stack, "Color", "");
            	return models.get(color);
            }
        });
    }
	
	public String getUnlocalizedName(ItemStack stack)
    {
        String color = ItemNBTHelper.getString(stack, "Color", "");
        return super.getUnlocalizedName(stack) + (color !="" ?"."+color:"");
    }
	
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for(String color : new String[]{"blue", "red", "green", "dark", "pure"}){
			ItemStack stack = new ItemStack(itemIn);
        	ItemNBTHelper.setString(stack, "Color", color);
			subItems.add(stack);
		}
    }

}
