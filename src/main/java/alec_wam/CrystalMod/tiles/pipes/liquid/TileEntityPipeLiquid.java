package alec_wam.CrystalMod.tiles.pipes.liquid;

import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.IPipeWrapper;
import alec_wam.CrystalMod.tiles.pipes.PipeUtil;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.item.ItemPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TileEntityPipeLiquid extends TileEntityPipe implements IInventoryChangedListener, IMessageHandler {
	
	private final EnumMap<EnumFacing, FluidFilter> outputFilters = new EnumMap<EnumFacing, FluidFilter>(EnumFacing.class);
	private final EnumMap<EnumFacing, FluidFilter> inputFilters = new EnumMap<EnumFacing, FluidFilter>(EnumFacing.class);
	
	public TileEntityPipeLiquid(){}
	
	public FluidFilter getFilter(EnumFacing dir, boolean isInput) {
	    if(isInput) {
	      return inputFilters.get(dir);
	    }
	    return outputFilters.get(dir);
	}

	public void setFilter(EnumFacing dir, FluidFilter filter, boolean isInput) {
	    if(isInput) {
	      inputFilters.put(dir, filter);
	    } else {
	      outputFilters.put(dir, filter);
	    }
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		for(EnumFacing face : EnumFacing.VALUES){
			FluidFilter outFilter = outputFilters.get(face);
			if(outFilter !=null){
				NBTTagCompound filterNBT = new NBTTagCompound();
				outFilter.writeToNBT(filterNBT);
				nbt.setTag("filter.out."+face, filterNBT);
			}
			FluidFilter inFilter = inputFilters.get(face);
			if(inFilter !=null){
				NBTTagCompound filterNBT = new NBTTagCompound();
				inFilter.writeToNBT(filterNBT);
				nbt.setTag("filter.in."+face, filterNBT);
			}
		}
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		for(EnumFacing face : EnumFacing.VALUES){
		  String key = "filter.out."+face.name();
	      if(nbt.hasKey(key)) {
	    	  FluidFilter out = new FluidFilter();
    	      out.readFromNBT(nbt.getCompoundTag(key));
    	      outputFilters.put(face, out);
	      }
	      key = "filter.in."+face.name();
	      if(nbt.hasKey(key)) {
	    	  FluidFilter in = new FluidFilter();
	    	  in.readFromNBT(nbt.getCompoundTag(key));
    	      inputFilters.put(face, in);
	      }
		}
	}
	
	@Override
	public IPipeType getPipeType() {
		return LiquidPipeType.INSTANCE;
	}

	private int ticksSinceFailedExtract;
	
	@Override
	public AbstractPipeNetwork createNetwork() {
		return new LiquidPipeNetwork();
	}
	
	public boolean canConnectToExternal(EnumFacing direction, boolean ignoreDisabled) {
		World world = getWorld();
	    if(world == null) {
	      return false;
	    }
	    BlockPos loc = getPos().offset(direction);
	    TileEntity te = world.getTileEntity(loc);
	    if(te !=null && te instanceof IPipeWrapper){
	    	return true;
	    }
		return getExternalHandler(direction) !=null;
	  }

	  public TileEntity getExternalHandler(EnumFacing direction) {
	    World world = getWorld();
	    if(world == null) {
	      return null;
	    }
	    BlockPos loc = getPos().offset(direction);
	    TileEntity te = world.getTileEntity(loc);
	    if(te !=null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()) && PipeUtil.getPipe(world, loc, null) == null) {
	      return te;
	    }
	    return null;
	  }
	  
	  public static TileEntity getExternalFluidHandler(IBlockAccess world, BlockPos bc, EnumFacing facing) {
		  if(world == null) {
		      return null;
		  }
		  BlockPos loc = bc;
		  TileEntity te = world.getTileEntity(loc);
		  if(te !=null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()) && PipeUtil.getPipe(world, loc, null) == null) {
			  return te;
		  }
		  return null;
	  }
	  

	  @Override
	  public boolean setNetwork(AbstractPipeNetwork network) {
	    if(network == null) {
	      this.network = null;
	      return true;
	    }
	    if(!(network instanceof LiquidPipeNetwork)) {
	      return false;
	    }
	    this.network = (LiquidPipeNetwork) network;
	    for (EnumFacing dir : externalConnections) {
	      if(this.network instanceof LiquidPipeNetwork)
	      ((LiquidPipeNetwork)this.network).connectionChanged(this, dir);
	    }

	    return true;
	  }
	  
	  @Override
	  public void setConnectionMode(EnumFacing dir, ConnectionMode mode) {
	    super.setConnectionMode(dir, mode);
	    refreshConnections(dir);
	  }

	  private void refreshConnections(EnumFacing dir) {
	    if(network == null) {
	      return;
	    }
	    if(this.network instanceof LiquidPipeNetwork)
	      if(getExternalHandler(dir) !=null)((LiquidPipeNetwork)this.network).connectionChanged(this, dir);
	  }

	  @Override
	  public void externalConnectionAdded(EnumFacing fromDirection) {
	    super.externalConnectionAdded(fromDirection);
	    refreshConnections(fromDirection);
	  }

	  @Override
	  public void externalConnectionRemoved(EnumFacing fromDirection) {
	  	externalConnections.remove(fromDirection);
	  	connectionsChanged();
	    refreshConnections(fromDirection);
	  }

	  @Override
	  public void update() {
		  super.update();
	    if(getWorld().isRemote) {
	      return;
	    }
	    doExtract();
	  }

	  public boolean matchedFilter(FluidStack drained, EnumFacing conDir, boolean isInput) {
	    if(drained == null || conDir == null) {
	      return false;
	    }
	    FluidFilter filter = getFilter(conDir, isInput);
	    if(filter == null || filter.isEmpty()) {
	      return true;
	    }
	    return filter.matchesFilter(drained);
	  }
	  
	  private void doExtract() {    
	    if(!(hasConnectionMode(ConnectionMode.INPUT) || hasConnectionMode(ConnectionMode.IN_OUT))) {
	      return;
	    }
	    if(network == null) {
	      return;
	    }

	    // assume failure, reset to 0 if we do extract
	    ticksSinceFailedExtract++;
	    if(ticksSinceFailedExtract > 25 && ticksSinceFailedExtract % 10 != 0) {
	      // after 25 ticks of failing, only check every 10 ticks
	      return;
	    }

	    for (EnumFacing dir : externalConnections) {
	      if(getConnectionMode(dir).acceptsInput()) {
	        if(this.network instanceof LiquidPipeNetwork && ((LiquidPipeNetwork)network).extractFrom(this, dir)) {
	          ticksSinceFailedExtract = 0;
	        }
	      }
	    }

	  }
	  
	  @Override
	    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
	      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public <T> T getCapability(Capability<T> capability, final EnumFacing facing) {
	        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
	            //noinspection unchecked
	            return (T) new IFluidHandler() {
	            	
	            	public int fill(FluidStack resource, boolean doFill) {
	            		if(network == null || !getConnectionMode(facing).acceptsInput() || !(network instanceof LiquidPipeNetwork)) {
            		      return 0;
            		    }
            		    return ((LiquidPipeNetwork)network).fillFrom(TileEntityPipeLiquid.this, facing, resource, doFill);
	                }

	                public FluidStack drain(int maxEmpty, boolean doDrain) {
	                	return null;
	                }

	                public FluidStack drain(FluidStack resource, boolean doDrain) {
	                    return null;
	                }

					@Override
					public IFluidTankProperties[] getTankProperties() {
						return ((LiquidPipeNetwork)network).getTankInfo(TileEntityPipeLiquid.this, facing);
					}
	                
	            };
	        }
	        return super.getCapability(capability, facing);
	    }

		@Override
		public void onInventoryChanged(IInventory invBasic) {
			this.markDirty();
		}

		@Override
		public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
			ModLogger.info("Message");
			if(messageId.equalsIgnoreCase("FilterFluid")){
				EnumFacing dir = EnumFacing.getFront(messageData.getInteger("Dir"));
				FluidFilter filter = new FluidFilter();
				filter.readFromNBT(messageData.getCompoundTag("FilterData"));
				setFilter(dir, filter, messageData.getBoolean("isInput"));
			}
		}
}
