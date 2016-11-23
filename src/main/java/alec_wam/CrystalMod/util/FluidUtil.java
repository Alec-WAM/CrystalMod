package alec_wam.CrystalMod.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.pipes.item.GhostItemHelper;
import alec_wam.CrystalMod.tiles.pipes.item.filters.CameraFilterInventory;
import alec_wam.CrystalMod.tiles.pipes.item.filters.FilterInventory;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidContainerItemWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidContainerRegistryWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidHandlerWrapper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

public class FluidUtil {

	public static ItemStack EMPTY_BUCKET = new ItemStack(Items.BUCKET);

	public static boolean canCombine(FluidStack stack1, FluidStack stack2){
		if(stack1 == null && stack2 == null)return true;
		if(stack1 != null && stack2 == null)return false;
		if(stack1 == null && stack2 != null)return false;
		return stack1.isFluidEqual(stack2);
	}
	
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
	
	public static ItemStack getEmptyContainer(ItemStack stack){
		if (stack == null) {
	      return null;
	    }

	    ItemStack ret = stack.copy();
	    ret.stackSize = 1;
	    IFluidHandler handler = getFluidHandlerCapability(ret);
	    if (handler != null) {
	    	try{
	    		handler.drain(Integer.MAX_VALUE, true);
	    	} catch(Exception e){
	    		e.printStackTrace();
	    		FluidStack fluid = getFluidTypeFromItem(stack);
	    		if(fluid !=null && hasFluidBucket(fluid)){
	    			return getFluidBucket(fluid);
	    		}
	    	}
	    	return ret;
	    }
		return null;
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

	public static boolean hasFluidBucket(FluidStack stack) {
        return stack.getFluid() == FluidRegistry.WATER || stack.getFluid() == FluidRegistry.LAVA || FluidRegistry.getBucketFluids().contains(stack.getFluid());
    }
	
	public static ItemStack getFluidBucket(FluidStack stack) {
		if(stack !=null){
			ItemStack bucket = EMPTY_BUCKET.copy();
			IFluidHandler handler = getFluidHandlerCapability(bucket);
		    if (handler != null) {
		    	handler.fill(stack, true);
		    }
		    return bucket;
		}
        return null;
    }

	public static IFluidHandler getExternalFluidHandler(World world, BlockPos pos, EnumFacing face) {
		if (world == null || pos == null || face == null) {
	      return null;
	    }
    	TileEntity te = world.getTileEntity(pos);
    	return getFluidHandler(te, face);
	}
	
	@SuppressWarnings("deprecation")
	public static IFluidHandler getFluidHandler(TileEntity tile, EnumFacing side) {
        if (tile == null) {
            return null;
        }

        IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);

        if (handler == null) {
            if (side != null && tile instanceof net.minecraftforge.fluids.IFluidHandler) {
      	      return new FluidHandlerWrapper((net.minecraftforge.fluids.IFluidHandler) tile, side);
    	    }
        }

        return handler;
    }
	
	public static boolean passesFilter(FluidStack fluid, ItemStack filter){
		if(fluid == null || fluid.getFluid() == null || (filter !=null && filter.getItem() !=ModItems.pipeFilter))return false;
	    if(filter !=null){
	    	List<ItemStack> filteredList = new ArrayList<ItemStack>();
	    	if(filter.getMetadata() == FilterType.NORMAL.ordinal()){
		    	FilterInventory inv = new FilterInventory(filter, 10, "");
		    	for (int i = 0; i < inv.getSizeInventory(); i++)
		        {
		            ItemStack stack = inv.getStackInSlot(i);
		            if (stack == null)
		            {
		                continue;
		            }
		            ItemStack ghostStack = GhostItemHelper.getStackFromGhost(stack);
		            filteredList.add(ghostStack);
		        }
		    	boolean black = ItemNBTHelper.getBoolean(filter, "BlackList", false);
		    	
		    	if(filteredList.isEmpty()){
		    		return black ? false : true;
		    	}
		    	
		    	boolean matched = false;
		    	for(ItemStack filterStack : filteredList){
		    		if(filterStack != null) {
		    	        FluidStack fluidS = FluidUtil.getFluidTypeFromItem(filterStack);
		    	        if(fluidS !=null){
		    	        	matched = FluidUtil.canCombine(fluid, fluidS);
		    	        }
		    		}
		    		if(matched) {
		    	        break;
		    		}
		    	}
		    	return black ? matched == false : matched;
	    	}
	    }
	    return true;
	}

	public static boolean isFluidSource(World world, BlockPos pos, IBlockState iblockstate) {
		Block block = iblockstate.getBlock();
		if(block instanceof IFluidBlock){
			return ((IFluidBlock)block).getFilledPercentage(world, pos) == 1.0f;
		}
		if(block instanceof BlockLiquid){
			return ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0;
		}
		return false;
	}
	
}
