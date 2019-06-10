package alec_wam.CrystalMod.compatibility;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;

public class FluidStackFixed extends FluidStack {

	public final Fluid fluid;
	
	public FluidStackFixed(Fluid fluid, int amount) {
		super(fluid, amount);
		this.fluid = fluid;
		IRegistryDelegate<Fluid> fluidDelegate = new IRegistryDelegate<Fluid>(){

			@Override
			public Fluid get() {
				return fluid;
			}

			@Override
			public ResourceLocation name() {
				return new ResourceLocation(fluid.getUnlocalizedName());
			}

			@Override
			public Class<Fluid> type() {
				return Fluid.class;
			}
			
		};
		ObfuscationReflectionHelper.setPrivateValue(FluidStack.class, this, fluidDelegate, 3);
	}
	
	public FluidStackFixed(Fluid fluid, int amount, CompoundNBT nbt)
    {
		this(fluid, amount);
		if(nbt !=null){
			this.tag = nbt.copy();
		}
    }

    public FluidStackFixed(FluidStack stack, int amount)
    {
        this(stack.getFluid(), amount, stack.tag);
    }
}
