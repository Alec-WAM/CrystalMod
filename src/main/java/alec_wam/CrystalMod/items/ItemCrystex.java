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

public class ItemCrystex extends Item implements ICustomModel {

	public ItemCrystex(){
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "crystexitem");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(CrystexItemType type : CrystexItemType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + CrystexItemType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < CrystexItemType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	public static enum CrystexItemType implements IStringSerializable, IEnumMetaItem
    {
        CRYSTEXUS(0, "crystexus"),
        CRYSTEXIUM_ESSENCE(1, "crystexium_essence"),
        CRYSTEXIUM_ESSENCE_BLUE(2, "crystexium_essence_blue"),
        CRYSTEXIUM_ESSENCE_RED(3, "crystexium_essence_red"),
        CRYSTEXIUM_ESSENCE_GREEN(4, "crystexium_essence_green"),
        CRYSTEXIUM_ESSENCE_DARK(5, "crystexium_essence_dark"),
        CRYSTEXIUM_ESSENCE_PURE(6, "crystexium_essence_pure"),
        CRYSTEXIUM_BRICK(7, "crystexium_brick"),
        CRYSTEXIUM_BRICK_BLUE(8, "crystexium_brick_blue"),
        CRYSTEXIUM_BRICK_RED(9, "crystexium_brick_red"),
        CRYSTEXIUM_BRICK_GREEN(10, "crystexium_brick_green"),
        CRYSTEXIUM_BRICK_DARK(11, "crystexium_brick_dark"),
        CRYSTEXIUM_BRICK_PURE(12, "crystexium_brick_pure");

        private static final CrystexItemType[] METADATA_LOOKUP = new CrystexItemType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private CrystexItemType(int dmg, String name)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
        }

        public int getMetadata()
        {
            return this.metadata;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        public String toString()
        {
            return this.unlocalizedName;
        }

        public static CrystexItemType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
            {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        public String getName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (CrystexItemType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
