package alec_wam.CrystalMod.entities.minions.warrior;

import alec_wam.CrystalMod.entities.ai.AIBase;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

public class MinionAIEat extends AIBase<EntityMinionWarrior>{
	public int eatTime;	
	public MinionAIEat()
    {        
    }
    
	@Override
	public void onUpdateCommon(EntityMinionWarrior minion) {
		float maxHealth = minion.getMaxHealth();
		float health = minion.getHealth();
		
		if(health < maxHealth && !minion.isEating()){
			ItemStack food = minion.inventory.getStackInSlot(2);
			if(ItemStackTools.isValid(food)){
				if(food.getItem() instanceof ItemFood){
					ItemFood foodItem = (ItemFood)food.getItem();
					float healAmt = (float)foodItem.getHealAmount(food);
					if(healAmt + health <= maxHealth){
						minion.setEating(true);
						minion.setHeldItem(EnumHand.MAIN_HAND, food);
						minion.setActiveHand(EnumHand.MAIN_HAND);
					}
				}
			}
		}
	}

	@Override
	public void onUpdateClient(EntityMinionWarrior minion) {
	}

	@Override
	public void onUpdateServer(EntityMinionWarrior minion) {
		
	}

	@Override
	public void reset(EntityMinionWarrior minion) {
	}

	@Override
	public void writeToNBT(EntityMinionWarrior minion, NBTTagCompound nbt) {}

	@Override
	public void readFromNBT(EntityMinionWarrior minion, NBTTagCompound nbt) {}

}
