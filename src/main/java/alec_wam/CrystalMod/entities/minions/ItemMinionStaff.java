package alec_wam.CrystalMod.entities.minions;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.pet.bombomb.EntityBombomb;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMinionStaff extends Item {

	public ItemMinionStaff(){
		super();
		setFull3D();
		setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "minionstaff");
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
		if ((!entity.isDead) && entity.getEntityWorld() !=null)
		{
			if(((entity instanceof EntityMinionBase)) && ((EntityMinionBase)entity).isOwner(player)){
				EntityMinionBase minion = (EntityMinionBase)entity;
				MinionType type = MinionType.BASIC;
				if(minion instanceof EntityMinionWorker){
					type = MinionType.WORKER;
				} else if(minion instanceof EntityMinionWarrior){
					type = MinionType.WARRIOR;
				}
				if(!entity.getEntityWorld().isRemote){
					ItemStack drop = ItemMinion.createMinion(type);
					minion.saveToItem(player, drop);
					minion.dropItem(drop, false, false);
					minion.setDead();
				}
				return true;
			}
			if(((entity instanceof EntityBombomb)) && ((EntityBombomb)entity).isOwner(player)){
				EntityBombomb bombomb = (EntityBombomb)entity;
				
				if(!entity.getEntityWorld().isRemote){
					ItemStack drop = new ItemStack(ModItems.bombomb);
					bombomb.saveToItem(player, drop);
					bombomb.entityDropItem(drop, 0.0f);
					bombomb.setDead();
				}
				return true;
			}
		}
        return false;
    }
	
}
