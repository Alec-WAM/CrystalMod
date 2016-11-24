package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import alec_wam.CrystalMod.api.estorage.INetworkInventory;
import alec_wam.CrystalMod.api.estorage.INetworkItemProvider;
import alec_wam.CrystalMod.api.estorage.INetworkInventory.EnumUpdateType;
import alec_wam.CrystalMod.api.estorage.storage.IItemProvider;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.item.InventoryWrapper;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
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
		return index == 0 ? !ItemStackTools.isNullStack(stack) && stack.getItem() instanceof IItemProvider : true;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		super.setInventorySlotContents(slot, itemstack);
		if(slot == 0){
			if(this.network !=null){
				network.getItemStorage().invalidate();
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
		if(!ItemStackTools.isNullStack(hddStack) && hddStack.getItem() instanceof ItemHDD){
			if(!ItemStackTools.isNullStack(input)){
				if(ItemHDD.hasItem(hddStack, input)){
					int index = ItemHDD.getItemIndex(hddStack, input);
					if(index > -1){
						ItemStack stored = ItemHDD.getItem(hddStack, index);
						if(ItemUtil.canCombine(stored, input)){
							ItemStackTools.incStackSize(stored, ItemStackTools.getStackSize(input));
							ItemHDD.setItem(hddStack, index, stored);
							if(this.network !=null && getNetworkInventory() !=null){
								network.getItemStorage().invalidate();
							}
							setInventorySlotContents(1, ItemStackTools.getEmptyStack());
							this.worldObj.markChunkDirty( this.pos, this );
						}
					}
				}else{
					int index = ItemHDD.getEmptyIndex(hddStack);
					if(index > -1){
						ItemHDD.setItem(hddStack, index, input);
						if(this.network !=null){
							network.getItemStorage().invalidate();
						}
						setInventorySlotContents(1, ItemStackTools.getEmptyStack());
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
					if(!ItemStackTools.isNullStack(toExtract)){
						int inserted = ItemUtil.doInsertItem(inv, toExtract, face.getOpposite());
				        if(inserted > 0) {
				          ItemStackTools.incStackSize(toExtract, -inserted);
				          if(ItemStackTools.isEmpty(toExtract)){
				        	  toExtract = ItemStackTools.getEmptyStack();
				        	  ItemHDD.setItem(hddStack, dumpIndex, toExtract);
				        	  if(this.network !=null && getNetworkInventory() !=null){
		  							network.getItemStorage().invalidate();
				  			  }
				        	  dumpIndex=-1;
				          }else {
				        	  ItemHDD.setItem(hddStack, dumpIndex, toExtract);
				        	  if(this.network !=null && getNetworkInventory() !=null){
				  				network.getItemStorage().invalidate();
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
