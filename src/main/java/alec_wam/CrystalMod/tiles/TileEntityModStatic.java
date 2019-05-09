package alec_wam.CrystalMod.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityModStatic extends TileEntity {

	public TileEntityModStatic(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	@Override
	public final void read(NBTTagCompound root) {
		super.read(root);
		readCustomNBT(root);
	}

	@Override
	public final NBTTagCompound write(NBTTagCompound root) {
	    super.write(root);
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
