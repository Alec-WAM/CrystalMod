package alec_wam.CrystalMod.capability;

import java.util.Arrays;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class ExtendedPlayerInventory extends ItemStackHandler implements IItemHandlerModifiable {

	public static final int INV_SIZE = 1;
	private boolean[] changed;
	
	public ExtendedPlayerInventory(){
		super(INV_SIZE);
	}
	
	@Override
	public void setSize(int size){
		if(size < INV_SIZE){
			size = INV_SIZE;
		}
		super.setSize(size);
	}
	
	@Override
	protected void onContentsChanged(int slot)
    {
		setChanged(slot, true);
    }
	
	public boolean isChanged(int slot) {
		if (changed == null) {
			changed = new boolean[this.getSlots()];
			Arrays.fill(changed, false);
		}
		return changed[slot];
	}

	public void setChanged(int slot, boolean change) {
		if (changed == null) {
			changed = new boolean[this.getSlots()];
			Arrays.fill(changed, false);
		}
		this.changed[slot] = change;
	}
	
}
