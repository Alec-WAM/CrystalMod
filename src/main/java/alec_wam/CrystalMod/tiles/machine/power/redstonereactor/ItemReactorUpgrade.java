package alec_wam.CrystalMod.tiles.machine.power.redstonereactor;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemReactorUpgrade extends Item implements ICustomModel {

	public ItemReactorUpgrade(){
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "reactorupgrade");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(UpgradeType type : UpgradeType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + UpgradeType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < UpgradeType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }

	public static enum UpgradeType implements IStringSerializable, IEnumMetaItem
    {
        BASIC(0, "basic"),
        ADVANCED(1, "advanced"),
        SUPER(2, "super");

        private static final UpgradeType[] METADATA_LOOKUP = new UpgradeType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private UpgradeType(int dmg, String name)
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

        public static UpgradeType byMetadata(int metadata)
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
            for (UpgradeType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
