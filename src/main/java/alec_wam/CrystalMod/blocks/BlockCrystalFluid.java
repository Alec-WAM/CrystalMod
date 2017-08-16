package alec_wam.CrystalMod.blocks;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.fluids.ModFluids;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalFluid extends BlockFluidClassic implements ICustomModel {

  public BlockCrystalFluid(Fluid fluid, Material material) {
    super(fluid, material);
  }

  @Nonnull
  @Override
  public String getUnlocalizedName() {
    Fluid fluid = FluidRegistry.getFluid(fluidName);
    if(fluid != null) {
      return fluid.getUnlocalizedName(stack);
    }
    return super.getUnlocalizedName();
  }
  
  @Override
  public String getLocalizedName()
  {
	  Fluid fluid = FluidRegistry.getFluid(fluidName);
	  if(fluid != null) {
		  return fluid.getLocalizedName(stack);
	  }
	  return "Error";
  }
  
  @SideOnly(Side.CLIENT)
  @Override
  public void initModel() {
	  Fluid fluid = FluidRegistry.getFluid(fluidName);
	  if(fluid != null) {
		  ModFluids.registerFluidModels(fluid);
	  }
  }
}
