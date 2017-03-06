package alec_wam.CrystalMod.tiles.pipes.attachments;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.client.model.CustomBakedModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
    	ModItems.initBasicModel(this);
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
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		for(String id : AttachmentUtil.getIds()){
			list.add(getAttachmentStack(id));
		}
	}
	
	public static ItemStack getAttachmentStack(String id){
		ItemStack stack = new ItemStack(ModItems.pipeAttachmant);
		ItemNBTHelper.setString(stack, "ID", id);
		return stack;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack){
		String id = getID(stack);
		return super.getUnlocalizedName() + (!Strings.isNullOrEmpty(id) ? "." + getID(stack) : "");
	}
	
}
