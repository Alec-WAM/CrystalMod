package alec_wam.CrystalMod.asm;

import java.util.Arrays;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ASMMethods {

	public static ASMMethods instance = new ASMMethods();

	
	public static boolean onEntityItemAttacked(EntityItem item, DamageSource source){
		if(ItemStackTools.isValid(item.getEntityItem())){
			ItemStack stack = item.getEntityItem();
			if(stack.getItem() == Items.APPLE){
				return false;
			}
		}
		return true;
	}
	
	@SideOnly(Side.CLIENT)
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

			if(Config.vanillaBoats3d){
				Item[] boats = new Item[] {Items.BOAT, Items.BIRCH_BOAT, Items.SPRUCE_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT};
	    		if(Arrays.asList(boats).contains(item)){
	    			FMLClientHandler.instance().getClient().mcProfiler.startSection("crystalmod-itemrender-"+registryName);
	    			GlStateManager.pushMatrix();
					GlStateManager.rotate(180, 0, 1, 0);
					ClientProxy.BoatRenderer3d.render(itemStackIn);
					GlStateManager.popMatrix();
					FMLClientHandler.instance().getClient().mcProfiler.endSection();
	    			return false;
	    		}
			}
		}
		return true;
	}
}
