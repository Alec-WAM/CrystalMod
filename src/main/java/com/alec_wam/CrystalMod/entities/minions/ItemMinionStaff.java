package com.alec_wam.CrystalMod.entities.minions;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import com.alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.util.ItemUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMinionStaff extends Item {

	public ItemMinionStaff(){
		super();
		setFull3D();
		setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "minionStaff");
	}
	
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
		if (((entity instanceof EntityMinionBase)) && (!entity.isDead) && entity.worldObj !=null && !entity.worldObj.isRemote && ((EntityMinionBase)entity).isOwner(player))
		{
			EntityMinionBase minion = (EntityMinionBase)entity;
			MinionType type = MinionType.BASIC;
			if(minion instanceof EntityMinionWorker){
				type = MinionType.WORKER;
			} else if(minion instanceof EntityMinionWarrior){
				type = MinionType.WARRIOR;
			}
			
			ItemStack drop = ItemMinion.createMinion(type);
			minion.saveToItem(player, stack);
			minion.dropItem(drop, false, false);
			minion.setDead();
			return true;
		}
        return false;
    }
	
}
