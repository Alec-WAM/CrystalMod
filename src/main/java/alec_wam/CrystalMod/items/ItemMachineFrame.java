package alec_wam.CrystalMod.items;

import java.util.List;
import java.util.Locale;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMachineFrame extends Item implements ICustomModel {

	public static enum FrameType implements IStringSerializable, IEnumMetaItem{
		BASIC, ENDER;

		@Override
		public int getMetadata() {
			return ordinal();
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}


        public static FrameType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= values().length)
            {
                metadata = 0;
            }

            return values()[metadata];
        }
	}
	
	public ItemMachineFrame(){
		super();
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setCreativeTab(CrystalMod.tabBlocks);
		ModItems.registerItem(this, "machineFrame");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "basic"));
		ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName(), "ender"));
    }
	
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + FrameType.byMetadata(i).getName();
    }
	
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (int i = 0; i < FrameType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
}
