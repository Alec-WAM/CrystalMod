package alec_wam.CrystalMod.util;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidContainerItemWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidContainerRegistryWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidHandlerWrapper;

public class FluidUtil {

	public static IFluidHandler getFluidHandlerCapability(@Nullable ICapabilityProvider provider, EnumFacing side) {
	    if (provider != null && provider.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
	      return provider.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
	    }
	    return getLegacyHandler(provider, side);
	}
	
	@Deprecated
	private static IFluidHandler getLegacyHandler(ICapabilityProvider provider, EnumFacing side) {
	    if (provider instanceof ItemStack) {
	      ItemStack stack = (ItemStack) provider;
	      if (stack.getItem() instanceof IFluidContainerItem) {
	        return new FluidContainerItemWrapper((IFluidContainerItem) stack.getItem(), stack);
	      }
	      if(FluidContainerRegistry.isContainer(stack)) {
	        return new FluidContainerRegistryWrapper(stack);
	      }
	    }
	    if (provider instanceof net.minecraftforge.fluids.IFluidHandler) {
	      return new FluidHandlerWrapper((net.minecraftforge.fluids.IFluidHandler) provider, side);
	    }
	    return null;
	}

	public static IFluidHandler getFluidHandlerCapability(ItemStack stack) {
	    return getFluidHandlerCapability(stack, null);
	}
	
	public static FluidStack getFluidTypeFromItem(ItemStack stack) {
	    if (stack == null) {
	      return null;
	    }

	    stack = stack.copy();
	    stack.stackSize = 1;
	    IFluidHandler handler = getFluidHandlerCapability(stack);
	    if (handler != null) {
	      return handler.drain(Fluid.BUCKET_VOLUME, false);
	    }
	    if (Block.getBlockFromItem(stack.getItem()) instanceof IFluidBlock) {
	      Fluid fluid = ((IFluidBlock) Block.getBlockFromItem(stack.getItem())).getFluid();
	      if (fluid != null) {
	        return new FluidStack(fluid, 1000);
	      }
	    }
	    return null;

	}
	
}
