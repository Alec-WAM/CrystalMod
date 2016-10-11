package alec_wam.CrystalMod.tiles.pipes.item.filters;

import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;

public class ItemPipeFilter extends Item implements ICustomModel {
	
	public static enum FilterType{
		NORMAL, MOD, CAMERA;
	}
	
	public ItemPipeFilter(){
    	super();
    	this.setHasSubtypes(true);
    	this.setMaxDamage(0);
    	this.setCreativeTab(CrystalMod.tabItems);
    	ModItems.registerItem(this, "pipefilter");
    }
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(FilterType type : FilterType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.ordinal(), new ModelResourceLocation(getRegistryName(), type.name().toLowerCase()));
        }
    }
	
	public String getUnlocalizedName(ItemStack stack){
		return super.getUnlocalizedName(stack)+"."+FilterType.values()[stack.getMetadata() % FilterType.values().length].name().toLowerCase();
	}
	
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
		for(int m = 0; m < FilterType.values().length; m++){
			list.add(new ItemStack(this, 1, m));
		}
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv){
		if(stack.getMetadata() == FilterType.NORMAL.ordinal()){
			boolean black = ItemNBTHelper.getBoolean(stack, "BlackList", false);
			boolean meta = ItemNBTHelper.getBoolean(stack, "MetaMatch", true);
			boolean nbtMatch = ItemNBTHelper.getBoolean(stack, "NBTMatch", true);
			boolean oreMatch = ItemNBTHelper.getBoolean(stack, "OreMatch", false);
			list.add("Mode: "+(black?"Block":"Allow"));
			list.add("Match Metadata: "+(meta?"Enabled":"Disabled"));
			list.add("Match NBT: "+(nbtMatch?"Enabled":"Disabled"));
			list.add("Use Ore Dictionary: "+(oreMatch?"Enabled":"Disabled"));
		}
	}
    
}
