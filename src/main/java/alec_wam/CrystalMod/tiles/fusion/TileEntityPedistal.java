package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.api.tile.IPedistal;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityPedistal extends TileEntityInventory implements IPedistal, IMessageHandler {

	public TileEntityPedistal() {
		super(ModBlocks.TILE_PEDISTAL, "Pedistal", 1);
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
	public EnumFacing getRotation() {
		return this.getBlockState().get(BlockPedistal.FACING);
	}
	
	@Override
	public void onItemChanged(int slot){
		if(slot == 0){
			syncStack();
		}
	}
	
	@Override
    public AxisAlignedBB getRenderBoundingBox() {
		return new net.minecraft.util.math.AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(1, 1, 1));
    }
	
	//TODO Handle Packet
	public void syncStack(){
		if(getWorld() !=null && !getWorld().isRemote && getPos() !=null){
			ItemStack stack = getStack();
			CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "StackSync", ItemStackTools.isEmpty(stack) ? new NBTTagCompound() : stack.serializeNBT()), this);
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("StackSync")){
			this.setStack(ItemStackTools.loadFromNBT(messageData));
		}
	}

}
