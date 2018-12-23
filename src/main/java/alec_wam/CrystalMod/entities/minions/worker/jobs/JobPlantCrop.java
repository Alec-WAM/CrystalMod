package alec_wam.CrystalMod.entities.minions.worker.jobs;

import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBoundedInventory;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class JobPlantCrop extends WorkerJob {

	public static final BlockPos NULL_BLOCKPOS = new BlockPos(0, -1, 0);
	
	public BlockPos dirtPos;
	
	public JobPlantCrop(BlockPos pos){
		this.dirtPos = pos;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.getEntityWorld().isRemote) return false;
		if(dirtPos == null || dirtPos == BlockPos.ORIGIN) return true;
		if(worksite == null || !(worksite instanceof TileWorksiteBoundedInventory)) return true;
		TileWorksiteBoundedInventory farm = (TileWorksiteBoundedInventory)worksite;
		ItemStack held = worker.getHeldItemMainhand();
		if(!worker.getEntityWorld().isAirBlock(dirtPos)){ 
			ModLogger.info("Plant Job: sending item into front/top ["+held.getDisplayName()+"]");
			farm.addStackToInventory(worker.getHeldItemMainhand(), RelativeSide.FRONT, RelativeSide.TOP);
			worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
			return true;
		}
		if(!ItemStackTools.isValid(held)){
			return false;
		}
		
		worker.getLookHelper().setLookPosition(dirtPos.getX() + 0.5, dirtPos.getY() - 1 + 0.5, dirtPos.getZ() + 0.5, 10, 40);
		double d = worker.getDistance(dirtPos.getX() + 0.5, dirtPos.down().getY() + 0.5, dirtPos.getZ() + 0.5);
		if(d <= 1.5D){
			if(FakePlayerUtil.rightClickBlock(worker.getEntityWorld(), dirtPos.down(), EnumFacing.UP, held)){
				if(!ItemStackTools.isValid(held)){
					held = ItemStackTools.getEmptyStack();
				}
				worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
				worker.swingArm(EnumHand.MAIN_HAND);
			}
			held = worker.getHeldItemMainhand();
			if(ItemStackTools.isValid(held)){
				ModLogger.info("Plant Job: 2 sending item into front/top ["+held.getDisplayName()+"]");
				farm.addStackToInventory(held, RelativeSide.FRONT, RelativeSide.TOP);
				worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
			}
			return true;
		} else {
			if(worker.getNavigator().noPath()){
				worker.getNavigator().tryMoveToXYZ(dirtPos.getX() + 0.5, dirtPos.down().getY() + 0.5, dirtPos.getZ() + 0.5, MinionConstants.SPEED_WALK);
			}
		}
		
		return false;
	}

	@Override
	public boolean isSame(WorkerJob job) {
		if(!(job instanceof JobPlantCrop)) return false;
		return dirtPos == ((JobPlantCrop)job).dirtPos;
	}

}
