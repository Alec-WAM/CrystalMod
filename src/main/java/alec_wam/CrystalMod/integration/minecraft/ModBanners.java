package alec_wam.CrystalMod.integration.minecraft;

import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraftforge.common.util.EnumHelper;

public class ModBanners {

	
	public static void init() {
		String crystal = "crystalmod_crystal";
		addBanner(crystal, crystal, "cmcrystal", new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata()));
		String sword = "crystalmod_sword";
		addBanner(sword, sword, "cmsword", new ItemStack(ModItems.crystalSword));
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addBanner(String enumName, String textureName, String id, ItemStack recipeItem){
		final Class<?>[] paramClasses = new Class[] { String.class, String.class, ItemStack.class };
        EnumHelper.addEnum((Class)BannerPattern.class, enumName.toUpperCase(), (Class[])paramClasses, new Object[] { textureName, id, recipeItem });
	}
	
}
