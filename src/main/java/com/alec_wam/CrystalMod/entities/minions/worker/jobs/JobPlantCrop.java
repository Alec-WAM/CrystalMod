package com.alec_wam.CrystalMod.entities.minions.worker.jobs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import com.alec_wam.CrystalMod.entities.minions.MinionConstants;
import com.alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import com.alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import com.alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import com.alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import com.alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteCropFarm;
import com.alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;

public class JobPlantCrop extends WorkerJob {

	public static final BlockPos NULL_BLOCKPOS = new BlockPos(0, -1, 0);
	
	public BlockPos dirtPos;
	
	public JobPlantCrop(BlockPos pos){
		this.dirtPos = pos;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.worldObj.isRemote) return false;
		if(dirtPos == null || dirtPos == BlockPos.ORIGIN) return true;
		if(worksite == null || !(worksite instanceof WorksiteCropFarm)) return true;
		WorksiteCropFarm cFarm = (WorksiteCropFarm)worksite;
		if(!worker.worldObj.isAirBlock(dirtPos)){ 
			return true;
		}
		ItemStack held = worker.getHeldItemMainhand();
		if(held == null){
			return false;
		}
		
		worker.getLookHelper().setLookPosition(dirtPos.getX() + 0.5, dirtPos.getY() - 1 + 0.5, dirtPos.getZ() + 0.5, 10, 40);
		double d = worker.getDistance(dirtPos.getX() + 0.5, dirtPos.down().getY() + 0.5, dirtPos.getZ() + 0.5);
		if(d <= 1.5D){
			if(FakePlayerUtil.rightClickBlock(worker.worldObj, dirtPos.down(), EnumFacing.UP, held)){
				if(held !=null && held.stackSize <=0){
					held = null;
				}
				worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
				worker.swingArm(EnumHand.MAIN_HAND);
			}
			dirtPos = BlockPos.ORIGIN;
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
