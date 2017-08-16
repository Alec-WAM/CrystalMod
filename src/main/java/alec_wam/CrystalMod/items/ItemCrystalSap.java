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

public class ItemCrystalSap extends Item implements ICustomModel {

	public ItemCrystalSap(){
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabCrops);
		ModItems.registerItem(this, "crystalsap");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(SapType type : SapType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + SapType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < SapType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }

	public static enum SapType implements IStringSerializable, IEnumMetaItem
    {
        BLUE(0, "blue"),
        RED(1, "red"),
        GREEN(2, "green"),
        DARK(3, "dark");

        private static final SapType[] METADATA_LOOKUP = new SapType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private SapType(int dmg, String name)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
        }

        @Override
		public int getMetadata()
        {
            return this.metadata;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        @Override
		public String toString()
        {
            return this.unlocalizedName;
        }

        public static SapType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
            {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        @Override
		public String getName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (SapType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
