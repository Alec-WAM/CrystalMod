package alec_wam.CrystalMod.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidColored extends Fluid {

	  public static ResourceLocation LiquidStill = new ResourceLocation("crystalmod", "blocks/fluid/liquid");
	  public static ResourceLocation LiquidFlowing = new ResourceLocation("crystalmod", "blocks/fluid/liquid_flow");

	  public final int color;

	  public FluidColored(String fluidName, int color) {
	    this(fluidName, color, LiquidStill, LiquidFlowing);
	  }

	  public FluidColored(String fluidName, int color, ResourceLocation still, ResourceLocation flowing) {
	    super(fluidName, still, flowing);

	    // make opaque if no alpha is set
	    if(((color >> 24) & 0xFF) == 0) {
	      color |= 0xFF << 24;
	    }
	    this.color = color;
	  }


	  @Override
	  public int getColor() {
	    return color;
	  }
}
