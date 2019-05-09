package alec_wam.CrystalMod.network.packet;

import java.util.UUID;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.util.ProfileUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

public class PacketRequestProfile extends AbstractPacket
{
	private UUID uuid;
    
    public PacketRequestProfile()
    {
    	uuid = null;
    }

    public PacketRequestProfile(UUID uuid)
    {
        this.uuid = uuid;
    }
    
	public static PacketRequestProfile decode(PacketBuffer buffer) {
		if(!buffer.readBoolean()) return new PacketRequestProfile();
		return new PacketRequestProfile(buffer.readUniqueId());
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		buffer.writeBoolean(uuid !=null);
		if(uuid !=null){
			buffer.writeUniqueId(uuid);
		}
	}

	@Override
	public void handleClient(EntityPlayer player) {
	}

	@Override
	public void handleServer(EntityPlayerMP player) {
		CrystalModNetwork.sendTo(player, new PacketResponseProfile(ProfileUtil.getProfileServer(uuid)));
	}
}
