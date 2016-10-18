package alec_wam.CrystalMod.blocks;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.fluids.Fluids;

public class BlockCrystalFluid extends BlockFluidClassic implements ICustomModel {

  public BlockCrystalFluid(Fluid fluid, Material material) {
    super(fluid, material);
  }

  @Nonnull
  @Override
  public String getUnlocalizedName() {
    Fluid fluid = FluidRegistry.getFluid(fluidName);
    if(fluid != null) {
      return fluid.getUnlocalizedName();
    }
    return super.getUnlocalizedName();
  }
  
  @SideOnly(Side.CLIENT)
  @Override
  public void initModel() {
	  Fluid fluid = FluidRegistry.getFluid(fluidName);
	    if(fluid != null) {
	    	Fluids.registerFluidModels(fluid);
	    }
  }
}
