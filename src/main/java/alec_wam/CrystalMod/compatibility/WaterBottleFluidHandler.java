package alec_wam.CrystalMod.compatibility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.init.FixedFluidRegistry;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class WaterBottleFluidHandler implements IFluidHandlerItem
{
	public static final int BOTTLE_CAPACITY = 250;
	
    @Nonnull
    protected ItemStack container;

    public WaterBottleFluidHandler(@Nonnull ItemStack container)
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
       return fluid.getFluid() == FixedFluidRegistry.WATER;
    }

    @Nullable
    public FluidStack getFluid()
    {
    	if(container.getItem() == Items.POTION){
    		String potion = ItemNBTHelper.getString(container, "Potion", "");
    		@SuppressWarnings("deprecation")
			ResourceLocation resourcelocation = Registry.POTION.getKey(Potions.WATER);
    		if(potion.equalsIgnoreCase(resourcelocation.toString())){
    			return new FluidStackFixed(FixedFluidRegistry.WATER, BOTTLE_CAPACITY);
    		}
    	}
        return null;
    }

    protected void setFluid(@Nullable Fluid fluid) {
        if (fluid == null)
        {
            container = new ItemStack(Items.GLASS_BOTTLE);
        }
        else 
        {
        	ItemStack stack = new ItemStack(Items.POTION);
        	PotionUtils.addPotionToItemStack(stack, Potions.WATER);
            container = stack;
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), BOTTLE_CAPACITY) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.getCount() != 1 || resource == null || resource.amount < BOTTLE_CAPACITY || getFluid() != null || !canFillFluidType(resource))
        {
            return 0;
        }

        if (doFill)
        {
            setFluid(resource.getFluid());
        }

        return BOTTLE_CAPACITY;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (container.getCount() != 1 || resource == null || resource.amount < BOTTLE_CAPACITY)
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
        if (container.getCount() != 1 || maxDrain < BOTTLE_CAPACITY)
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