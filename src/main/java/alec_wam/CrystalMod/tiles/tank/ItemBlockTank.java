package alec_wam.CrystalMod.tiles.tank;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.blocks.ItemBlockMeta;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;

public class ItemBlockTank extends ItemBlockMeta {

	public ItemBlockTank(Block block) {
		super(block);
	}
	private static final FluidTank dummy = new FluidTank(FluidRegistry.WATER, 16000, 16000);
	private FluidTank loadTank(ItemStack stack) {
		int tankType = stack.getMetadata();
		tankType = MathHelper.clamp_int(tankType, 0, BlockTank.tankCaps.length);
		int cap = BlockTank.tankCaps[tankType]*Fluid.BUCKET_VOLUME;
	    if (stack.hasTagCompound()) {
	      FluidTank tank = loadTank(stack.getTagCompound());
	      if(tank !=null){
	    	  tank.setCapacity(cap);
	    	  return tank;
	      }
	    }
	    return new FluidTank(cap);
	}
	
	public static FluidTank loadTank(NBTTagCompound nbtRoot) {
		int tankType = nbtRoot.hasKey("tankType") ? nbtRoot.getInteger("tankType") : 0;
		tankType = MathHelper.clamp_int(tankType, 0, BlockTank.tankCaps.length);
		int cap = BlockTank.tankCaps[tankType]*Fluid.BUCKET_VOLUME;
	    FluidTank ret = new FluidTank(cap);
	    
	    if(nbtRoot.hasKey(FluidHandlerItemStack.FLUID_NBT_KEY)) {
	      FluidStack fl = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbtRoot.getTag(FluidHandlerItemStack.FLUID_NBT_KEY));
	      ret.setFluid(fl);
	    } else {
	      ret.setFluid(null);
	    }
	    return ret;
	}
	  
	public static void saveTank(ItemStack stack, FluidTank tank) {
	    if (!stack.hasTagCompound()) {
	      stack.setTagCompound(new NBTTagCompound());
	    }
	    saveTank(stack.getTagCompound(), tank);
	}
	
	public static void saveTank(NBTTagCompound nbtRoot, FluidTank tank) {
		if(tank.getFluidAmount() > 0) {
			NBTTagCompound fluidRoot = new NBTTagCompound();
			tank.getFluid().writeToNBT(fluidRoot);
			nbtRoot.setTag(FluidHandlerItemStack.FLUID_NBT_KEY, fluidRoot);
		} else {
			nbtRoot.removeTag(FluidHandlerItemStack.FLUID_NBT_KEY);
		}
	}
	
	@Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new CapabilityProvider(stack);
    }
	
	private class CapabilityProvider implements IFluidHandler, ICapabilityProvider {
	    protected final ItemStack container;

	    private CapabilityProvider(ItemStack container) {
	      this.container = container;
	    }

	    @Override
	    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
	      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	    }

	    @SuppressWarnings("unchecked")
	    @Override
	    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
	      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
	    }

	    @Override
	    public IFluidTankProperties[] getTankProperties() {
	      FluidTank tank = loadTank(container);
		  if(tank == null)return new IFluidTankProperties[0];
	      return loadTank(container).getTankProperties();
	    }

	    @Override
	    public int fill(FluidStack resource, boolean doFill) {
	      if (container.stackSize != 1) {
	        return 0;
	      }
	      FluidTank tank = loadTank(container);
	      if(tank == null)return 0;
	      
	      boolean infi = container.getMetadata() == TankType.CREATIVE.getMeta();
	      if(infi){
	    	  FluidStack resourceCreative = resource.copy();
	    	  resourceCreative.amount = tank.getCapacity();
	    	  tank.fill(resourceCreative, doFill);
		      saveTank(container, tank);
		      return 1;
	      }
	      
	      int ret = tank.fill(resource, doFill);
	      saveTank(container, tank);
	      return ret;
	    }

	    @Override
	    @Nullable
	    public FluidStack drain(FluidStack resource, boolean doDrain) {
	      if (container.stackSize != 1) {
	        return null;
	      }
	      FluidTank tank = loadTank(container);
	      if(tank == null)return null;
	      
	      boolean infi = container.getMetadata() == TankType.CREATIVE.getMeta();
	      if(infi){
	    	  return resource.copy();
	      }
	      FluidStack ret = tank.drain(resource, doDrain);
	      saveTank(container, tank);
	      return ret;
	    }

	    @Override
	    @Nullable
	    public FluidStack drain(int maxDrain, boolean doDrain) {
	      if (container.stackSize != 1) {
	        return null;
	      }
	      FluidTank tank = loadTank(container);
	      if(tank == null)return null;
	      
	      boolean infi = container.getMetadata() == TankType.CREATIVE.getMeta();
	      if(infi){
	    	  if(tank.getFluid() == null)return null;
	    	  FluidStack fluid = tank.getFluid().copy();
	    	  fluid.amount = maxDrain;
	    	  return fluid;
	      }
	      
	      FluidStack ret = tank.drain(maxDrain, doDrain);
	      saveTank(container, tank);
	      return ret;
	    }

	  }
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		super.addInformation(stack, playerIn, tooltip, advanced);
		FluidTank tank = loadTank(stack);
		if(tank !=null){
			FluidStack fluid = tank.getFluid();
			if(fluid != null)tooltip.add(fluid.getLocalizedName()+" "+fluid.amount+" mB / "+tank.getCapacity()+" mB");
		}
    }
	
}
