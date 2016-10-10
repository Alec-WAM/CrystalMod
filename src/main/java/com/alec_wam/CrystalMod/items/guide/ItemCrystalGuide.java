package com.alec_wam.CrystalMod.items.guide;

import java.util.List;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.ICustomModel;
import com.alec_wam.CrystalMod.items.IEnumMetaItem;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.proxy.CommonProxy;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.ModLogger;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrystalGuide extends Item implements ICustomModel {

	public ItemCrystalGuide(){
		setMaxStackSize(1);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "guide");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(GuideType type : GuideType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + GuideType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (int i = 0; i < GuideType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		if(itemStackIn !=null){
			if(playerIn.isSneaking()){
				GuidePages.createPages();
				ModLogger.info("Created Pages");
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
			}
			if(ItemNBTHelper.verifyExistance(itemStackIn, "LastPage")){
				playerIn.openGui(CrystalMod.instance, CommonProxy.GUI_ID_ITEM, worldIn, ItemNBTHelper.getInteger(itemStackIn, "LastPage", 0), 0, 0);
			} else { 
				playerIn.openGui(CrystalMod.instance, CommonProxy.GUI_ID_ITEM, worldIn, 0, 0, 0);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
    }
	
	public static enum GuideType implements IStringSerializable, IEnumMetaItem
    {
        CRYSTAL(0, "crystal"),
        ESTORAGE(1, "estorage");

        private static final GuideType[] METADATA_LOOKUP = new GuideType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private GuideType(int dmg, String name)
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

        public static GuideType byMetadata(int metadata)
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
            for (GuideType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
	
}
