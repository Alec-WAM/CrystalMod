package alec_wam.CrystalMod.client.model.dynamic;

import java.util.Arrays;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomItemRendererHandler {

	public static CustomItemRendererHandler instance = new CustomItemRendererHandler();
	
	public boolean test(){
		return false;
	}
	
	public boolean overrideRender(ItemStack itemStackIn)
	{
		if(ItemStackTools.isValid(itemStackIn)){
			String registryName = itemStackIn.getItem().getRegistryName().getResourcePath();
			Item item = itemStackIn.getItem();
			ICustomItemRenderer render = ClientProxy.getRenderer(item);
			if(render !=null){
				FMLClientHandler.instance().getClient().mcProfiler.startSection("crystalmod-itemrender-"+registryName);
				GlStateManager.pushMatrix();
				GlStateManager.rotate(180, 0, 1, 0);
				render.render(itemStackIn);
				GlStateManager.popMatrix();
				FMLClientHandler.instance().getClient().mcProfiler.endSection();
				return false;
			}
			
			if(Config.vanillaMinecarts3d){
	    		Item[] minecarts = new Item[] {Items.MINECART, Items.CHEST_MINECART, Items.FURNACE_MINECART, Items.TNT_MINECART, Items.HOPPER_MINECART, Items.COMMAND_BLOCK_MINECART};
	    		if(Arrays.asList(minecarts).contains(item)){
	    			FMLClientHandler.instance().getClient().mcProfiler.startSection("crystalmod-itemrender-"+registryName);
	    			GlStateManager.pushMatrix();
					GlStateManager.rotate(180, 0, 1, 0);
					ClientProxy.MinecartRenderer3d.render(itemStackIn);
					GlStateManager.popMatrix();
					FMLClientHandler.instance().getClient().mcProfiler.endSection();
	    			return false;
	    		}
			}			
		}
		return true;
	}
	
	public boolean renderByItem(ItemStack itemStackIn)
	{
		//Item item = itemStackIn.getItem();
		/*if(item == Item.getItemFromBlock(Blocks.CHEST)){
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.rotate(180, 0, 1, 0);
			ClientProxy.MinecartRenderer3d.render(new ItemStack(Items.MINECART));
			GlStateManager.popMatrix();
			
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			
			return false;
		}*/
		return false;
	}
}
