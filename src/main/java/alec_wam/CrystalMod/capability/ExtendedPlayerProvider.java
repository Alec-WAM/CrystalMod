package alec_wam.CrystalMod.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ExtendedPlayerProvider implements ICapabilitySerializable<NBTTagCompound> {

	public static final ResourceLocation KEY = new ResourceLocation("crystalmod", "extendedplayer");

	private ExtendedPlayer extendedPlayer;

    public ExtendedPlayerProvider(EntityPlayer player) {
        this.extendedPlayer = Capabilities.EXTENDED_PLAYER != null ? Capabilities.EXTENDED_PLAYER.getDefaultInstance().setPlayer(player) : null;
    }
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return Capabilities.EXTENDED_PLAYER != null && capability == Capabilities.EXTENDED_PLAYER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return hasCapability(capability, facing) ? (T) extendedPlayer : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) Capabilities.EXTENDED_PLAYER.getStorage().writeNBT(Capabilities.EXTENDED_PLAYER, extendedPlayer, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		Capabilities.EXTENDED_PLAYER.getStorage().readNBT(Capabilities.EXTENDED_PLAYER, extendedPlayer, null, nbt);
	}
	
	public static class Storage implements Capability.IStorage<ExtendedPlayer> {
        @Override
        public NBTBase writeNBT(Capability<ExtendedPlayer> capability, ExtendedPlayer instance, EnumFacing side) {
            return instance != null ? instance.writeToNBT() : null;
        }

        @Override
        public void readNBT(Capability<ExtendedPlayer> capability, ExtendedPlayer instance, EnumFacing side, NBTBase nbt) {
            if(instance != null && (nbt instanceof NBTTagCompound)) {
            	NBTTagCompound nbtTag = (NBTTagCompound) nbt;
                instance.readFromNBT(nbtTag);
                instance.needsSync = true;
            }
        }
    }

    public static ExtendedPlayer getExtendedPlayer(EntityPlayer player) {
        return player.hasCapability(Capabilities.EXTENDED_PLAYER, null) ? player.getCapability(Capabilities.EXTENDED_PLAYER, null) : null;
    }
	
}
