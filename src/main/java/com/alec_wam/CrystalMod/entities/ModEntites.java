package com.alec_wam.CrystalMod.entities;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.entities.animals.EntityCrystalCow;
import com.alec_wam.CrystalMod.entities.animals.RenderCrystalCow;
import com.alec_wam.CrystalMod.entities.minions.EntityMinionBase;
import com.alec_wam.CrystalMod.entities.minions.RenderMinionBase;
import com.alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import com.alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import com.alec_wam.CrystalMod.entities.mob.enderman.EntityCrystalEnderman;
import com.alec_wam.CrystalMod.entities.mob.enderman.RenderCrystalEnderman;
import com.alec_wam.CrystalMod.entities.mob.zombiePigmen.EntityCrystalPigZombie;
import com.alec_wam.CrystalMod.entities.mob.zombiePigmen.RenderCrystalPigZombie;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEntites {

	public static void init(){
		addEntity(EntityCrystalPigZombie.class, "CrystalPigZombie");
		EntityRegistry.registerEgg(EntityCrystalPigZombie.class, /*PINK*/ 15373203, 0x6CE5F8);
		
		addEntity(EntityCrystalCow.class, "CrystalCow");
		EntityRegistry.registerEgg(EntityCrystalCow.class, /*BROWN*/ 4470310, 0x6CE5F8);
		
		addEntity(EntityCrystalEnderman.class, "CrystalEnderman");
		EntityRegistry.registerEgg(EntityCrystalEnderman.class, /*BLACK*/ 0, 0x6CE5F8);
		
		addEntity(EntityMinionWorker.class, "MinionWorker");
		
		addEntity(EntityMinionWarrior.class, "MinionWarrior");
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient(){
		RenderingRegistry.registerEntityRenderingHandler(EntityCrystalPigZombie.class, RenderCrystalPigZombie.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalCow.class, RenderCrystalCow.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalEnderman.class, RenderCrystalEnderman.FACTORY);

        RenderingRegistry.registerEntityRenderingHandler(EntityMinionBase.class, RenderMinionBase.FACTORY);
	}
	
	public static int nextEntityId = 1;
	
	public static int getNextID(){
		return nextEntityId++;
	}
	
	public static void addEntity(Class<? extends Entity> entclass, String name){
		addEntity(entclass, name, 32, 1, false);
	}
	
	public static void addEntity(Class<? extends Entity> entclass, String name, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates){
		EntityRegistry.registerModEntity(entclass, name, getNextID(), CrystalMod.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	public static EntityEggInfo registerEntityEgg(String id, Class<? extends Entity> entity,
			int primaryColor, int secondaryColor) {
		EntityEggInfo info = new EntityEggInfo(id, primaryColor, secondaryColor);
		EntityList.ENTITY_EGGS.put(id, info);
		return info;
	}
	
}
