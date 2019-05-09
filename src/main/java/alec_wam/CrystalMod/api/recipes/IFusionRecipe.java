package alec_wam.CrystalMod.api.recipes;

import java.util.List;

import alec_wam.CrystalMod.api.tile.IFusionPedistal;
import alec_wam.CrystalMod.api.tile.IPedistal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IFusionRecipe {

	public Object getMainInput();
	
	public List<?> getInputs();
	
	public ItemStack getOutput();
	
	public boolean matches(IFusionPedistal pedistal, World world, List<IPedistal> pedistals);
	
	public String canCraft(IFusionPedistal pedistal, World world, List<IPedistal> pedistals);

	public void finishCrafting(IFusionPedistal pedistal, World world, List<IPedistal> linkedPedistals);
	
	public Vec3d getRecipeColor();
	
}
