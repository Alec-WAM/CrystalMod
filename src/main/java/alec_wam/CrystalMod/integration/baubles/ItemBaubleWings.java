package alec_wam.CrystalMod.integration.baubles;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import baubles.api.BaubleType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

public class ItemBaubleWings extends ItemBauble {
	
	public ItemBaubleWings(){
		super();
		setMaxStackSize(1);
		this.setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "dragonWingBauble");
	}
	
	@Override
	@Optional.Method(modid = "Baubles")
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.BODY;
	}

}
