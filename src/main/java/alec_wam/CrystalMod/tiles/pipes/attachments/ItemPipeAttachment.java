package alec_wam.CrystalMod.tiles.pipes.attachments;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.client.model.CustomBakedModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;

import com.google.common.base.Strings;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPipeAttachment extends Item implements ICustomModel {

	public ItemPipeAttachment(){
		super();
		this.setCreativeTab(CrystalMod.tabCovers);
		ModItems.registerItem(this, "pipeattachment");
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public void initModel(){
    	ClientProxy.registerCustomModel(new CustomBakedModel(new ModelResourceLocation(getRegistryName(), "inventory"), ModelAttachment.INSTANCE){
    		public void preModelRegister(){
    			ModelAttachment.map.clear();
    		}
    	});
    }
	
	public static String getID(ItemStack stack){
		if(ItemNBTHelper.verifyExistance(stack, "ID")){
			return ItemNBTHelper.getString(stack, "ID", "");
		}
		return "";
	}
	
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
		for(String id : AttachmentUtil.getIds()){
			ItemStack stack = new ItemStack(item);
			ItemNBTHelper.setString(stack, "ID", id);
			list.add(stack);
		}
	}
	
	public String getUnlocalizedName(ItemStack stack){
		String id = getID(stack);
		return super.getUnlocalizedName() + (!Strings.isNullOrEmpty(id) ? "." + getID(stack) : "");
	}
	
}
