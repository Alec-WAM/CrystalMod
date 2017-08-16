package alec_wam.CrystalMod.tiles.machine.elevator;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;

public class PacketPlayerMove extends AbstractPacketThreadsafe {

	public double x;
	public double y;
	public double z;
	
	public PacketPlayerMove(){}
	
    public PacketPlayerMove(double x, double y, double z){
    	this.x = x;
    	this.y = y;
    	this.z = z;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeDouble(x);
		buffer.writeDouble(y);
		buffer.writeDouble(z);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		if(player == null){
			return;
		}
		player.setPositionAndUpdate(x, y, z);
		player.onGround = true;
		//CrystalModNetwork.sendToServer(new PacketPlayerMove(x, player.getEntityBoundingBox().minY, z));
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayer player = netHandler.playerEntity;
		if(player == null){
			return;
		}
		player.prevPosX = player.posX = x;
		player.prevPosY = player.posY = y;
		player.prevPosZ = player.posZ = z;
		player.setPosition(player.posX, player.posY, player.posZ);
	}

}
