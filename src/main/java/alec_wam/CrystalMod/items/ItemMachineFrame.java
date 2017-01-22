package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
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
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabBlocks);
		ModItems.registerItem(this, "machineframe");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("crystalmod:machineframe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation("crystalmod:machineframe", "ender"));
    }
	
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + FrameType.byMetadata(i).getName();
    }
	
	@SideOnly(Side.CLIENT)
    @Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < FrameType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
}
