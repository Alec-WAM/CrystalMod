package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;

public class PacketExtendedPlayer extends AbstractPacketThreadsafe {

	private NBTTagCompound nbt;
	
	public PacketExtendedPlayer(){}
	
	public PacketExtendedPlayer(NBTTagCompound nbt){
		this.nbt = nbt;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			nbt = ByteBufUtils.readNBTTagCompoundFromBuffer(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeNBTTagCompoundToBuffer(buf, nbt);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		if(player !=null){
			if(nbt !=null){
				String command = nbt.hasKey("Command") ? nbt.getString("Command") : "Error";
				if(command.equalsIgnoreCase("Flag")){
					
					ExtendedPlayer extPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
					if(extPlayer == null)return;
					if(nbt.hasKey("hasFlag")){
						boolean hasFlag = nbt.getBoolean("hasFlag");
						extPlayer.setHasFlag(hasFlag);
					}
					if(nbt.hasKey("FlagColor")){
						int flagColor = nbt.getInteger("FlagColor");
						extPlayer.setFlagColor(flagColor);
					}
				}
			}
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		
	}

}
