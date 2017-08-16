package alec_wam.CrystalMod.proxy;

import java.io.File;

import com.mojang.authlib.GameProfile;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.oxygen.CapabilityOxygen;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.material.ModCrops;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.crafting.ModCrafting;
import alec_wam.CrystalMod.enchantment.ModEnchantments;
import alec_wam.CrystalMod.entities.ModEntites;
import alec_wam.CrystalMod.entities.disguise.DisguiseHandler;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.handler.ArmorEventHandler;
import alec_wam.CrystalMod.handler.EventHandler;
import alec_wam.CrystalMod.handler.KeyHandler;
import alec_wam.CrystalMod.handler.MissingItemHandler;
import alec_wam.CrystalMod.integration.ModIntegration;
import alec_wam.CrystalMod.integration.minecraft.ModBanners;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.enchancements.ModEnhancements;
import alec_wam.CrystalMod.items.guide.GuidePages.LookupResult;
import alec_wam.CrystalMod.items.tools.bat.ModBats;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteChunkLoader;
import alec_wam.CrystalMod.tiles.pipes.PipeNetworkTickHandler;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil;
import alec_wam.CrystalMod.tiles.playercube.PlayerCubeChunkLoaderManager;
import alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import alec_wam.CrystalMod.util.FarmUtil;
import alec_wam.CrystalMod.world.CrystalModWorldGenerator;
import alec_wam.CrystalMod.world.DropCapture;
import alec_wam.CrystalMod.world.ModDimensions;
import alec_wam.CrystalMod.world.WorldTickHandler;
import alec_wam.CrystalMod.world.crystex.CrystexiumSpikeStructure;
import alec_wam.CrystalMod.world.crystex.MapGenCrystexiumSpike;
import alec_wam.CrystalMod.world.game.tag.TagManager;
import alec_wam.CrystalMod.world.structures.FusionTempleStructure;
import alec_wam.CrystalMod.world.structures.MapGenFusionTemple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

	public KeyHandler keyHandler;
	public static File modConfigDir;
	public Configuration mainConfig;
	
	public void preInit(FMLPreInitializationEvent e) {
		modConfigDir = e.getModConfigurationDirectory();
        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "crystalmod", "crystalmod.cfg"));
        readMainConfig();
        
        MinecraftForge.EVENT_BUS.register(new Config());
        MinecraftForge.EVENT_BUS.register(new MissingItemHandler());
        
        
        ModFluids.registerFluids();
		ModBlocks.init();
		ModItems.init();
		ModCrops.init();
		ModCrafting.preInit();
		ModEntites.init();
        ItemMobEssence.initDefaultMobs();
        ModBats.registerBats();
        ModBats.registerUpgrades();
        
        
        CapabilityManager.INSTANCE.register(ExtendedPlayer.class, new ExtendedPlayerProvider.Storage(), ExtendedPlayer.class);
        CapabilityCrystalEnergy.register();
        CapabilityOxygen.register();
        
        CrystalModWorldGenerator generator = CrystalModWorldGenerator.instance;
        GameRegistry.registerWorldGenerator(generator, 5);
        
        MapGenStructureIO.registerStructure(MapGenFusionTemple.Start.class, CrystalMod.resource("fusiontemplestart"));
        MapGenStructureIO.registerStructureComponent(FusionTempleStructure.class, CrystalMod.resource("fusiontemple"));
        MapGenStructureIO.registerStructure(MapGenCrystexiumSpike.Start.class, CrystalMod.resource("crystexiumspikestart"));
        MapGenStructureIO.registerStructureComponent(CrystexiumSpikeStructure.class, CrystalMod.resource("crystexiumspike"));
       
        MinecraftForge.EVENT_BUS.register(generator);
        
        MinecraftForge.EVENT_BUS.register(DropCapture.instance);
        
        MinecraftForge.EVENT_BUS.register(new DisguiseHandler());
        
        PlayerCubeChunkLoaderManager.init();
        ForgeChunkManager.setForcedChunkLoadingCallback(CrystalMod.instance, new WorksiteChunkLoader());
        
        ModIntegration.register();
        ModIntegration.preInit();
	}
	
	public void readMainConfig() {
        Configuration cfg = mainConfig;
        Config.init(cfg);
        Config.postInit();
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
    }
	
	public void init(FMLInitializationEvent event) {
		ModDimensions.register();
		AttachmentUtil.initAttachments();
		MinecraftForge.EVENT_BUS.register(PipeNetworkTickHandler.instance);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(WorldTickHandler.instance);
		new TagManager();
		MinecraftForge.EVENT_BUS.register(new ArmorEventHandler());
		
		ModEnhancements.init();
		ModCrafting.init();
		ModBanners.init();
		ModIntegration.init();
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		ModItems.pipeCover.initialize();
		if(ModFluids.fluidXpJuice == null) { //should have been registered by enderio
	      ModFluids.forgeRegisterXPJuice();      
	    }
		ModIntegration.postInit();
		ModEnchantments.init();
		ModEntites.postInit();
		ModCrafting.postInit();
		FarmUtil.addDefaultCrops();
	}

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    	return entityPlayer instanceof EntityPlayerMP ? ((EntityPlayerMP)entityPlayer).interactionManager.getBlockReachDistance() : 5;
    }

	public World getWorld(int dim) {
		if(!DimensionManager.isDimensionRegistered(dim)){
			return null;
		}
		return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dim);
	}

	public World getClientWorld() {
		return null;
	}

	public EntityPlayer getPlayerForUsername(String playerName) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
	}

	public boolean isOp(GameProfile profile) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(profile);
	}

	public boolean isShiftKeyDown() {
		return false;
	}
	
	public void setForcedGuidePage(LookupResult result){}

	public Object getForcedGuidePage() {
		return null;
	}
	
}
