package alec_wam.CrystalMod.items.armor;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.tools.IWolfArmor;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.entities.accessories.WolfAccessories.WolfArmor;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWolfArmor extends Item implements IWolfArmor, ICustomModel {

	public ItemWolfArmor(){
		super();
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.COMBAT);
		ModItems.registerItem(this, "wolfarmor");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final Map<WolfArmor, ModelResourceLocation> models = Maps.newHashMap();
		for(WolfArmor armor : new WolfArmor[]{WolfArmor.LEATHER, WolfArmor.CHAIN, WolfArmor.IRON, WolfArmor.DIRON, WolfArmor.DIAMOND, WolfArmor.GOLD}){
			ModelResourceLocation loc = new ModelResourceLocation("crystalmod:wolfarmor", "armor="+armor.name().toLowerCase());
			models.put(armor, loc);
			ModelBakery.registerItemVariants(this, loc);
		}
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	return models.get(getWolfArmor(stack));
            }
        });
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + "."+getWolfArmor(stack).name().toLowerCase();
    }
	
	@Override
	public WolfArmor getWolfArmor(ItemStack stack) {
		if(ItemNBTHelper.verifyExistance(stack, "ArmorID")){
			return WolfArmor.getByName(ItemNBTHelper.getString(stack, "ArmorID", "none").toLowerCase());
		}
		return WolfArmor.NONE;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
		for(WolfArmor armor : new WolfArmor[]{WolfArmor.LEATHER, WolfArmor.CHAIN, WolfArmor.IRON, WolfArmor.DIRON, WolfArmor.DIAMOND, WolfArmor.GOLD}){
			ItemStack stack = new ItemStack(item);
			ItemNBTHelper.setString(stack, "ArmorID", armor.name().toLowerCase());
			list.add(stack);
		}
	}
	
}
