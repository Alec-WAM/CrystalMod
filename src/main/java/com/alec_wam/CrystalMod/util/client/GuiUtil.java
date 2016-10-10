package com.alec_wam.CrystalMod.util.client;


public class GuiUtil {

	public static boolean inBounds(int x, int y, int w, int h, int ox, int oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }
	
    public static int calculateOffsetOnScale(int pos, float scale) {
        float multiplier = (pos / scale);

        return (int) multiplier;
    }
}
