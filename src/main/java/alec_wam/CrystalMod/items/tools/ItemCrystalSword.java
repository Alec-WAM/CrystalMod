package alec_wam.CrystalMod.items.tools;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.Config;
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
import net.minecraft.item.ItemSword;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrystalSword extends ItemSword implements ICustomModel {

	public static final String[] colors = new String[]{"blue", "red", "green", "dark", "pure"};
	
	public final ToolMaterial toolMaterial;
	
	public ItemCrystalSword(ToolMaterial material) {
		super(material);
		toolMaterial = material;
		this.setCreativeTab(CrystalMod.tabTools);
		setNoRepair();
		ModItems.registerItem(this, "crystalsword");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final Map<String, ModelResourceLocation> models = Maps.newHashMap();
		for(String color : colors){
			ModelResourceLocation loc = new ModelResourceLocation("crystalmod:tool/sword", "color="+color);
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
    @Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for(String color : colors){
			ItemStack stack = new ItemStack(itemIn);
        	ItemNBTHelper.setString(stack, "Color", color);
			subItems.add(stack);
		}
    }
	
	@Override
	public int getMaxDamage(ItemStack stack)
    {
		String color = ItemNBTHelper.getString(stack, "Color", "");
		if(color.equals("pure")){
			return toolMaterial.getMaxUses() + Config.tool_pureDamageAddition;
		}
		return super.getMaxDamage(stack);
    }

}
