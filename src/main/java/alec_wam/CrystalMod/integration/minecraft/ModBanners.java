package alec_wam.CrystalMod.integration.minecraft;

import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraftforge.common.util.EnumHelper;

public class ModBanners {

	
	public static void init() {
		String crystal = "crystalmod_crystal";
		addBanner(crystal, crystal, "cmcrystal", new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMeta()));
		
		String sword = "sword";
		addBanner(sword, sword, "sword", new ItemStack(Items.STONE_SWORD));
		
		String octagon = "crystalmod_octagon";
		addBanner(octagon, octagon, "cmoctagon", "# #", "   ", "# #");
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addBanner(String enumName, String textureName, String id, ItemStack recipeItem){
		final Class<?>[] paramClasses = new Class[] { String.class, String.class, ItemStack.class };
        EnumHelper.addEnum((Class)BannerPattern.class, enumName.toUpperCase(), (Class[])paramClasses, new Object[] { textureName, id, recipeItem });
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addBanner(String enumName, String textureName, String id, String row1, String row2, String row3){
		final Class<?>[] paramClasses = new Class[] { String.class, String.class, String.class, String.class, String.class };
        EnumHelper.addEnum((Class)BannerPattern.class, enumName.toUpperCase(), (Class[])paramClasses, new Object[] { textureName, id, row1, row2, row3 });
	}
	
}
