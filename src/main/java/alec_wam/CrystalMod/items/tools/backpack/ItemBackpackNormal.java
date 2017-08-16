package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBackpackNormal extends ItemBackpackBase implements ICustomModel {

	public ItemBackpackNormal(IBackpack backpack) {
		super(backpack);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
		super.initModel();
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + CrystalBackpackType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < CrystalBackpackType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	public static enum CrystalBackpackType implements IStringSerializable, IEnumMetaItem
    {
		NORMAL(0, "normal"),
		DARK_IRON(1, "darkiron"),
		BLUE(2, "blue"),
        RED(3, "red"),
        GREEN(4, "green"),
        DARK(5, "dark"),
        PURE(6, "pure");

        private static final CrystalBackpackType[] METADATA_LOOKUP = new CrystalBackpackType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private CrystalBackpackType(int dmg, String name)
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

        public static CrystalBackpackType byMetadata(int metadata)
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
            for (CrystalBackpackType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }

}
