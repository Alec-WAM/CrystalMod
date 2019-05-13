package alec_wam.CrystalMod.network.packet;

import com.mojang.authlib.GameProfile;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.util.ProfileUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;

public class PacketResponseProfile extends AbstractPacket
{
	private GameProfile profileResonse;
    
    public PacketResponseProfile()
    {
    	profileResonse = null;
    }

    public PacketResponseProfile(GameProfile uuid)
    {
        this.profileResonse = uuid;
    }
    
	public static PacketResponseProfile decode(PacketBuffer buffer) {
		NBTTagCompound nbt = buffer.readCompoundTag();
		return new PacketResponseProfile(NBTUtil.readGameProfile(nbt));
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTUtil.writeGameProfile(nbt, profileResonse);
		buffer.writeCompoundTag(nbt);
	}

	@Override
	public void handleClient(EntityPlayer player) {
		ProfileUtil.profileCache.put(profileResonse.getId(), profileResonse);		
	}

	@Override
	public void handleServer(EntityPlayerMP player) {
	}
}