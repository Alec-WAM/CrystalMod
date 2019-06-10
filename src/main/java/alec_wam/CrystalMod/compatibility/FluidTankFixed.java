package alec_wam.CrystalMod.compatibility;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FluidTankFixed extends FluidTank {

	public FluidTankFixed(int capacity)
    {
        super(capacity);
    }

    public FluidTankFixed(@Nullable FluidStack fluidStack, int capacity)
    {
    	super(fluidStack, capacity);
    }

    public FluidTankFixed(Fluid fluid, int amount, int capacity)
    {
    	super(new FluidStackFixed(fluid, amount), capacity);
    }

    @Override
    public FluidTank readFromNBT(CompoundNBT nbt)
    {
        if (!nbt.contains("Empty"))
        {
            FluidStack fluid = FluidConversion.loadFluidStackFromNBT(nbt);
            setFluid(fluid);
        }
        else
        {
            setFluid(null);
        }
        return this;
    }

    @Override
    public CompoundNBT writeToNBT(CompoundNBT nbt)
    {
        if (fluid != null)
        {
        	FluidConversion.writeToNBT(fluid, nbt);
        }
        else
        {
            nbt.putString("Empty", "");
        }
        return nbt;
    }
    
    @Override
    public int fillInternal(FluidStack resource, boolean doFill)
    {
        if (resource == null || resource.amount <= 0)
        {
            return 0;
        }

        if (!doFill)
        {
            if (fluid == null)
            {
                return Math.min(capacity, resource.amount);
            }

            if (!fluid.isFluidEqual(resource))
            {
                return 0;
            }

            return Math.min(capacity - fluid.amount, resource.amount);
        }

        if (fluid == null)
        {
            fluid = new FluidStackFixed(resource, Math.min(capacity, resource.amount));

            onContentsChanged();

            if (tile != null)
            {
                FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorld(), tile.getPos(), this, fluid.amount));
            }
            return fluid.amount;
        }

        if (!fluid.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = capacity - fluid.amount;

        if (resource.amount < filled)
        {
            fluid.amount += resource.amount;
            filled = resource.amount;
        }
        else
        {
            fluid.amount = capacity;
        }

        onContentsChanged();

        if (tile != null)
        {
            FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorld(), tile.getPos(), this, filled));
        }
        return filled;
    }
    
    @Override
    @Nullable
    public FluidStack drainInternal(int maxDrain, boolean doDrain)
    {
        if (fluid == null || maxDrain <= 0)
        {
            return null;
        }

        int drained = maxDrain;
        if (fluid.amount < drained)
        {
            drained = fluid.amount;
        }

        FluidStack stack = new FluidStackFixed(fluid, drained);
        if (doDrain)
        {
            fluid.amount -= drained;
            if (fluid.amount <= 0)
            {
                fluid = null;
            }

            onContentsChanged();

            if (tile != null)
            {
                FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(fluid, tile.getWorld(), tile.getPos(), this, drained));
            }
        }
        return stack;
    }
	
}
