package alec_wam.CrystalMod.enchantment;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModEnchantments {
	
	public static EnchantmentDoubleJump jump;
	
	public static void init(){
		jump = new EnchantmentDoubleJump();
		MinecraftForge.EVENT_BUS.register(jump);
		GameRegistry.register(jump);
	}
	
}
