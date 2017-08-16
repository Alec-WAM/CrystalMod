package alec_wam.CrystalMod.items.armor;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrystalArmor extends ItemCustomArmor implements ICustomModel {

	public static final String NBT_COLOR = "Color";
	public static final String[] colors = new String[]{"blue", "red", "green", "dark", "pure"};
	
	
	public ItemCrystalArmor(ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn, String name,	ItemStack repair) {
		super(materialIn, equipmentSlotIn, name, repair);
	}

	@Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
		int suffix = this.armorType == EntityEquipmentSlot.LEGS ? 2 : 1;
		String color = ItemNBTHelper.getString(stack, NBT_COLOR, "");
    	
        return "crystalmod:textures/model/armor/crystal/" + color + "_layer_" + suffix + ".png";
    }
	
	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
		return ItemUtil.stackMatchUseOre(par2ItemStack, repair) ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final Map<String, ModelResourceLocation> models = Maps.newHashMap();
		for(String color : colors){
			ModelResourceLocation loc = new ModelResourceLocation(getRegistryName(), "color="+color);
			models.put(color, loc);
			ModelBakery.registerItemVariants(this, loc);
		}
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	String color = ItemNBTHelper.getString(stack, NBT_COLOR, "");
            	return models.get(color);
            }
        });
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        String color = ItemNBTHelper.getString(stack, NBT_COLOR, "");
        return super.getUnlocalizedName(stack) + (color !="" ?"."+color:"");
    }
	
	@SideOnly(Side.CLIENT)
    @Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for(String color : colors){
			ItemStack stack = new ItemStack(itemIn);
        	ItemNBTHelper.setString(stack, NBT_COLOR, color);
			subItems.add(stack);
		}
    }
}
