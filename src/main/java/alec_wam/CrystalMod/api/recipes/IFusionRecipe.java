package alec_wam.CrystalMod.api.recipes;

import java.util.List;

import alec_wam.CrystalMod.api.tile.IFusionPedestal;
import alec_wam.CrystalMod.api.tile.IPedestal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IFusionRecipe {

	public Object getMainInput();
	
	public List<?> getInputs();
	
	public ItemStack getOutput();
	
	public boolean matches(IFusionPedestal pedistal, World world, List<IPedestal> pedistals);
	
	public String canCraft(IFusionPedestal pedistal, World world, List<IPedestal> pedistals);

	public void finishCrafting(IFusionPedestal pedistal, World world, List<IPedestal> linkedPedistals);
	
	public Vec3d getRecipeColor();
	
}
