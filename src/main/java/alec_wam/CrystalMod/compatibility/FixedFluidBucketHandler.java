package alec_wam.CrystalMod.compatibility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.init.FixedFluidRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * Wrapper for vanilla and forge buckets.
 * Swaps between empty bucket and filled bucket of the correct type.
 */
public class FixedFluidBucketHandler implements IFluidHandlerItem
{
    @Nonnull
    protected ItemStack container;

    public FixedFluidBucketHandler(@Nonnull ItemStack container)
    {
        this.container = container;
    }

    @Nonnull
    @Override
    public ItemStack getContainer()
    {
        return container;
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        /*if (fluid.getFluid() == FluidConversion.WATER || fluid.getFluid() == FluidConversion.LAVA || fluid.getFluid() == FluidConversion.MILK)
        {
            return true;
        }*/
        return true;
    }

    @Nullable
    public FluidStack getFluid()
    {
        return FixedFluidRegistry.getBucketFluid(container);
    }

    protected void setFluid(@Nullable Fluid fluid) {
        if (fluid == null)
        {
            container = new ItemStack(Items.BUCKET);
        }
        else 
        {
            container = FixedFluidRegistry.getFilledBucket(fluid);
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME || getFluid() != null || !canFillFluidType(resource))
        {
            return 0;
        }

        if (doFill)
        {
            setFluid(resource.getFluid());
        }

        return Fluid.BUCKET_VOLUME;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME)
        {
            return null;
        }
        FluidStack fluidStack = getFluid();
        if (fluidStack != null && fluidStack.isFluidEqual(resource))
        {
            if (doDrain)
            {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (container.getCount() != 1 || maxDrain < Fluid.BUCKET_VOLUME)
        {
        	return null;
        }
        
        FluidStack fluidStack = getFluid();
        if (fluidStack != null)
        {
        	if (doDrain)
            {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }
}