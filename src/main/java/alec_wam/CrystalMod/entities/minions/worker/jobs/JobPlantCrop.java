package alec_wam.CrystalMod.entities.minions.worker.jobs;

import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.PathFinderWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBoundedInventory;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
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
			ModLogger.info("Plant Job: sending item into bottom/front/top ["+held.getDisplayName()+"]");
			farm.addStackToInventory(worker.getHeldItemMainhand(), RelativeSide.BOTTOM, RelativeSide.FRONT, RelativeSide.TOP);
			worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
			return true;
		}
		if(!ItemStackTools.isValid(held)){
			return false;
		}
		
		worker.getLookHelper().setLookPosition(dirtPos.getX() + 0.5, dirtPos.getY() - 1 + 0.5, dirtPos.getZ() + 0.5, 10, 40);
		double d = worker.getDistance(dirtPos.getX() + 0.5, dirtPos.down().getY() + 0.5, dirtPos.getZ() + 0.5);
		if(d <= 1.5D){			
			AxisAlignedBB bb = new AxisAlignedBB(dirtPos).expand(0.1, 0, 0.1);
			if(worker.getEntityBoundingBox().intersectsWith(bb)){
				if(worker.getNavigator().noPath()){
					for(EnumFacing face : EnumFacing.HORIZONTALS){
						BlockPos otherPos = dirtPos.offset(face);
						if(worker.getEntityWorld().isAirBlock(otherPos)){	
							double offsetX = face.getFrontOffsetX() * 1.4;
							double offsetZ = face.getFrontOffsetZ() * 1.4;
							Path path = PathFinderWorker.findDetailedPath(worker, worker.getNavigator(), otherPos.getX() + offsetX, otherPos.getY(), otherPos.getZ() + offsetZ);
							worker.getNavigator().setPath(path, MinionConstants.SPEED_WALK);
						}
					}
				}
				return false;
			}
			if(FakePlayerUtil.rightClickBlock(worker.getEntityWorld(), dirtPos.down(), EnumFacing.UP, held)){
				if(!ItemStackTools.isValid(held)){
					held = ItemStackTools.getEmptyStack();
				}
				worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
				worker.swingArm(EnumHand.MAIN_HAND);
			}
			held = worker.getHeldItemMainhand();
			if(ItemStackTools.isValid(held)){
				ModLogger.info("Plant Job: 2 sending item into bottom/front/top ["+held.getDisplayName()+"]");
				farm.addStackToInventory(held, RelativeSide.BOTTOM, RelativeSide.FRONT, RelativeSide.TOP);
				worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
			}
			return true;
		} else {
			if(worker.getNavigator().noPath()){
				for(EnumFacing face : EnumFacing.HORIZONTALS){
					BlockPos otherPos = dirtPos.offset(face);
					if(worker.getEntityWorld().isAirBlock(otherPos)){	
						double offsetX = face.getFrontOffsetX() * 0.5;
						double offsetZ = face.getFrontOffsetZ() * 0.5;
						Path path = PathFinderWorker.findDetailedPath(worker, worker.getNavigator(), otherPos.getX() + offsetX, otherPos.getY(), otherPos.getZ() + offsetZ);
						worker.getNavigator().setPath(path, MinionConstants.SPEED_WALK);
					}
				}
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
