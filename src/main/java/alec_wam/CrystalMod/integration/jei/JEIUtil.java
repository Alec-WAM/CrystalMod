package alec_wam.CrystalMod.integration.jei;

import mezz.jei.api.IItemListOverlay;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JEIUtil {

	public static boolean isInstalled(){
		return Loader.isModLoaded("JEI");
	}
	
	@SideOnly(Side.CLIENT)
	public static IItemListOverlay getItemListOverlay(){
		return JEIPlugin.runtime.getItemListOverlay();
	}
	
	@SideOnly(Side.CLIENT)
	public static void setFilterText(String text){
		IItemListOverlay overlay = getItemListOverlay();
		if(overlay !=null){
			overlay.setFilterText(text);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static String getFilterText(){
		IItemListOverlay overlay = getItemListOverlay();
		if(overlay !=null){
			return overlay.getFilterText();
		}
		return "";
	}
	
}
