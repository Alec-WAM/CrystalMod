package alec_wam.CrystalMod.tiles;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityModStatic extends TileEntity {

	public TileEntityModStatic(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	@Override
	public final void read(CompoundNBT root) {
		super.read(root);
		readCustomNBT(root);
	}

	@Override
	public final CompoundNBT write(CompoundNBT root) {
	    super.write(root);
	    writeCustomNBT(root);
	    return root;
	}

	@Override
	public CompoundNBT getUpdateTag() {
	    CompoundNBT tag = super.getUpdateTag();
	    writeCustomNBT(tag);
	    return tag;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbttagcompound = new CompoundNBT();
		writeCustomNBT(nbttagcompound);
	    return new SUpdateTileEntityPacket(getPos(), 0, nbttagcompound);
	}

	@Override
    public void handleUpdateTag(CompoundNBT tag) {
		super.handleUpdateTag(tag);
        readCustomNBT(tag);
    }
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readCustomNBT(pkt.getNbtCompound());
	}
	
	public void writeCustomNBT(CompoundNBT cmp) {
		// NO-OP
	}

	public void readCustomNBT(CompoundNBT cmp) {
		// NO-OP
	}
}
