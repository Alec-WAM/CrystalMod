package alec_wam.CrystalMod.tiles;

import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityMod extends TileEntity implements ITickableTileEntity {

	public TileEntityMod(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	public boolean dirtyClient = false;
	
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
	
	public void updateAfterLoad(){
		if(this.getWorld() !=null){
			if(getWorld().isRemote)this.dirtyClient = true;
		}
	}

	@Override
	public void tick() {
		if(getWorld() != null){
			if (getWorld().isRemote && dirtyClient) {
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
				dirtyClient = false;
			}
		}
	}
	
	boolean warned = false;
	@Override
	public void warnInvalidBlock() {
		super.warnInvalidBlock();
		if(!warned) {
			TileEntityType<?> type = this.getType();
			BlockState state = world.getBlockState(getPos());
			if(!type.isValidBlock(state.getBlock())){
				System.out.println("Not valid" + state + " for " + type.getRegistryName());
			}
			warned = true;
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
		return (getWorld().getGameTime() + checkOffset + offset) % interval == 0;
	}
	private final int checkOffset = (int) (Math.random() * 20);
}
