package alec_wam.CrystalMod.items.enchancements;

import alec_wam.CrystalMod.api.enhancements.EnhancementManager;

public class ModEnhancements {

	public static final EnhancementDragonWings DRAGON_WINGS = new EnhancementDragonWings();
	public static final EnhancementInvisibleArmor INVIS_ARMOR = new EnhancementInvisibleArmor();
	public static final EnhancementWaterWalking WATER_WALKING = new EnhancementWaterWalking();

	public static void init(){
		EnhancementManager.register(DRAGON_WINGS);
		EnhancementManager.register(INVIS_ARMOR);
		EnhancementManager.register(WATER_WALKING);
	}
	
}
