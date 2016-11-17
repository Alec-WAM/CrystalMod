package alec_wam.CrystalMod.client.model.dynamic;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.spawner.ItemRenderMobEssence;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomItemRendererHandler {

	public static CustomItemRendererHandler instance = new CustomItemRendererHandler();
	
	public boolean renderByItem(ItemStack itemStackIn)
	{
		if(itemStackIn !=null){
			ICustomItemRenderer render = ClientProxy.getRenderer(itemStackIn.getItem());
			if(render !=null){
				render.render(itemStackIn);
				return true;
			}
		}
		
		return false;
	}
}
