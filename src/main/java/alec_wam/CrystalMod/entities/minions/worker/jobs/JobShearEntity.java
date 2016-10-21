package alec_wam.CrystalMod.entities.minions.worker.jobs;

import java.util.List;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IShearable;
import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteAnimalFarm;
import alec_wam.CrystalMod.util.ModLogger;

public class JobShearEntity extends WorkerJob {
	
	public EntityLivingBase animalToShear;
	
	public JobShearEntity(EntityLivingBase animal){
		animalToShear = animal;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.worldObj.isRemote) return false;
		if(this.animalToShear == null || this.animalToShear.isDead || !(this.animalToShear instanceof IShearable)) return true;
		if(worksite == null || !(worksite instanceof WorksiteAnimalFarm)) return true;
		WorksiteAnimalFarm aFarm = (WorksiteAnimalFarm)worksite;

		
		destroyTool(worker);
		aFarm.giveShears(worker);
		ItemStack held = worker.getHeldItemMainhand();
		if(held == null || !(held.getItem() instanceof ItemShears)){
			return true;
		}
		worker.getLookHelper().setLookPositionWithEntity(animalToShear, 10, 40);
		double d = worker.getDistanceToEntity(animalToShear);
		if(d <= 2.5D){
			IShearable shearable = (IShearable)this.animalToShear;
			BlockPos shearPos = new BlockPos(animalToShear);
			if(shearable.isShearable(held, worker.worldObj, shearPos)){
				worker.swingArm(EnumHand.MAIN_HAND);
	            
				int fourtune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held);
		  		if(fourtune < 2)fourtune += aFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : aFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : aFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_3) ? 3 : 0;
		  		
				List<ItemStack> stacks = shearable.onSheared(held, worker.worldObj, shearPos, fourtune);
				for(ItemStack item : stacks){
					if(item !=null){
						aFarm.addStackToInventory(item, RelativeSide.TOP);
					}
				}
				held.damageItem(1, worker);
				destroyTool(worker);
				animalToShear = null;
				return true;
			}
		} else {
			if(worker.getNavigator().noPath()){
				worker.getNavigator().tryMoveToEntityLiving(animalToShear, MinionConstants.SPEED_WALK);
			}
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
		if(!(job instanceof JobShearEntity)) return false;
		return animalToShear == ((JobShearEntity)job).animalToShear;
	}

}
