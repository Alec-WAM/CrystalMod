package alec_wam.CrystalMod.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityModStatic extends TileEntity {
	@Override
	public final void readFromNBT(NBTTagCompound root) {
		super.readFromNBT(root);
		readCustomNBT(root);
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound root) {
	    super.writeToNBT(root);
	    writeCustomNBT(root);
	    return root;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
	    NBTTagCompound tag = super.getUpdateTag();
	    writeCustomNBT(tag);
	    return tag;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		writeCustomNBT(nbttagcompound);
	    return new SPacketUpdateTileEntity(getPos(), 0, nbttagcompound);
	}

	@Override
    public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
        readCustomNBT(tag);
    }
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readCustomNBT(pkt.getNbtCompound());
	}
	
	public void writeCustomNBT(NBTTagCompound cmp) {
		// NO-OP
	}

	public void readCustomNBT(NBTTagCompound cmp) {
		// NO-OP
	}
}
