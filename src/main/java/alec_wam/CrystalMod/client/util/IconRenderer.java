package alec_wam.CrystalMod.client.util;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class IconRenderer {

	public static final ResourceLocation ICON_TEXTURE = CrystalMod.resourceL("textures/gui/icons.png");
	
	public static enum Icon {
		REDSTONE_NONE(0, 0),
		REDSTONE_IGNORE(0, 1),
		REDSTONE_OFF(0, 2),
		REDSTONE_ON(0, 3),
		
		WEATHER_SUNNY(1, 0),
		WEATHER_RAIN(1, 1),
		WEATHER_STORM(1, 2);
		
		
		private int x, y;
		Icon(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}
	}
	
	public static void renderIcon(Icon icon, int x, int y){
		Minecraft.getMinecraft().renderEngine.bindTexture(ICON_TEXTURE);
		GuiScreen.drawModalRectWithCustomSizedTexture(x, y, icon.getX() * 16, icon.getY() * 16, 16, 16, 256, 256);
	}
}
