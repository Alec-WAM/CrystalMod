package alec_wam.CrystalMod.items.tools;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemToolParts extends Item implements ICustomModel {

	public ItemToolParts(){
		super();
		this.setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "toolparts");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final ModelResourceLocation rod = new ModelResourceLocation(this.getRegistryName(), "rod");
		final ModelResourceLocation cover = new ModelResourceLocation(this.getRegistryName(), "cover");
		ModelBakery.registerItemVariants(this, rod, cover);
		final Map<String, ModelResourceLocation> axe = Maps.newHashMap();
		final Map<String, ModelResourceLocation> hoe = Maps.newHashMap();
		final Map<String, ModelResourceLocation> pick = Maps.newHashMap();
		final Map<String, ModelResourceLocation> shovel = Maps.newHashMap();
		final Map<String, ModelResourceLocation> sword = Maps.newHashMap();
        for(String color : new String[]{"blue", "red", "green", "dark", "pure"}){
        	ModelResourceLocation axeR = new ModelResourceLocation(this.getRegistryName(), "axe_head_"+color);
        	axe.put(color, axeR);
        	ModelResourceLocation hoeR = new ModelResourceLocation(this.getRegistryName(), "hoe_head_"+color);
        	hoe.put(color, hoeR);
        	ModelResourceLocation pickR = new ModelResourceLocation(this.getRegistryName(), "pick_head_"+color);
        	pick.put(color, pickR);
        	ModelResourceLocation shovelR = new ModelResourceLocation(this.getRegistryName(), "shovel_head_"+color);
        	shovel.put(color, shovelR);
        	ModelResourceLocation swordR = new ModelResourceLocation(this.getRegistryName(), "sword_head_"+color);
        	sword.put(color, swordR);
        	ModelBakery.registerItemVariants(this, axeR, hoeR, pickR, shovelR, swordR);
        }

        

        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	String type = ItemNBTHelper.getString(stack, "Type", "");
            	String color = ItemNBTHelper.getString(stack, "Color", "");
            	if(type.contains("axe")){
                	return axe.get(color);
                }
            	if(type.contains("hoe")){
                	return hoe.get(color);
                }
            	if(type.contains("pick")){
                	return pick.get(color);
                }
            	if(type.contains("shovel")){
                	return shovel.get(color);
                }
            	if(type.contains("sword")){
                	return sword.get(color);
                }
                if(type.contains("cover")){
                	return cover;
                }
                return rod;
            }
        });
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        String type = ItemNBTHelper.getString(stack, "Type", "");
        String color = ItemNBTHelper.getString(stack, "Color", "");
        return super.getUnlocalizedName(stack) + (type !="" ? "." + type+(color !="" ?"."+color:"") : "");
    }
	
	@SideOnly(Side.CLIENT)
    @Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for(PartType part : PartType.values()){
        	
        	if(part.colored){
        		for(String color : new String[]{"blue", "red", "green", "dark", "pure"}){
        			ItemStack stack = new ItemStack(itemIn);
                	ItemNBTHelper.setString(stack, "Type", part.getName()+(part.colored ? "_head" : ""));
        			ItemNBTHelper.setString(stack, "Color", color);
        			subItems.add(stack);
        		}
        	}else{
        		ItemStack stack = new ItemStack(itemIn);
            	ItemNBTHelper.setString(stack, "Type", part.getName()+(part.colored ? "_head" : ""));
        		subItems.add(stack);
        	}
        }
    }
	
	public static enum PartType implements IStringSerializable
    {
        ROD, COVER, AXE(true), HOE(true), PICK(true), SHOVEL(true), SWORD(true);

        public boolean colored;
        private PartType()
        {
        	colored = false;
        }
        
        private PartType(boolean color)
        {
        	colored = color;
        }

        @Override
		public String toString()
        {
            return name().toLowerCase();
        }

        @Override
		public String getName()
        {
            return name().toLowerCase();
        }
    }
}
