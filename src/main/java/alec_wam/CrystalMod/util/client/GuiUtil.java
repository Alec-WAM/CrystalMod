package alec_wam.CrystalMod.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiUtil {

	public static boolean inBounds(int x, int y, int w, int h, int ox, int oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }
	
    public static int calculateOffsetOnScale(int pos, float scale) {
        float multiplier = (pos / scale);

        return (int) multiplier;
    }
    public static final ResourceLocation BACKGROUND_BLANK = new ResourceLocation("crystalmod:textures/gui/blank.png");
    public static void renderBlankGuiBackground(int x, int y, int xSize, int ySize){
    	Minecraft.getMinecraft().renderEngine.bindTexture(BACKGROUND_BLANK);
	    GuiScreen.drawScaledCustomSizeModalRect(x, y, 0, 0, 256, 256, xSize, ySize, 256, 256);
    }
}
