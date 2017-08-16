package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrystalBerry extends ItemFood implements ICustomModel {

	public ItemCrystalBerry() {
		this("crystalberry");
	}
	
	public ItemCrystalBerry(String name) {
		//Apple Food Value
		super(4, 0.3F, false);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabCrops);
		ModItems.registerItem(this, name);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(PlantType type : PlantType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMeta(), new ModelResourceLocation(getRegistryName(), type.getName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + PlantType.values()[i].getName();
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < PlantType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }

}
