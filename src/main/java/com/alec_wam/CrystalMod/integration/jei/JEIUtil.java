package com.alec_wam.CrystalMod.integration.jei;

import mezz.jei.GuiEventHandler;
import mezz.jei.Internal;
import mezz.jei.JustEnoughItems;
import mezz.jei.ProxyCommonClient;
import mezz.jei.gui.ItemListOverlay;
import mezz.jei.gui.RecipesGui;
import mezz.jei.input.IShowsRecipeFocuses;
import mezz.jei.input.InputHandler;

import java.util.List;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JEIUtil {

	public static boolean isInstalled(){
		return Loader.isModLoaded("JEI");
	}
	
	@SideOnly(Side.CLIENT)
	public static GuiEventHandler getGuiEventHandler(){
		ProxyCommonClient common = (ProxyCommonClient) JustEnoughItems.getProxy(); 
		try{
			return ObfuscationReflectionHelper.getPrivateValue(ProxyCommonClient.class, common, 1);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static ItemListOverlay getItemListOverlay(){
		return Internal.getRuntime().getItemListOverlay();
	}
	
	@SideOnly(Side.CLIENT)
	public static InputHandler getInputHandler(){
		GuiEventHandler handler = getGuiEventHandler();
		if(handler !=null){
			try{
				return ObfuscationReflectionHelper.getPrivateValue(GuiEventHandler.class, handler, 2);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static RecipesGui getRecipesGui(){
		return (RecipesGui) Internal.getRuntime().getRecipesGui();
	}
	
	@SideOnly(Side.CLIENT)
	public static List<IShowsRecipeFocuses> getIShowsRecipeFocuses(){
		InputHandler handler = getInputHandler();
		if(handler !=null){
			try{
				return ObfuscationReflectionHelper.getPrivateValue(InputHandler.class, handler, 3);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static void setFilterText(String text){
		ItemListOverlay overlay = getItemListOverlay();
		if(overlay !=null && !overlay.hasKeyboardFocus()){
			overlay.setFilterText(text);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static String getFilterText(){
		ItemListOverlay overlay = getItemListOverlay();
		if(overlay !=null){
			return overlay.getFilterText();
		}
		return "";
	}
	
}
