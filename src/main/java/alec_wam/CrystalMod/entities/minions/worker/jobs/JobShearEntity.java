package alec_wam.CrystalMod.entities.minions.worker.jobs;

import java.util.List;

import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteAnimalFarm;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IShearable;

public class JobShearEntity extends WorkerJob {
	
	public EntityLivingBase animalToShear;
	
	public JobShearEntity(EntityLivingBase animal){
		animalToShear = animal;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.getEntityWorld().isRemote) return false;
		if(this.animalToShear == null || this.animalToShear.isDead || !(this.animalToShear instanceof IShearable)) return true;
		if(worksite == null || !(worksite instanceof WorksiteAnimalFarm)) return true;
		WorksiteAnimalFarm aFarm = (WorksiteAnimalFarm)worksite;
		destroyTool(worker);
		boolean foundShears = false;
		if(!WorksiteAnimalFarm.isShears(worker.getHeldItemMainhand())){
			if(WorksiteAnimalFarm.isShears(worker.getBackItem())){
				ItemStack held = worker.getHeldItemMainhand();
				if(ItemStackTools.isEmpty(held) || WorksiteAnimalFarm.isSword(held)){
					worker.switchItems();
				} else {
					ModLogger.info("Shear Job: sending non tool into front/top ["+held.getDisplayName()+"]");
					if(aFarm.addStackToInventoryNoDrop(held, false, RelativeSide.BOTTOM, RelativeSide.FRONT, RelativeSide.TOP)){
						worker.setHeldItem(EnumHand.MAIN_HAND, ItemStackTools.getEmptyStack());
						worker.switchItems();
					}
				}
			}
			foundShears = WorksiteAnimalFarm.isShears(worker.getHeldItemMainhand());
		}
		
		if(!foundShears)aFarm.giveShears(worker);
		ItemStack held = worker.getHeldItemMainhand();
		if(ItemStackTools.isEmpty(held) || !WorksiteAnimalFarm.isShears(held)){
			//TODO Create Missing Shears warning
			return true;
		}
		worker.getLookHelper().setLookPositionWithEntity(animalToShear, 10, 40);
		double d = worker.getDistanceToEntity(animalToShear);
		if(d <= 1.5D){
			IShearable shearable = (IShearable)this.animalToShear;
			BlockPos shearPos = new BlockPos(animalToShear);
			if(shearable.isShearable(held, worker.getEntityWorld(), shearPos)){
				worker.swingArm(EnumHand.MAIN_HAND);
	            
				int fourtune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held);
		  		if(fourtune < 2)fourtune += aFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : aFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : aFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_3) ? 3 : 0;
		  		
				List<ItemStack> stacks = shearable.onSheared(held, worker.getEntityWorld(), shearPos, fourtune);
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
		if(!ItemStackTools.isValid(tool)) return;
		boolean canDamage = tool.isItemStackDamageable() && tool.getItem().isDamageable();
		if(ItemStackTools.isEmpty(tool) || (canDamage && tool.getItemDamage() >= tool.getMaxDamage())) {
			worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
	    }
	}

	@Override
	public boolean isSame(WorkerJob job) {
		if(!(job instanceof JobShearEntity)) return false;
		return animalToShear == ((JobShearEntity)job).animalToShear;
	}

}
