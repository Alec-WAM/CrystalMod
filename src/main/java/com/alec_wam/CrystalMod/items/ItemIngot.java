package com.alec_wam.CrystalMod.items;

import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.ICustomModel;

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
		ModItems.registerItem(this, "crystalIngot");
	}
	
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return (stack.getItemDamage() !=IngotType.DARK_IRON.getMetadata());
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(IngotType type : IngotType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + IngotType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
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

        public static IngotType byMetadata(int metadata)
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
            for (IngotType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
