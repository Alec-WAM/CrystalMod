package alec_wam.CrystalMod.entities;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.entities.accessories.boats.EntityBoatBanner;
import alec_wam.CrystalMod.entities.accessories.boats.EntityBoatChest;
import alec_wam.CrystalMod.entities.accessories.boats.RenderBoatBanner;
import alec_wam.CrystalMod.entities.accessories.boats.RenderEntityBoatChest;
import alec_wam.CrystalMod.entities.animals.EntityCrystalCow;
import alec_wam.CrystalMod.entities.animals.EntityTamedPolarBear;
import alec_wam.CrystalMod.entities.animals.RenderCrystalCow;
import alec_wam.CrystalMod.entities.animals.RenderTamedPolarBear;
import alec_wam.CrystalMod.entities.balloon.EntityBalloon;
import alec_wam.CrystalMod.entities.balloon.RenderEntityBalloon;
import alec_wam.CrystalMod.entities.boatflume.EntityFlumeBoat;
import alec_wam.CrystalMod.entities.boatflume.RenderFlumeBoat;
import alec_wam.CrystalMod.entities.explosives.grenade.EntityGrenade;
import alec_wam.CrystalMod.entities.explosives.grenade.EntityNormalGrenade;
import alec_wam.CrystalMod.entities.explosives.grenade.RenderEntityGrenade;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartBase;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartBlue;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartDark;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartDarkIron;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartGreen;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartPure;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartRed;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityEnderChestMinecart;
import alec_wam.CrystalMod.entities.minecarts.chests.RenderEnderChestMinecart;
import alec_wam.CrystalMod.entities.minecarts.chests.RenderMinecartCrystalChest;
import alec_wam.CrystalMod.entities.minecarts.chests.wireless.EntityWirelessChestMinecart;
import alec_wam.CrystalMod.entities.minecarts.chests.wireless.RenderWirelessChestMinecart;
import alec_wam.CrystalMod.entities.minions.EntityMinionBase;
import alec_wam.CrystalMod.entities.minions.RenderMinionBase;
import alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.misc.EntityBambooBoat;
import alec_wam.CrystalMod.entities.misc.EntityCrystalModPainting;
import alec_wam.CrystalMod.entities.misc.EntityCustomFallingBlock;
import alec_wam.CrystalMod.entities.misc.RenderCrystalModPainting;
import alec_wam.CrystalMod.entities.misc.RenderCustomFallingBlock;
import alec_wam.CrystalMod.entities.misc.RenderEntityBambooBoat;
import alec_wam.CrystalMod.entities.mob.angel.EntityAngel;
import alec_wam.CrystalMod.entities.mob.angel.RenderAngel;
import alec_wam.CrystalMod.entities.mob.devil.EntityDevil;
import alec_wam.CrystalMod.entities.mob.devil.RenderDevil;
import alec_wam.CrystalMod.entities.mob.enderman.EntityCrystalEnderman;
import alec_wam.CrystalMod.entities.mob.enderman.RenderCrystalEnderman;
import alec_wam.CrystalMod.entities.mob.zombiePigmen.EntityCrystalPigZombie;
import alec_wam.CrystalMod.entities.mob.zombiePigmen.RenderCrystalPigZombie;
import alec_wam.CrystalMod.entities.pet.bombomb.EntityBombomb;
import alec_wam.CrystalMod.entities.pet.bombomb.RenderEntityBombomb;
import alec_wam.CrystalMod.items.tools.blowdart.EntityDart;
import alec_wam.CrystalMod.items.tools.blowdart.RenderEntityDart;
import alec_wam.CrystalMod.items.tools.grapple.EntityGrapplingHook;
import alec_wam.CrystalMod.items.tools.grapple.RenderEntityGrapplingHook;
import alec_wam.CrystalMod.items.tools.projectiles.EntityDagger;
import alec_wam.CrystalMod.items.tools.projectiles.EntityDarkarang;
import alec_wam.CrystalMod.items.tools.projectiles.RenderDagger;
import alec_wam.CrystalMod.items.tools.projectiles.RenderDarkarang;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEntites {
	
	public static final ResourceLocation LOOTTABLE_ANGEL = LootTableList.register(CrystalMod.resourceL("entities/angel"));
	public static final ResourceLocation LOOTTABLE_DEVIL = LootTableList.register(CrystalMod.resourceL("entities/devil"));

	public static void init(){
		ResourceLocation pigzombie = addEntity(EntityCrystalPigZombie.class, "crystalpigzombie");
		EntityRegistry.registerEgg(pigzombie, /*PINK*/ 15373203, 0x6CE5F8);
		EntitySpawnPlacementRegistry.setPlacementType(EntityCrystalPigZombie.class, SpawnPlacementType.ON_GROUND);
		
		ResourceLocation cow = addEntity(EntityCrystalCow.class, "crystalcow");
		EntityRegistry.registerEgg(cow, /*BROWN*/ 4470310, 0x6CE5F8);
		EntitySpawnPlacementRegistry.setPlacementType(EntityCrystalCow.class, SpawnPlacementType.ON_GROUND);
		
		ResourceLocation enderman = addEntity(EntityCrystalEnderman.class, "crystalenderman");
		EntityRegistry.registerEgg(enderman, /*BLACK*/ 0, 0x6CE5F8);
		EntitySpawnPlacementRegistry.setPlacementType(EntityCrystalEnderman.class, SpawnPlacementType.ON_GROUND);
		
		addEntity(EntityMinionWorker.class, "minionworker");
		
		addEntity(EntityMinionWarrior.class, "minionwarrior");
		
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartBlue.class, CrystalChestType.BLUE);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartRed.class, CrystalChestType.RED);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartGreen.class, CrystalChestType.GREEN);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartDark.class, CrystalChestType.DARK);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartPure.class, CrystalChestType.PURE);
		EntityCrystalChestMinecartBase.register(EntityCrystalChestMinecartDarkIron.class, CrystalChestType.DARKIRON);

		for(Entry<CrystalChestType, Class<? extends EntityCrystalChestMinecartBase>> entry : EntityCrystalChestMinecartBase.minecarts.entrySet()){
			addEntity(entry.getValue(), "minecart_chest_"+entry.getKey().name().toLowerCase(), 64, 1, true);
		}
		addEntity(EntityBombomb.class, "bombomb");
		
		addEntity(EntityWirelessChestMinecart.class, "minecart_wirelesschest", 64, 1, true);
		addEntity(EntityEnderChestMinecart.class, "minecart_enderchest", 64, 1, true);
		addEntity(EntityGrapplingHook.class, "grapplinghook", 900, 1, true);
		
		addEntity(EntityCustomFallingBlock.class, "customfallingblock", 160, 20, true);
		
		ResourceLocation angel = addEntity(EntityAngel.class, "pureangel");
		EntityRegistry.registerEgg(angel, 0xFFFFFF, 0xFFFF00);
		EntitySpawnPlacementRegistry.setPlacementType(EntityAngel.class, SpawnPlacementType.IN_AIR);
		
		ResourceLocation devil = addEntity(EntityDevil.class, "darkdevil");
		EntityRegistry.registerEgg(devil, 0, 0xFFFF00);
		EntitySpawnPlacementRegistry.setPlacementType(EntityDevil.class, SpawnPlacementType.IN_AIR);
		
		addEntity(EntityDarkarang.class, "darkarang", 160, 20, true);
		addEntity(EntityDagger.class, "dagger", 160, 20, true);
		
		addEntity(EntityTamedPolarBear.class, "polarbear_tamed");
		

		addEntity(EntityBambooBoat.class, "bambooboat", 80, 3, true);
		addEntity(EntityDart.class, "dart", 64, 24, false);
		addEntity(EntityCrystalModPainting.class, "crystalmodpainting", 160, Integer.MAX_VALUE, false);
		addEntity(EntityFlumeBoat.class, "flumeboat", 80, 3, true);
		addEntity(EntityBoatChest.class, "chestboat", 64, Integer.MAX_VALUE, false);
		addEntity(EntityBoatBanner.class, "chestbanner", 64, Integer.MAX_VALUE, false);
		addEntity(EntityNormalGrenade.class, "grenade_normal", 64, 10, true);
		addEntity(EntityBalloon.class, "baloon", 64, Integer.MAX_VALUE, true);
	}
	
	public static void postInit(){
		addToBiomes(EntityCrystalPigZombie.class, 50, 1, 4, EnumCreatureType.MONSTER, getBiomesThatCanSpawn(EntityPigZombie.class, EnumCreatureType.MONSTER));
		addToBiomes(EntityCrystalCow.class, 6, 1, 4, EnumCreatureType.CREATURE, getBiomesThatCanSpawn(EntityCow.class, EnumCreatureType.CREATURE));
		addToBiomes(EntityCrystalEnderman.class, 8, 1, 4, EnumCreatureType.MONSTER, getBiomesThatCanSpawn(EntityEnderman.class, EnumCreatureType.MONSTER));
		
		List<Biome> angelBiomeList = getBiomesThatCanSpawn(EntityEnderman.class, EnumCreatureType.MONSTER);
		Biome hell = Biome.REGISTRY.getObject(new ResourceLocation("hell"));
		Biome sky = Biome.REGISTRY.getObject(new ResourceLocation("sky"));
		
		if(sky !=null){
			angelBiomeList.remove(sky);
		}
		
		if(hell !=null){
			angelBiomeList.remove(hell);
			List<Biome> listHell = Lists.newArrayList(hell);
			addToBiomes(EntityAngel.class, 50, 4, 4, EnumCreatureType.MONSTER, listHell);
			addToBiomes(EntityDevil.class, 50, 4, 4, EnumCreatureType.MONSTER, listHell);
		}
		
		addToBiomes(EntityAngel.class, 8, 1, 4, EnumCreatureType.MONSTER, angelBiomeList);
		addToBiomes(EntityDevil.class, 8, 1, 4, EnumCreatureType.MONSTER, angelBiomeList);
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient(){
		RenderingRegistry.registerEntityRenderingHandler(EntityCrystalPigZombie.class, RenderCrystalPigZombie.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalCow.class, RenderCrystalCow.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalEnderman.class, RenderCrystalEnderman.FACTORY);

        RenderingRegistry.registerEntityRenderingHandler(EntityMinionBase.class, RenderMinionBase.FACTORY);
        
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalChestMinecartBase.class, RenderMinecartCrystalChest.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityWirelessChestMinecart.class, RenderWirelessChestMinecart.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityEnderChestMinecart.class, RenderEnderChestMinecart.FACTORY);
        
        RenderingRegistry.registerEntityRenderingHandler(EntityBombomb.class, RenderEntityBombomb.FACTORY);
        
        RenderingRegistry.registerEntityRenderingHandler(EntityGrapplingHook.class, new IRenderFactory<EntityGrapplingHook>() {
			@Override
			public Render<EntityGrapplingHook> createRenderFor(RenderManager manager) {
				return new RenderEntityGrapplingHook<EntityGrapplingHook>(manager, Minecraft.getMinecraft().getRenderItem());
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityCustomFallingBlock.class, new IRenderFactory<EntityCustomFallingBlock>() {
			@Override
			public Render<EntityCustomFallingBlock> createRenderFor(RenderManager manager) {
				return new RenderCustomFallingBlock(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityAngel.class, new IRenderFactory<EntityAngel>() {
			@Override
			public Render<EntityAngel> createRenderFor(RenderManager manager) {
				return new RenderAngel(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityDevil.class, new IRenderFactory<EntityDevil>() {
			@Override
			public Render<EntityDevil> createRenderFor(RenderManager manager) {
				return new RenderDevil(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityDarkarang.class, new IRenderFactory<EntityDarkarang>() {
			@Override
			public Render<EntityDarkarang> createRenderFor(RenderManager manager) {
				return new RenderDarkarang(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityDagger.class, new IRenderFactory<EntityDagger>() {
			@Override
			public Render<EntityDagger> createRenderFor(RenderManager manager) {
				return new RenderDagger(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityTamedPolarBear.class, new IRenderFactory<EntityTamedPolarBear>() {
			@Override
			public Render<EntityTamedPolarBear> createRenderFor(RenderManager manager) {
				return new RenderTamedPolarBear(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityBambooBoat.class, new IRenderFactory<EntityBambooBoat>() {
			@Override
			public RenderEntityBambooBoat createRenderFor(RenderManager manager) {
				return new RenderEntityBambooBoat(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityDart.class, new IRenderFactory<EntityDart>() {
			@Override
			public RenderEntityDart createRenderFor(RenderManager manager) {
				return new RenderEntityDart(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalModPainting.class, new IRenderFactory<EntityCrystalModPainting>() {
			@Override
			public RenderCrystalModPainting createRenderFor(RenderManager manager) {
				return new RenderCrystalModPainting(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityFlumeBoat.class, new IRenderFactory<EntityFlumeBoat>() {
			@Override
			public RenderFlumeBoat createRenderFor(RenderManager manager) {
				return new RenderFlumeBoat(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityBoatChest.class, new IRenderFactory<EntityBoatChest>() {
			@Override
			public RenderEntityBoatChest createRenderFor(RenderManager manager) {
				return new RenderEntityBoatChest(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityBoatBanner.class, new IRenderFactory<EntityBoatBanner>() {
			@Override
			public RenderBoatBanner createRenderFor(RenderManager manager) {
				return new RenderBoatBanner(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new IRenderFactory<EntityGrenade>() {
			@Override
			public RenderEntityGrenade createRenderFor(RenderManager manager) {
				return new RenderEntityGrenade(manager);
			}
		});
        
        RenderingRegistry.registerEntityRenderingHandler(EntityBalloon.class, RenderEntityBalloon.FACTORY);
	}
	
	public static int nextEntityId = 1;
	
	public static int getNextID(){
		return nextEntityId++;
	}
	
	public static ResourceLocation addEntity(Class<? extends Entity> entclass, String name){
		return addEntity(entclass, name, 32, 1, true);
	}
	
	public static ResourceLocation addEntity(Class<? extends Entity> entclass, String name, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates){
		ResourceLocation res = CrystalMod.resourceL(name);
		EntityRegistry.registerModEntity(res, entclass, res.toString(), getNextID(), CrystalMod.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
		return res;
	}

	public static EntityEggInfo registerEntityEgg(String id, Class<? extends Entity> entity,
			int primaryColor, int secondaryColor) {
		ResourceLocation res = CrystalMod.resourceL(id);
		EntityEggInfo info = new EntityEggInfo(res, primaryColor, secondaryColor);
		EntityList.ENTITY_EGGS.put(res, info);
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
