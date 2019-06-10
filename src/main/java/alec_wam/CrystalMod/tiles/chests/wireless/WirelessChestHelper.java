package alec_wam.CrystalMod.tiles.chests.wireless;

import net.minecraft.item.DyeColor;

public class WirelessChestHelper {

	public static final String NBT_OWNER = "Owner";
	public static final String PUBLIC_OWNER = "{Public}";
	public static final String NBT_CODE = "Code";
	
	public static int getDefaultCode(DyeColor dye) {
		int meta = dye.getId();
		return (meta) | (meta << 4) | (meta << 8);
	}

	public static int getColor1(int code) {
		return code & 15;
	}
	
	public static int getColor2(int code) {
		return (code >> 4) & 15;
	}
	
	public static int getColor3(int code) {
		return (code >> 8) & 15;
	}
	
	public static DyeColor getDye1(int code){
		return DyeColor.byId(getColor1(code));
	}
	
	public static DyeColor getDye2(int code){
		return DyeColor.byId(getColor2(code));
	}
	
	public static DyeColor getDye3(int code){
		return DyeColor.byId(getColor3(code));
	}
	
}
