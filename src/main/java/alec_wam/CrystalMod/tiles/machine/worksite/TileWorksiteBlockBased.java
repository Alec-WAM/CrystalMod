package alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public abstract class TileWorksiteBlockBased extends TileWorksiteBoundedInventory {

	private List<BlockPos> blocksToUpdate = new ArrayList<BlockPos>();

	protected abstract void fillBlocksToProcess(Collection<BlockPos> addTo);

	protected abstract void scanBlockPosition(BlockPos pos);

	protected abstract void updateBlockWorksite();

	@Override
	protected void updateWorksite() {
		getWorld().theProfiler.startSection("Incremental Scan");
		incrementalScan();
		getWorld().theProfiler.endSection();
		updateBlockWorksite();
	}

	protected void clearBlocksToUpdate() {
		blocksToUpdate.clear();
	}

	protected void incrementalScan() {
		if (blocksToUpdate.isEmpty()) {
			fillBlocksToProcess(blocksToUpdate);
		}
		if (!blocksToUpdate.isEmpty()) {
			int rand = getWorld().rand.nextInt(blocksToUpdate.size());
			BlockPos pos = blocksToUpdate.remove(rand);
			scanBlockPosition(pos);
		}
	}

}
