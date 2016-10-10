package com.alec_wam.CrystalMod.entities.minions.worker.jobs;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;

import com.alec_wam.CrystalMod.entities.minions.MinionConstants;
import com.alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import com.alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import com.alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import com.alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import com.alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteAnimalFarm;
import com.alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteTreeFarm;
import com.alec_wam.CrystalMod.util.BlockUtil;
import com.alec_wam.CrystalMod.util.ModLogger;
import com.alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import com.alec_wam.CrystalMod.util.tool.ToolUtil;
import com.alec_wam.CrystalMod.util.tool.TreeUtil;
import com.alec_wam.CrystalMod.world.DropCapture;
import com.alec_wam.CrystalMod.world.DropCapture.CaptureContext;

public class JobKillEntity extends WorkerJob {
	
	public EntityLivingBase animalToKill;
	
	public JobKillEntity(EntityLivingBase animal){
		animalToKill = animal;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.worldObj.isRemote) return false;
		if(this.animalToKill == null || this.animalToKill.isDead) return true;
		if(worksite == null || !(worksite instanceof WorksiteAnimalFarm)) return true;
		WorksiteAnimalFarm aFarm = (WorksiteAnimalFarm)worksite;

		
		destroyTool(worker);
		aFarm.giveSword(worker);
		ItemStack held = worker.getHeldItemMainhand();
		if(held == null){
			return true;
		}
		EntityPlayer player = FakePlayerUtil.getPlayer((WorldServer)worker.worldObj);
		worker.getLookHelper().setLookPositionWithEntity(animalToKill, 10, 40);
		double d = worker.getDistanceToEntity(animalToKill);
		if(d <= 2.5D){
			if(!animalToKill.isDead && animalToKill.deathTime <= 0 && animalToKill.hurtResistantTime == 0){
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
				if (held != null)
                {
                    player.getAttributeMap().applyAttributeModifiers(held.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
                }
				animalToKill.captureDrops = true;
				
				worker.swingArm(EnumHand.MAIN_HAND);
				player.attackTargetEntityWithCurrentItem(animalToKill);
				
				worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, player.getHeldItemMainhand());
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
				if (held != null)
                {
                    player.getAttributeMap().removeAttributeModifiers(held.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
                }
				destroyTool(worker);
                
				if(!animalToKill.capturedDrops.isEmpty()){
					for(EntityItem item : animalToKill.capturedDrops){
						if(item.getEntityItem() !=null && !item.isDead){
							aFarm.addStackToInventory(item.getEntityItem(), RelativeSide.TOP);
							item.setDead();
						}
					}
				}
				if(animalToKill.getHealth() <=0.0F || animalToKill.isDead){
					animalToKill = null;
					return true;
				}
			}
		} else {
			//if(worker.getNavigator().noPath()){
				worker.getNavigator().tryMoveToEntityLiving(animalToKill, MinionConstants.SPEED_WALK);
			//}
		}
		
		return false;
	}
	
	public void destroyTool(EntityMinionWorker worker){
		ItemStack tool = worker.getHeldItemMainhand();
		if(tool == null) return;
		boolean canDamage = tool.isItemStackDamageable() && tool.getItem().isDamageable();
		if(tool.stackSize == 0 || (canDamage && tool.getItemDamage() >= tool.getMaxDamage())) {
			worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
	    }
	}

	@Override
	public boolean isSame(WorkerJob job) {
		if(!(job instanceof JobKillEntity)) return false;
		return animalToKill == ((JobKillEntity)job).animalToKill;
	}

}
