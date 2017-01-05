package alec_wam.CrystalMod.blocks.crops.material;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

public interface IPlantType {

	@Nonnull
	public ResourceLocation getRoot();
	@Nonnull
	public ResourceLocation[] getSpites();
	
}
