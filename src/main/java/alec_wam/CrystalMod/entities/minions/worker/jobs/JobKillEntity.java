package alec_wam.CrystalMod.entities.minions.worker.jobs;

import java.util.Map;

import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteAnimalFarm;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;

public class JobKillEntity extends WorkerJob {
	
	public EntityLivingBase animalToKill;
	
	public JobKillEntity(EntityLivingBase animal){
		animalToKill = animal;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.getEntityWorld().isRemote) return false;
		if(this.animalToKill == null || this.animalToKill.isDead) return true;
		if(worksite == null || !(worksite instanceof WorksiteAnimalFarm)) return true;
		WorksiteAnimalFarm aFarm = (WorksiteAnimalFarm)worksite;

		
		destroyTool(worker);
		aFarm.giveSword(worker);
		ItemStack held = worker.getHeldItemMainhand();
		if(held == null){
			return true;
		}
		EntityPlayer player = FakePlayerUtil.getPlayer((WorldServer)worker.getEntityWorld());
		worker.getLookHelper().setLookPositionWithEntity(animalToKill, 10, 40);
		double d = worker.getDistanceToEntity(animalToKill);
		if(d <= 2.5D){
			if(!animalToKill.isDead && animalToKill.deathTime <= 0 && animalToKill.hurtResistantTime == 0){
				int fortune = aFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : aFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : aFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_3) ? 3 : 0;
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
				if (held != null)
                {
                    player.getAttributeMap().applyAttributeModifiers(held.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
                }
				animalToKill.captureDrops = true;
				
				worker.swingArm(EnumHand.MAIN_HAND);
				final Map<Enchantment, Integer> oEnchants = EnchantmentHelper.getEnchantments(player.getHeldItemMainhand());
				int oLooting = oEnchants == null || !oEnchants.containsKey(Enchantments.LOOTING) ? 0 : oEnchants.get(Enchantments.LOOTING);
				int looting = oLooting + fortune;
				
				if(fortune > 0)ItemUtil.addEnchantment(player.getHeldItemMainhand(), Enchantments.LOOTING, looting);
				player.attackTargetEntityWithCurrentItem(animalToKill);
				if(fortune > 0)EnchantmentHelper.setEnchantments(oEnchants, player.getHeldItemMainhand());
				
				worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, player.getHeldItemMainhand());
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
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
		if(!ItemStackTools.isValid(tool)) return;
		boolean canDamage = tool.isItemStackDamageable() && tool.getItem().isDamageable();
		if(ItemStackTools.isEmpty(tool) || (canDamage && tool.getItemDamage() >= tool.getMaxDamage())) {
			worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
	    }
	}

	@Override
	public boolean isSame(WorkerJob job) {
		if(!(job instanceof JobKillEntity)) return false;
		return animalToKill == ((JobKillEntity)job).animalToKill;
	}

}
