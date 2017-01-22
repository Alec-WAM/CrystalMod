package alec_wam.CrystalMod.tiles;

import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityMod extends TileEntity implements ITickable {

	public boolean dirtyClient = false;
	public boolean dirtyServer = false;
	
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
	
	public void updateAfterLoad(){
		if(this.getWorld() !=null){
			if(getWorld().isRemote)this.dirtyClient = true;
			else{
				//dirtyServer = true;
				//markDirty();
			}
		}
	}

	@Override
	public void update() {
		if(getWorld() != null){
			if (getWorld().isRemote && dirtyClient) {
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
				dirtyClient = false;
			}
			if (!getWorld().isRemote && dirtyServer) {
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
				markDirty();
				dirtyServer = false;
			}
		}
	}
	
	/**
	 * Call this with an interval (in ticks) to find out if the current tick is the one you want to do some work. This is staggered so the work of different TEs
	 * is stretched out over time.
	 *
	 * @see #shouldDoWorkThisTick(int, int) If you need to offset work ticks
	 */
	protected boolean shouldDoWorkThisTick(int interval) {
		return shouldDoWorkThisTick(interval, 0);
	}

	/**
	 * Call this with an interval (in ticks) to find out if the current tick is the one you want to do some work. This is staggered so the work of different TEs
	 * is stretched out over time.
	 *
	 * If you have different work items in your TE, use this variant to stagger your work.
	 */
	protected boolean shouldDoWorkThisTick(int interval, int offset) {
		return (getWorld().getTotalWorldTime() + checkOffset + offset) % interval == 0;
	}
	private final int checkOffset = (int) (Math.random() * 20);
}
