package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class PacketSpawnParticle extends AbstractPacketThreadsafe {

	public EnumParticleTypes type;
	private NBTTagCompound data;
	
	public PacketSpawnParticle(){}
	
	public PacketSpawnParticle(EnumParticleTypes type, @Nonnull NBTTagCompound data){
    	this.type = type;
    	this.data = data;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		type = EnumParticleTypes.getParticleFromId(buffer.readInt());
		try {
			data = buffer.readCompoundTag();
		} catch (IOException e) {
			data = new NBTTagCompound();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(type.getParticleID());
		buffer.writeCompoundTag(data);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		handle(CrystalMod.proxy.getClientPlayer() == null ? null : CrystalMod.proxy.getClientPlayer().getEntityWorld(), true);
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		handle(netHandler.playerEntity.getEntityWorld(), false);
	}
	
	public void handle(World world, boolean client){
		if(world == null){
			return;
		}
		double x = data.getDouble("x");
		double y = data.getDouble("y");
		double z = data.getDouble("z");
		double sx = data.getDouble("mX");
		double sy = data.getDouble("mY");
		double sz = data.getDouble("mZ");
		world.spawnParticle(type, x, y, z, sx, sy, sz, 0);
	}

}
