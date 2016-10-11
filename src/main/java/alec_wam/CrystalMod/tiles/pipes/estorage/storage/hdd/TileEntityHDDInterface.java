package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory.INetworkInventory;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.inventory.NetworkInventoryHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.item.InventoryWrapper;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityHDDInterface extends TileEntityInventory implements ITickable, IMessageHandler, INetworkItemProvider {

	public TileEntityHDDInterface() {
		super("InvName", 2);
	}

	private EStorageNetwork network;
	public int dumpIndex = -1;
	private int priority = 0;
	public int facing = EnumFacing.NORTH.ordinal();
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("DumpIndex", dumpIndex);
		nbt.setInteger("Priority", priority);
		nbt.setInteger("Facing", facing);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		dumpIndex = nbt.getInteger("DumpIndex");
		priority = nbt.getInteger("Priority");
		facing = nbt.getInteger("Facing");
		updateAfterLoad();
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index == 0 ? stack !=null && stack.getItem() instanceof ItemHDD : true;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		super.setInventorySlotContents(slot, itemstack);
		if(slot == 0){
			if(this.network !=null && getNetworkInventory() !=null){
				getNetworkInventory().updateItems(getNetwork(), -1);
			}
			if(this.worldObj !=null && this.getPos() !=null)BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}
	
	@Override
	public void update() {
		super.update();
		if(this.getWorld().isRemote){
			return;
		}
		
		ItemStack hddStack = getStackInSlot(0);
		ItemStack input = getStackInSlot(1);
		if(hddStack !=null && hddStack.getItem() instanceof ItemHDD){
			if(input !=null){
				if(ItemHDD.hasItem(hddStack, input)){
					int index = ItemHDD.getItemIndex(hddStack, input);
					if(index > -1){
						ItemStack stored = ItemHDD.getItem(hddStack, index);
						if(ItemUtil.canCombine(stored, input)){
							stored.stackSize+=input.stackSize;
							ItemHDD.setItem(hddStack, index, stored);
							if(this.network !=null && getNetworkInventory() !=null){
								getNetworkInventory().updateItems(getNetwork(), index);
							}
							setInventorySlotContents(1, null);
							this.worldObj.markChunkDirty( this.pos, this );
						}
					}
				}else{
					int index = ItemHDD.getEmptyIndex(hddStack);
					if(index > -1){
						ItemHDD.setItem(hddStack, index, input);
						if(this.network !=null && getNetworkInventory() !=null){
							getNetworkInventory().updateItems(getNetwork(), index);
						}
						setInventorySlotContents(1, null);
						this.worldObj.markChunkDirty( this.pos, this );
					}
				}
			}
			if(dumpIndex > -1){
				ISidedInventory inv = null;
				EnumFacing face = null;
				for(EnumFacing f : EnumFacing.VALUES){
					TileEntity tile = getWorld().getTileEntity(getPos().offset(f));
					if(tile == null || (tile instanceof TileEntityHDDInterface))continue;
					if(tile instanceof ISidedInventory) {
						inv = (ISidedInventory) tile;
						face = f;
						break;
				    } else if(tile instanceof IInventory) {
				    	inv = new InventoryWrapper((IInventory) tile);
				    	face = f;
						break;
				    }
				}
				if(inv !=null && face !=null && !(inv instanceof TileEntityHDDInterface)){
					ItemStack toExtract = ItemHDD.getItem(hddStack, dumpIndex);
					if(toExtract !=null){
						int inserted = ItemUtil.doInsertItem(inv, toExtract, face.getOpposite());
				        if(inserted > 0) {
				          toExtract.stackSize -= inserted;
				          if(toExtract.stackSize <= 0){
				        	  toExtract = null;
				        	  ItemHDD.setItem(hddStack, dumpIndex, null);
				        	  if(this.network !=null && getNetworkInventory() !=null){
			  					getNetworkInventory().updateItems(getNetwork(), dumpIndex);
				  			  }
				        	  dumpIndex=-1;
				          }else {
				        	  ItemHDD.setItem(hddStack, dumpIndex, toExtract);
				        	  if(this.network !=null && getNetworkInventory() !=null){
				  				getNetworkInventory().updateItems(getNetwork(), dumpIndex);
				  			  }
				          }
				          this.worldObj.markChunkDirty( this.pos, this );
				        }
					}
				}
			}
		}
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("DumpIndex")){
			dumpIndex = messageData.getInteger("DumpIndex");
		}
		if(messageId.equalsIgnoreCase("Priority")){
			setPriority(messageData.getInteger("Priority"));
		}
	}

	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}
	
	@Override
	public int getPriority(){
		return priority;
	}

	@Override
	public void setPriority(int i) {
		this.priority = i;
		if(getNetwork() !=null){
			getNetwork().updateInterfaces();
		}
	}

	@Override
	public INetworkInventory getNetworkInventory() {
		return new NetworkInventoryHDDInterface(this);
	}

	@Override
	public void onDisconnected() {}
}
