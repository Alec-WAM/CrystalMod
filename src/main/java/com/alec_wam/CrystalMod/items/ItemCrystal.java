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

public class ItemCrystal extends Item implements ICustomModel {

	public ItemCrystal(){
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "crystal");
	}
	
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return stack.getMetadata() !=CrystalType.DIRON_NUGGET.getMetadata();
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(CrystalType type : CrystalType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + CrystalType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (int i = 0; i < CrystalType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	public static enum CrystalType implements IStringSerializable, IEnumMetaItem
    {
        BLUE(0, "blue"),
        RED(1, "red"),
        GREEN(2, "green"),
        DARK(3, "dark"),
        PURE(4, "pure"),
        BLUE_SHARD(5, "blue_shard"),
        RED_SHARD(6, "red_shard"),
        GREEN_SHARD(7, "green_shard"),
        DARK_SHARD(8, "dark_shard"),
        PURE_SHARD(9, "pure_shard"),
        BLUE_NUGGET(10, "blue_nugget"),
        RED_NUGGET(11, "red_nugget"),
        GREEN_NUGGET(12, "green_nugget"),
        DARK_NUGGET(13, "dark_nugget"),
        PURE_NUGGET(14, "pure_nugget"),
        DIRON_NUGGET(15, "diron_nugget");

        private static final CrystalType[] METADATA_LOOKUP = new CrystalType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private CrystalType(int dmg, String name)
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

        public static CrystalType byMetadata(int metadata)
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
            for (CrystalType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
