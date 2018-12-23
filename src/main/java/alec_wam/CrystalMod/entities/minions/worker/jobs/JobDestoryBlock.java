package alec_wam.CrystalMod.entities.minions.worker.jobs;

import java.util.List;

import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBoundedInventory;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import alec_wam.CrystalMod.world.DropCapture;
import alec_wam.CrystalMod.world.DropCapture.CaptureContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class JobDestoryBlock extends WorkerJob {

	public static final BlockPos NULL_BLOCKPOS = new BlockPos(0, -1, 0);
	
	public BlockPos blockPos;
	private int delay = -1;
    private int maxDelay = 1;
    private int mod = 1;
    boolean captureDrops;
	
	public JobDestoryBlock(BlockPos pos, boolean captureDrops){
		this.blockPos = pos;
		this.captureDrops = captureDrops;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.getEntityWorld().isRemote) return false;
		if(blockPos == null || blockPos == BlockPos.ORIGIN) return true;
		if(captureDrops && !(worksite instanceof TileWorksiteBoundedInventory))return true;
		if(worker.getEntityWorld().isAirBlock(blockPos)){ 
			return true;
		}
		IBlockState state = worker.getEntityWorld().getBlockState(blockPos);
		worker.getLookHelper().setLookPosition(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 10, 40);
		double d = worker.getDistance(blockPos.getX() + 0.5, blockPos.down().getY() + 0.5, blockPos.getZ() + 0.5);
		if(d <= 1.5D){
			if (delay < 0)
		    {
				int str = 1;
				delay = ((int)Math.max(5.0F, (20.0F - (str) * 3.0F) * state.getBlockHardness(worker.getEntityWorld(), blockPos)));
		        maxDelay = delay;
		        mod = (delay / Math.max(1, Math.round(delay / 12.0F)));
		    }
			if (delay > 0)
			{
				if ((--delay > 0) && (delay % mod == 0))
				{
					int des = 10-(int)(1.0F * ((10*delay)/maxDelay));
					FakePlayerUtil.destroyBlockPartially(worker.getEntityWorld(), worker.getEntityId(), blockPos, des);
					worker.swingArm(EnumHand.MAIN_HAND);
				}
				if (delay == 0)
				{
					FakePlayerUtil.destroyBlockPartially(worker.getEntityWorld(), worker.getEntityId(), blockPos, -1);
					worker.swingArm(EnumHand.MAIN_HAND);
					
					if(captureDrops){
						final CaptureContext dropsCapturer = DropCapture.instance.start(blockPos);

						final List<EntityItem> drops;
						try {
							worker.getEntityWorld().destroyBlock(blockPos, true);
						} finally {
							drops = dropsCapturer.stop();
						}
						for(EntityItem item : drops){
							if(item !=null && item.getEntityItem() !=null){
								ItemStack copyStack = ItemStackTools.safeCopy(item.getEntityItem());
								((TileWorksiteBoundedInventory)worksite).addStackToInventoryNoDrop(copyStack, false, RelativeSide.FRONT, RelativeSide.TOP);
								item.setEntityItemStack(copyStack);
								if (ItemStackTools.isEmpty(copyStack)) {
									item.setDead();
								}
							}
						}
					} else {
						worker.getEntityWorld().destroyBlock(blockPos, true);
					}
					
					delay = -1;
					blockPos = BlockPos.ORIGIN;
					return true;
				}
			}
		} else {
			if(worker.getNavigator().noPath()){
				worker.getNavigator().tryMoveToXYZ(blockPos.getX() + 0.5, blockPos.down().getY() + 0.5, blockPos.getZ() + 0.5, MinionConstants.SPEED_WALK);
			}
		}
		
		return false;
	}

	@Override
	public boolean isSame(WorkerJob job) {
		if(!(job instanceof JobDestoryBlock)) return false;
		return blockPos == ((JobDestoryBlock)job).blockPos;
	}

}
