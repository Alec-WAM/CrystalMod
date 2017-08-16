package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TilePedistal extends TileEntityInventory implements IPedistal, IFacingTile {

	public EnumFacing facing = EnumFacing.UP;
	
	public TilePedistal() {
		super("Pedistal", 1);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", getFacing());
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("Facing"))setFacing(nbt.getInteger("Facing"));
		else {
			ModLogger.info("No Facing Loaded defaulting to Up");
			setFacing(1); //Up
		}
		updateAfterLoad();
	}
	
	@Override
	public void setFacing(int facing) {
		this.facing = EnumFacing.getFront(facing);
	}

	@Override
	public int getFacing() {
		return facing.getIndex();
	}

	@Override
	public ItemStack getStack() {
		return getStackInSlot(0);
	}
	
	@Override
	public void setStack(ItemStack stack){
		setInventorySlotContents(0, stack);
	}
	
	@Override
	public void onItemChanged(int slot){
		if(slot == 0){
			syncStack();
		}
	}
	
	public void syncStack(){
		if(getWorld() !=null && !getWorld().isRemote && getPos() !=null){
			ItemStack stack = getStack();
			CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "StackSync", ItemStackTools.isEmpty(stack) ? new NBTTagCompound() : stack.serializeNBT()), this);
		}
	}

	@Override
	public EnumFacing getRotation() {
		return facing;
	}

}
