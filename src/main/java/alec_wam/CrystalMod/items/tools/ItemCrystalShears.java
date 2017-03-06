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
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrystalShears extends ItemShears implements ICustomModel {

	public ItemCrystalShears()
    {
        this.setMaxStackSize(1);
        this.setCreativeTab(CrystalMod.tabTools);
        ModItems.registerItem(this, "crystalshears");
    }
	
	public String[] getColors(){
		return new String[]{"blue", "red", "green", "dark", "pure", "darkIron"};
	}
	
	public String getColor(ItemStack stack){
		return ItemNBTHelper.getString(stack, "Color", "");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final Map<String, ModelResourceLocation> models = Maps.newHashMap();
		for(String color : getColors()){
			ModelResourceLocation loc = new ModelResourceLocation("crystalmod:tool/shears", "color="+color);
			models.put(color, loc);
			ModelBakery.registerItemVariants(this, loc);
		}
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	String color = getColor(stack);
            	return models.get(color);
            }
        });
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        String color = getColor(stack);
        return super.getUnlocalizedName(stack) + (color !="" ?"."+color:"");
    }
	
	@SideOnly(Side.CLIENT)
    @Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for(String color : getColors()){
			ItemStack stack = new ItemStack(itemIn);
        	ItemNBTHelper.setString(stack, "Color", color);
			subItems.add(stack);
		}
    }
	
	@Override
	public void setDamage(ItemStack stack, int damage)
    {
		String color = getColor(stack);
		if(color.equalsIgnoreCase("pure")){
			return;
		}
		super.setDamage(stack, damage);
    }
	
	@Override
	public int getMaxDamage(ItemStack stack)
    {
		int normal = 238;
		String color = getColor(stack);
		if(color.equalsIgnoreCase("darkIron")){
			return normal+(normal/2);
		}
		return normal*2;
    }
	
}
