package alec_wam.CrystalMod.entities;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.entities.animals.EntityCrystalCow;
import alec_wam.CrystalMod.entities.animals.RenderCrystalCow;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartBase;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartBlue;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartDark;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartDarkIron;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartGreen;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartPure;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartRed;
import alec_wam.CrystalMod.entities.minecarts.chests.RenderMinecartCrystalChest;
import alec_wam.CrystalMod.entities.minions.EntityMinionBase;
import alec_wam.CrystalMod.entities.minions.RenderMinionBase;
import alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.mob.enderman.EntityCrystalEnderman;
import alec_wam.CrystalMod.entities.mob.enderman.RenderCrystalEnderman;
import alec_wam.CrystalMod.entities.mob.zombiePigmen.EntityCrystalPigZombie;
import alec_wam.CrystalMod.entities.mob.zombiePigmen.RenderCrystalPigZombie;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
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
		
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartBlue.class, CrystalChestType.BLUE);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartRed.class, CrystalChestType.RED);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartGreen.class, CrystalChestType.GREEN);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartDark.class, CrystalChestType.DARK);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartPure.class, CrystalChestType.PURE);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartDarkIron.class, CrystalChestType.DARKIRON);

		for(Entry<CrystalChestType, Class<? extends EntityCrystalChestMinecartBase>> entry : EntityCrystalChestMinecartBase.minecarts.entrySet()){
			addEntity(entry.getValue(), "minecart_chest_"+entry.getKey().name().toLowerCase(), 64, 1, true);
		}
	}
	
	public static void postInit(){
		addToBiomes(EntityCrystalPigZombie.class, 50, 1, 4, EnumCreatureType.MONSTER, getBiomesThatCanSpawn(EntityPigZombie.class, EnumCreatureType.MONSTER));
		addToBiomes(EntityCrystalCow.class, 6, 1, 4, EnumCreatureType.CREATURE, getBiomesThatCanSpawn(EntityCow.class, EnumCreatureType.CREATURE));
		addToBiomes(EntityCrystalEnderman.class, 8, 1, 4, EnumCreatureType.MONSTER, getBiomesThatCanSpawn(EntityEnderman.class, EnumCreatureType.MONSTER));
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient(){
		RenderingRegistry.registerEntityRenderingHandler(EntityCrystalPigZombie.class, RenderCrystalPigZombie.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalCow.class, RenderCrystalCow.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalEnderman.class, RenderCrystalEnderman.FACTORY);

        RenderingRegistry.registerEntityRenderingHandler(EntityMinionBase.class, RenderMinionBase.FACTORY);
        
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalChestMinecartBase.class, RenderMinecartCrystalChest.FACTORY);
	}
	
	public static int nextEntityId = 1;
	
	public static int getNextID(){
		return nextEntityId++;
	}
	
	public static void addEntity(Class<? extends Entity> entclass, String name){
		addEntity(entclass, name, 32, 1, true);
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
	
	public static boolean canTypeSpawnByDefault(Biome biome, Class <? extends EntityLiving> entityClass, EnumCreatureType type){
		List<Biome.SpawnListEntry> entryList = biome.getSpawnableList(type);
		if(entryList == null || entryList.size() < 1) return false;
		for(SpawnListEntry entry : entryList){
			if(entityClass.equals(entry.entityClass)){
				return true;
			}
		}
		return false;
	}
	
	public static List<Biome> getBiomesThatCanSpawn(Class <? extends EntityLiving> entityClass, EnumCreatureType type){
		List<Biome> list = Lists.newArrayList();
		Iterator<Biome> it = Biome.REGISTRY.iterator();
		while(it.hasNext()){
			Biome biome = it.next();
			if(biome !=null){
				if(canTypeSpawnByDefault(biome, entityClass, type)){
					list.add(biome);
				}
			}
		}
		return list;
	}
	
	public static void addToBiomes(Class <? extends EntityLiving > entityClass, int weightedProb, int min, int max, EnumCreatureType type, List<Biome> biomes){
		EntityRegistry.addSpawn(entityClass, weightedProb, min, max, type, biomes.toArray(new Biome[biomes.size()]));
	}
	
}
