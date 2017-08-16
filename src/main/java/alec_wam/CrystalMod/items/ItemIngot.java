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

public class ItemIngot extends Item implements ICustomModel {

	public static int RGB_BLUE = 0x00fefe;
	public static int RGB_RED = 0xfe0000;
	public static int RGB_GREEN = 0x00fe00;
	public static int RGB_DARK = 0x373737;
	public static int RGB_PURE = 0xffffff;
	public static int RGB_DARK_IRON = 0x616161;
	
	public ItemIngot(){
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "crystalingot");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return (stack.getItemDamage() !=IngotType.DARK_IRON.getMetadata());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(IngotType type : IngotType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + IngotType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < IngotType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	public static enum IngotType implements IStringSerializable, IEnumMetaItem
    {
        BLUE(0, "blue"),
        RED(1, "red"),
        GREEN(2, "green"),
        DARK(3, "dark"),
        PURE(4, "pure"),
        DARK_IRON(5, "darkIron");

        private static final IngotType[] METADATA_LOOKUP = new IngotType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private IngotType(int dmg, String name)
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

        public static IngotType byMetadata(int metadata)
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
            for (IngotType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
