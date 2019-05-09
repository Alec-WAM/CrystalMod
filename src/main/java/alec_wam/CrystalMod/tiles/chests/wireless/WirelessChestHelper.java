package alec_wam.CrystalMod.tiles.chests.wireless;

import net.minecraft.item.EnumDyeColor;

public class WirelessChestHelper {

	public static final String NBT_OWNER = "Owner";
	public static final String PUBLIC_OWNER = "{Public}";
	public static final String NBT_CODE = "Code";
	
	public static int getDefaultCode(EnumDyeColor dye) {
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
	
	public static EnumDyeColor getDye1(int code){
		return EnumDyeColor.byId(getColor1(code));
	}
	
	public static EnumDyeColor getDye2(int code){
		return EnumDyeColor.byId(getColor2(code));
	}
	
	public static EnumDyeColor getDye3(int code){
		return EnumDyeColor.byId(getColor3(code));
	}
	
}
