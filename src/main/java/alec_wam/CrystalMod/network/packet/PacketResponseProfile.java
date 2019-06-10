package alec_wam.CrystalMod.network.packet;

import com.mojang.authlib.GameProfile;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.util.ProfileUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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
		CompoundNBT nbt = buffer.readCompoundTag();
		return new PacketResponseProfile(NBTUtil.readGameProfile(nbt));
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		CompoundNBT nbt = new CompoundNBT();
		NBTUtil.writeGameProfile(nbt, profileResonse);
		buffer.writeCompoundTag(nbt);
	}

	@Override
	public void handleClient(PlayerEntity player) {
		ProfileUtil.profileCache.put(profileResonse.getId(), profileResonse);		
	}

	@Override
	public void handleServer(ServerPlayerEntity player) {
	}
}
