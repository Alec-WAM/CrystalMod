package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketTileMessage extends AbstractPacketThreadsafe {

	public int x;
	public int y;
	public int z;
	private String type;
	private NBTTagCompound data;
	
	public PacketTileMessage(){}
	
	public PacketTileMessage(BlockPos pos, String type){
		this(pos, type, new NBTTagCompound());
	}
	
    public PacketTileMessage(BlockPos pos, String type, NBTTagCompound data){
    	x = pos.getX();
    	y = pos.getY();
    	z = pos.getZ();
    	this.type = type;
    	this.data = data;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		type = buffer.readStringFromBuffer(100);
		try {
			data = buffer.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			data = new NBTTagCompound();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeString(type);
		buffer.writeNBTTagCompoundToBuffer(data);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		World world = CrystalMod.proxy.getClientPlayer() == null ? null : CrystalMod.proxy.getClientPlayer().worldObj;
		if(world == null){
			return;
		}
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile !=null){
			if(tile instanceof IMessageHandler)((IMessageHandler)tile).handleMessage(type, data, true);
			if(tile instanceof IPedistal){
				if(type.equalsIgnoreCase("StackSync")){
					IPedistal pedistal = (IPedistal)tile;
					if(data.hasNoTags()){
						pedistal.setStack(ItemStackTools.getEmptyStack());
					} else {
						pedistal.setStack(ItemStackTools.loadFromNBT(data));
					}
				}
			}
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		World world = netHandler.playerEntity.worldObj;
		if(world == null){
			return;
		}
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile !=null && tile instanceof IMessageHandler){
			((IMessageHandler)tile).handleMessage(type, data, false);
		}
	}

}
