package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMiscFood extends ItemFood implements ICustomModel {

	public ItemMiscFood() {
		super(1, false);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "miscfood");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(FoodType type : FoodType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + FoodType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < FoodType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	@Override
	public int getHealAmount(ItemStack stack)
    {
		FoodType type = FoodType.byMetadata(stack.getMetadata());
        return type.getFoodValue();
    }

	@Override
	public float getSaturationModifier(ItemStack stack)
    {
        return 0.6f;
    }
    
    public static enum FoodType implements IStringSerializable, IEnumMetaItem
    {
        CORN_COB(0, "corn_cob", 4),
        POPCORN(1, "popcorn", 4),
        WHITE_FISH_RAW(2, "whitefish_raw", 3),
        WHITE_FISH_COOKED(3, "whitefish_cooked", 8);

        private static final FoodType[] METADATA_LOOKUP = new FoodType[values().length];
        private final int metadata;
        private final String unlocalizedName;
        private final int foodValue;

        private FoodType(int dmg, String name, int foodValue)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
            this.foodValue = foodValue;
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
        
        public int getFoodValue()
        {
            return this.foodValue;
        }

        @Override
		public String toString()
        {
            return this.unlocalizedName;
        }

        public static FoodType byMetadata(int metadata)
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
            for (FoodType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }

}
