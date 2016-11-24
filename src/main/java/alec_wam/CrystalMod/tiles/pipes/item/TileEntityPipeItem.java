package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import alec_wam.CrystalMod.tiles.pipes.AbstractPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.ContainerNormalPipe;
import alec_wam.CrystalMod.tiles.pipes.PipeUtil;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.item.filters.CameraFilterInventory;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;

public class TileEntityPipeItem extends TileEntityPipe implements IInventoryChangedListener {

	protected final EnumMap<EnumFacing, Boolean> selfFeed = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

	protected final EnumMap<EnumFacing, Boolean> roundRobin = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

	protected final EnumMap<EnumFacing, Integer> priority = new EnumMap<EnumFacing, Integer>(EnumFacing.class);
	
	protected final EnumMap<EnumFacing, InventoryBasic> filters = new EnumMap<EnumFacing, InventoryBasic>(EnumFacing.class);
	
	protected final EnumMap<EnumFacing, RedstoneMode> redstoneSettings = new EnumMap<EnumFacing, RedstoneMode>(EnumFacing.class);
	
	public InventoryBasic getFilter(EnumFacing dir){
		InventoryBasic inv = filters.get(dir);
		if(inv == null){
			inv = new InventoryBasic("filter."+dir.getName().toLowerCase(), false, 1);
			inv.addInventoryChangeListener(this);
			this.filters.put(dir, inv);
		}
		return filters.get(dir);
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		for (Entry<EnumFacing, Boolean> entry : selfFeed.entrySet()) {
		      if(entry.getValue() != null) {
		    	  nbt.setBoolean("selfFeed." + entry.getKey().name(), entry.getValue());
		      }
		    }

		    for (Entry<EnumFacing, Boolean> entry : roundRobin.entrySet()) {
		      if(entry.getValue() != null) {
		    	  nbt.setBoolean("roundRobin." + entry.getKey().name(), entry.getValue());
		      }
		    }

		    for (Entry<EnumFacing, Integer> entry : priority.entrySet()) {
		      if(entry.getValue() != null) {
		    	  nbt.setInteger("priority." + entry.getKey().name(), entry.getValue());
		      }
		    }
		    for (Entry<EnumFacing, InventoryBasic> entry : filters.entrySet()) {
		    	NBTTagCompound nbtInv = new NBTTagCompound();
		    	ItemUtil.writeInventoryToNBT(entry.getValue(), nbtInv);
		    	nbt.setTag("filter."+entry.getKey().name(), nbtInv);
		    }
		    for (Entry<EnumFacing, RedstoneMode> entry : redstoneSettings.entrySet()) {
		      if(entry.getValue() != null) {
		    	  nbt.setInteger("redstone." + entry.getKey().name(), entry.getValue().ordinal());
		      }
		    }
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		for (EnumFacing dir : EnumFacing.VALUES) {
			String key = "selfFeed." + dir.name();
		      if(nbt.hasKey(key)) {
		        boolean val = nbt.getBoolean(key);
		        selfFeed.put(dir, val);
		      }

		      key = "roundRobin." + dir.name();
		      if(nbt.hasKey(key)) {
		        boolean val = nbt.getBoolean(key);
		        roundRobin.put(dir, val);
		      }

		      key = "priority." + dir.name();
		      if(nbt.hasKey(key)) {
		        int val = nbt.getInteger(key);
		        priority.put(dir, val);
		      }
		      key = "filter."+dir.name();
		      if(nbt.hasKey(key)) {
		    	  NBTTagCompound invNBT = nbt.getCompoundTag(key);
		    	  ItemUtil.readInventoryFromNBT(getFilter(dir), invNBT);
		      }
		      key = "redstone." + dir.name();
		      if(nbt.hasKey(key)) {
		        int val = nbt.getInteger(key);
		        redstoneSettings.put(dir, RedstoneMode.values()[val]);
		      }
		}
		
	}
	
	public void scanInventory(EnumFacing dir){
		if(dir == null)return;
		ItemStack filter = this.getFilter(dir).getStackInSlot(0);
		if(!ItemStackTools.isNullStack(filter)){
			if(filter.getMetadata() == FilterType.CAMERA.ordinal()){
				if(this.network instanceof ItemPipeNetwork){
					ItemPipeNetwork net = (ItemPipeNetwork)network;
					NetworkedInventory inv = net.getInventory(this, dir);
					IItemHandler sInv = inv.getInventory();
					if(sInv !=null){
						CameraFilterInventory filterInv = new CameraFilterInventory(filter, "");
						filterInv.clear();
						int slots = sInv.getSlots();
						if(slots > 0){
							for(int slot = 0; slot < slots; slot++){
								ItemStack stack = sInv.getStackInSlot(slot);
								if(!ItemStackTools.isNullStack(stack)){
									filterInv.addItem(stack);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public boolean passesFilter(ItemStack item, EnumFacing dir){
		if(dir == null)return false;
		ItemStack filter = getFilter(dir).getStackInSlot(0);
	    if(!ItemStackTools.isNullStack(filter)){
	    	return ItemUtil.passesFilter(item, filter);
	    }
	    return true;
	}
	
	@Override
	public IPipeType getPipeType() {
		return ItemPipeType.INSTANCE;
	}

	@Override
	public AbstractPipeNetwork createNetwork() {
		return new ItemPipeNetwork();
	}
	
	public int getMaximumExtracted(EnumFacing dir) {
	    return 4;
	}

	public float getTickTimePerItem(EnumFacing dir) {
		float maxExtract = 10f / getMaximumExtracted(dir);
		return maxExtract;
	}

	public void itemsExtracted(int numExtracted, int slot) {
	}
	
	public boolean isSelfFeedEnabled(EnumFacing dir) {
	    Boolean val = selfFeed.get(dir);
	    if(val == null) {
	      return false;
	    }
	    return val;
	  }

	  public void setSelfFeedEnabled(EnumFacing dir, boolean enabled) {
	    if(!enabled) {
	      selfFeed.remove(dir);
	    } else {
	      selfFeed.put(dir, enabled);
	    }
	    if(network != null && network instanceof ItemPipeNetwork) {
	      ((ItemPipeNetwork)network).routesChanged();
	    }
	  }

	  public boolean isRoundRobinEnabled(EnumFacing dir) {
	    Boolean val = roundRobin.get(dir);
	    if(val == null) {
	      return false;
	    }
	    return val;
	  }

	  public void setRoundRobinEnabled(EnumFacing dir, boolean enabled) {
	    if(!enabled) {
	      roundRobin.remove(dir);
	    } else {
	      roundRobin.put(dir, enabled);
	    }
	    if(network != null && network instanceof ItemPipeNetwork) {
	      ((ItemPipeNetwork)network).routesChanged();
	    }
	  }

	  public int getOutputPriority(EnumFacing dir) {
	    Integer res = priority.get(dir);
	    if(res == null) {
	      return 0;
	    }
	    return res.intValue();
	  }

	  public void setOutputPriority(EnumFacing dir, int priority) {
	    if(priority == 0) {
	      this.priority.remove(dir);
	    } else {
	      this.priority.put(dir, priority);
	    }
	    if(network != null && network instanceof ItemPipeNetwork) {
	      ((ItemPipeNetwork)network).routesChanged();
	    }
	  }
	  

	  public void setRedstoneMode(RedstoneMode mode, EnumFacing dir) {
		  redstoneSettings.put(dir, mode);
	  }
	  
	  public boolean isRedstoneModeMet(EnumFacing pipDir) {
			RedstoneMode res = redstoneSettings.get(pipDir);
		    if(res == null) {
		      res = RedstoneMode.ON;
		    }
		    return res.passes(getWorld(), getPos());
	  }
	  
	  public RedstoneMode getNextRedstoneMode(EnumFacing dir) {
		  RedstoneMode res = redstoneSettings.get(dir);
		  if(res == null) {
		      res = RedstoneMode.ON;
		  }
		  return RedstoneMode.getNextRedstoneMode(res);
	  }
	
	public boolean canConnectToExternal(EnumFacing direction, boolean ignoreDisabled) {
		return getExternalInventory(direction) != null;
	}

	public boolean onActivated(EntityPlayer player, EnumFacing side, EnumHand hand) {
		 if(super.onActivated(player, side, hand))return true;
	    return false;
	  }
	
	
	@Override
	  public void externalConnectionAdded(EnumFacing direction) {
	    super.externalConnectionAdded(direction);
	    if(network != null && network instanceof ItemPipeNetwork) {
	      BlockPos p = getPos().offset(direction);
	      ((ItemPipeNetwork)network).inventoryAdded(this, direction, p.getX(), p.getY(), p.getZ(), getExternalInventory(direction));
	    }
	  }

	  public IItemHandler getExternalInventory(EnumFacing direction) {
	    World world = getWorld();
	    if(world == null) {
	      return null;
	    }
	    BlockPos loc = getPos().offset(direction);
	    TileEntity te = world.getTileEntity(loc);
	    if(te !=null &&  PipeUtil.getPipe(getWorld(), loc, null) == null) {
	      return ItemUtil.getItemHandler(te, direction.getOpposite());
	    }
	    return null;
	  }

	  @Override
	  public void externalConnectionRemoved(EnumFacing direction) {
	    externalConnections.remove(direction);
	    connectionsChanged();
	    if(network != null && network instanceof ItemPipeNetwork) {
	      BlockPos p = getPos().offset(direction);
	      ((ItemPipeNetwork)network).inventoryRemoved(this, p.getX(), p.getY(), p.getZ());
	    }
	  }

	  @Override
	  public void setConnectionMode(EnumFacing dir, ConnectionMode mode) {
	    ConnectionMode oldVal = conectionModes.get(dir);
	    if(oldVal == mode) {
	      return;
	    }
	    super.setConnectionMode(dir, mode);
	    if(network != null && network instanceof ItemPipeNetwork) {
	    	((ItemPipeNetwork)network).routesChanged();
	    }
	  }

	@Override
	public void onInventoryChanged(InventoryBasic p_76316_1_) {
		if(network != null && network instanceof ItemPipeNetwork) {
		      ((ItemPipeNetwork)network).routesChanged();
	    }
		this.markDirty();
	}
	
	public List<ItemStack> getDrops(){
		List<ItemStack> list = super.getDrops();
		for(EnumFacing dir : EnumFacing.VALUES){
			IInventory inv = getFilter(dir);
			for(int s = 0 ; s < inv.getSizeInventory(); s++){
				ItemStack stack = inv.getStackInSlot(s);
				if(!ItemStackTools.isNullStack(stack)){
					list.add(stack);
				}
			}
  	  }
		return list;
	}
	
	public Object getContainer(int iD, EntityPlayer player) {
		return new ContainerNormalPipe();
	}
	
	@SideOnly(Side.CLIENT)
	public Object getGui(int iD, EntityPlayer player) {
		return new GuiItemPipe(null, null, null);
	}

	
}
