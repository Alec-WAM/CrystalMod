package com.alec_wam.CrystalMod.blocks;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.BlockCrystalPlant.PlantType;
import com.alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass;
import com.alec_wam.CrystalMod.blocks.rail.BlockReinforcedRail;
import com.alec_wam.CrystalMod.proxy.ClientProxy;
import com.alec_wam.CrystalMod.tiles.cauldron.BlockCrystalCauldron;
import com.alec_wam.CrystalMod.tiles.cauldron.TileEntityCrystalCauldron;
import com.alec_wam.CrystalMod.tiles.chest.BlockCrystalChest;
import com.alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import com.alec_wam.CrystalMod.tiles.chest.ItemBlockCrystalChest;
import com.alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine;
import com.alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import com.alec_wam.CrystalMod.tiles.machine.elevator.BlockElevator;
import com.alec_wam.CrystalMod.tiles.machine.elevator.TileEntityElevator;
import com.alec_wam.CrystalMod.tiles.machine.elevator.TileEntityElevatorRenderer;
import com.alec_wam.CrystalMod.tiles.machine.elevator.caller.BlockElevatorCaller;
import com.alec_wam.CrystalMod.tiles.machine.elevator.caller.TileEntityElevatorCaller;
import com.alec_wam.CrystalMod.tiles.machine.elevator.caller.TileEntityElevatorCallerRenderer;
import com.alec_wam.CrystalMod.tiles.machine.elevator.floor.BlockElevatorFloor;
import com.alec_wam.CrystalMod.tiles.machine.elevator.floor.TileEntityElevatorFloor;
import com.alec_wam.CrystalMod.tiles.machine.enderbuffer.BlockEnderBuffer;
import com.alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import com.alec_wam.CrystalMod.tiles.machine.inventory.charger.BlockInventoryCharger;
import com.alec_wam.CrystalMod.tiles.machine.inventory.charger.BlockStateInventoryCharger;
import com.alec_wam.CrystalMod.tiles.machine.inventory.charger.TileEntityInventoryChargerCU;
import com.alec_wam.CrystalMod.tiles.machine.inventory.charger.TileEntityInventoryChargerRF;
import com.alec_wam.CrystalMod.tiles.machine.mobGrinder.BlockMobGrinder;
import com.alec_wam.CrystalMod.tiles.machine.mobGrinder.TileEntityMobGrinder;
import com.alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery;
import com.alec_wam.CrystalMod.tiles.machine.power.battery.TileEntityBattery;
import com.alec_wam.CrystalMod.tiles.machine.power.battery.TileEntityBatteryRenderer;
import com.alec_wam.CrystalMod.tiles.machine.power.converter.BlockPowerConverter;
import com.alec_wam.CrystalMod.tiles.machine.power.converter.TileEnergyConverterCUtoRF;
import com.alec_wam.CrystalMod.tiles.machine.power.converter.TileEnergyConverterRFtoCU;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.BlockEngine;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.BlockEngine.EngineType;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.ItemBlockEngine;
import com.alec_wam.CrystalMod.tiles.machine.worksite.BlockWorksite;
import com.alec_wam.CrystalMod.tiles.machine.worksite.BlockWorksite.WorksiteType;
import com.alec_wam.CrystalMod.tiles.machine.worksite.ItemBlockWorksite;
import com.alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import com.alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteRenderer;
import com.alec_wam.CrystalMod.tiles.matter.BlockMatterCollector;
import com.alec_wam.CrystalMod.tiles.matter.TileEntityMatterCollector;
import com.alec_wam.CrystalMod.tiles.pipes.BlockPipe;
import com.alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import com.alec_wam.CrystalMod.tiles.pipes.ItemBlockPipe;
import com.alec_wam.CrystalMod.tiles.pipes.ModelPipe;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.BlockCrafter;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.BlockPatternEncoder;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileCrafter;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TilePatternEncoder;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileProcessingPatternEncoder;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.BlockPanel;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.BlockPanel.PanelType;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.ItemBlockPanel;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.display.TileEntityPanelItem;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.display.TileEntityPanelItemRenderer;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.BlockWirelessPanel;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.TileEntityWirelessPanel;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.external.BlockExternalInterface;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.external.TileEntityExternalInterface;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.BlockHDDInterface;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.TileEntityHDDInterface;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.TileEntityHDDInterfaceRenderer;
import com.alec_wam.CrystalMod.tiles.pipes.wireless.BlockWirelessPipeWrapper;
import com.alec_wam.CrystalMod.tiles.pipes.wireless.TileEntityPipeWrapper;
import com.alec_wam.CrystalMod.tiles.playercube.BlockPlayerCubeBlock;
import com.alec_wam.CrystalMod.tiles.playercube.BlockPlayerCubeCore;
import com.alec_wam.CrystalMod.tiles.playercube.BlockPlayerCubePortal;
import com.alec_wam.CrystalMod.tiles.playercube.TileEntityPlayerCubePortal;
import com.alec_wam.CrystalMod.tiles.playercube.TileEntityPlayerCubePortalRenderer;
import com.alec_wam.CrystalMod.tiles.spawner.BlockCustomSpawner;
import com.alec_wam.CrystalMod.tiles.spawner.RenderTileEntityCustomSpawner;
import com.alec_wam.CrystalMod.tiles.spawner.TileEntityCustomSpawner;
import com.alec_wam.CrystalMod.tiles.tank.BlockTank;
import com.alec_wam.CrystalMod.tiles.tank.ItemBlockTank;
import com.alec_wam.CrystalMod.tiles.tank.TileEntityTank;
import com.alec_wam.CrystalMod.tiles.weather.BlockWeather;
import com.alec_wam.CrystalMod.tiles.weather.TileEntityWeather;
import com.alec_wam.CrystalMod.tiles.workbench.BlockCrystalWorkbench;
import com.alec_wam.CrystalMod.tiles.workbench.TileEntityCrystalWorkbench;
import com.google.common.collect.Maps;

public class ModBlocks {

	public static final Map<String, Block> REGISTRY = Maps.newHashMap();
	
	public static BlockCrystal crystal;
	public static BlockCrystalOre crystalOre;
	public static BlockCrystalIngot crystalIngot;
	public static BlockCrystalGlass crystalGlass;
	public static BlockCrystalReed crystalReeds;
	public static BlockCrystalWorkbench crystalWorkbench;
	public static BlockCrystalChest crystalChest;
	public static BlockCrystalPlant crystalPlantBlue, crystalPlantRed, crystalPlantGreen, crystalPlantDark;
	public static BlockCrystalTreePlant crystalTreePlantBlue, crystalTreePlantRed, crystalTreePlantGreen, crystalTreePlantDark;
	public static BlockCrystalLog crystalLog;
	public static BlockCrystalLeaves crystalLeaves;
	public static BlockCrystalSapling crystalSapling;
	public static BlockPipe crystalPipe;
	public static BlockTank crystalTank;
	public static BlockEngine engine;
	public static BlockWeather weather;
	public static BlockCrystalCauldron cauldron;
	public static BlockCustomSpawner customSpawner;
	public static BlockMobGrinder mobGrinder;
	
	public static BlockHDDInterface hddInterface;
	public static BlockExternalInterface externalInterface;
	public static BlockPanel storagePanel;
	public static BlockWirelessPanel wirelessPanel;
	public static BlockWirelessPipeWrapper wirelessPipe;
	public static BlockCrafter crafter;
	public static BlockPatternEncoder encoder;
	
	public static BlockPlayerCubeBlock cubeBlock;
	public static BlockPlayerCubeCore cubeCore;
	public static BlockPlayerCubePortal cubePortal;
	
	public static BlockMatterCollector matterCollector;
	public static BlockBattery battery;
	public static BlockElevator elevator;
	public static BlockElevatorFloor elevatorFloor;
	public static BlockElevatorCaller elevatorCaller;
	public static BlockInventoryCharger invCharger;
	public static BlockCrystalMachine crystalMachine;
	public static BlockEnderBuffer enderBuffer;
	public static BlockWorksite worksite;
	
	public static BlockPowerConverter converter;
	
	public static BlockReinforcedRail darkIronRail;

	public static final EnumPlantType crystalPlantType = EnumPlantType.getPlantType("crystal");
	
	public static void init() {
		crystal = registerEnumBlock(new BlockCrystal(), "crystalblock");
		
		crystalOre = registerEnumBlock(new BlockCrystalOre(), "crystalore");
		
		crystalIngot = registerEnumBlock(new BlockCrystalIngot(), "crystalingotblock");
		
		crystalGlass = registerEnumBlock(new BlockCrystalGlass(), "crystalglass");
		
		crystalReeds = new BlockCrystalReed();
		registerBlock(crystalReeds, "crystalreedblock");
		
		crystalWorkbench = new BlockCrystalWorkbench();
		registerEnumBlock(crystalWorkbench, "crystalworkbench");
		regsiterTileEntity(TileEntityCrystalWorkbench.class);
		
		crystalChest = new BlockCrystalChest();
		registerBlock(crystalChest, new ItemBlockCrystalChest(crystalChest), "crystalchest");
		
		for (CrystalChestType typ : CrystalChestType.values())
        {
            GameRegistry.registerTileEntityWithAlternatives(typ.clazz, CrystalMod.MODID+"." + typ.name(), typ.name());
        }
		
		crystalPlantBlue = new BlockCrystalPlant(PlantType.BLUE);
		registerBlock(crystalPlantBlue, "bluecrystalplant");
		
		crystalPlantRed = new BlockCrystalPlant(PlantType.RED);
		registerBlock(crystalPlantRed, "redcrystalplant");
		
		crystalPlantGreen = new BlockCrystalPlant(PlantType.GREEN);
		registerBlock(crystalPlantGreen, "greencrystalplant");
		
		crystalPlantDark = new BlockCrystalPlant(PlantType.DARK);
		registerBlock(crystalPlantDark, "darkcrystalplant");
		
		crystalTreePlantBlue = new BlockCrystalTreePlant(PlantType.BLUE);
		registerBlock(crystalTreePlantBlue, "bluecrystaltreeplant");
		
		crystalTreePlantRed = new BlockCrystalTreePlant(PlantType.RED);
		registerBlock(crystalTreePlantRed, "redcrystaltreeplant");
		
		crystalTreePlantGreen = new BlockCrystalTreePlant(PlantType.GREEN);
		registerBlock(crystalTreePlantGreen, "greencrystaltreeplant");
		
		crystalTreePlantDark = new BlockCrystalTreePlant(PlantType.DARK);
		registerBlock(crystalTreePlantDark, "darkcrystaltreeplant");
		
		crystalLog = new BlockCrystalLog();
		registerBlock(crystalLog, new ItemBlockMeta(crystalLog), "crystallog");
		ItemBlockMeta.setMappingProperty(crystalLog, BlockCrystalLog.VARIANT);
		
		crystalLeaves = new BlockCrystalLeaves();
		registerBlock(crystalLeaves, new ItemBlockMeta(crystalLeaves){
			public int getMetadata(int m){
				return m | 4;
			}
		}, "crystalleaves");
		ItemBlockMeta.setMappingProperty(crystalLeaves, BlockCrystalLeaves.VARIANT);
		
		crystalSapling = new BlockCrystalSapling();
		registerBlock(crystalSapling, new ItemBlockMeta(crystalSapling), "crystalsapling");
		ItemBlockMeta.setMappingProperty(crystalSapling, BlockCrystalSapling.VARIANT);
		
		crystalPipe = new BlockPipe();
		registerBlock(crystalPipe, new ItemBlockPipe(crystalPipe), "crystalpipe");
		for(PipeType type : PipeType.values()){
			regsiterTileEntity(type.clazz);
		}
		
		crystalTank = new BlockTank();
		registerBlock(crystalTank, new ItemBlockTank(crystalTank), "crystaltank");
	    regsiterTileEntity(TileEntityTank.class);
		
		engine = new BlockEngine();
		registerBlock(engine, new ItemBlockEngine(engine), "engine");
		for(EngineType type : EngineType.values()){
			regsiterTileEntity(type.clazz);
		}
		
		weather = new BlockWeather();
		registerBlock(weather, "weather");
		regsiterTileEntity(TileEntityWeather.class);
		
		hddInterface = new BlockHDDInterface();
		registerBlock(hddInterface, "hddinterface");
		regsiterTileEntity(TileEntityHDDInterface.class);
		
		externalInterface = new BlockExternalInterface();
		registerBlock(externalInterface, "externalinterface");
		regsiterTileEntity(TileEntityExternalInterface.class);
		
		storagePanel = new BlockPanel();
		registerBlock(storagePanel, new ItemBlockPanel(storagePanel), "estoragepanel");
		for(PanelType type : PanelType.values()){
			regsiterTileEntity(type.clazz);
		}
		
		wirelessPanel = new BlockWirelessPanel();
		registerBlock(wirelessPanel, "ewirelesspanel");
		regsiterTileEntity(TileEntityWirelessPanel.class);
		
		wirelessPipe = new BlockWirelessPipeWrapper();
		registerBlock(wirelessPipe, "pipewrapper");
		regsiterTileEntity(TileEntityPipeWrapper.class);
		
		crafter = new BlockCrafter();
		registerBlock(crafter, "autocrafter");
		regsiterTileEntity(TileCrafter.class);
		
		encoder = new BlockPatternEncoder();
		registerEnumBlock(encoder, "craftingEncoder");
		regsiterTileEntity(TilePatternEncoder.class);
		regsiterTileEntity(TileProcessingPatternEncoder.class);
		
		cauldron = new BlockCrystalCauldron();
		registerBlock(cauldron, "crystalcauldron");
		regsiterTileEntity(TileEntityCrystalCauldron.class);
		
		cubeBlock = new BlockPlayerCubeBlock();
		registerBlock(cubeBlock, "playercubewall");
		
		cubeCore = new BlockPlayerCubeCore();
		registerBlock(cubeCore, "playercubecore");
		
		cubePortal = new BlockPlayerCubePortal();
		registerBlock(cubePortal, "playercubeportal");
		regsiterTileEntity(TileEntityPlayerCubePortal.class);
		
		matterCollector = new BlockMatterCollector();
		registerBlock(matterCollector, "mattercollector");
		regsiterTileEntity(TileEntityMatterCollector.class);
		
		battery = new BlockBattery();
		registerBlock(battery, "battery");
		regsiterTileEntity(TileEntityBattery.class);
		
		elevator = new BlockElevator();
		registerBlock(elevator, "elevator");
		regsiterTileEntity(TileEntityElevator.class);
		
		elevatorFloor = new BlockElevatorFloor();
		registerBlock(elevatorFloor, "elevatorfloor");
		regsiterTileEntity(TileEntityElevatorFloor.class);
		
		elevatorCaller = new BlockElevatorCaller();
		registerBlock(elevatorCaller, "elevatorcaller");
		regsiterTileEntity(TileEntityElevatorCaller.class);
		
		invCharger = new BlockInventoryCharger();
		registerBlock(invCharger, new ItemBlockMeta(invCharger), "inventorycharger");
		ItemBlockMeta.setMappingProperty(invCharger, BlockStateInventoryCharger.typeProperty);
		regsiterTileEntity(TileEntityInventoryChargerCU.class, TileEntityInventoryChargerRF.class);
		
		crystalMachine = new BlockCrystalMachine();
		registerBlock(crystalMachine, new ItemBlockMeta(crystalMachine), "crystalmachine");
		ItemBlockMeta.setMappingProperty(crystalMachine, BlockCrystalMachine.MACHINE_TYPE);
		for(MachineType type : MachineType.values()){
			regsiterTileEntity(type.clazz);
		}
		
		enderBuffer = new BlockEnderBuffer();
		registerBlock(enderBuffer, "enderbuffer");
		regsiterTileEntity(TileEntityEnderBuffer.class);
		
		worksite = new BlockWorksite();
		registerBlock(worksite, new ItemBlockWorksite(worksite), "worksite");
		for(WorksiteType type : WorksiteType.values()){
			regsiterTileEntity(type.clazz);
		}
		
		converter = new BlockPowerConverter();
		registerEnumBlock(converter, "powerconverter");
		regsiterTileEntity(TileEnergyConverterRFtoCU.class, TileEnergyConverterCUtoRF.class);
		
		customSpawner = new BlockCustomSpawner();
		registerBlock(customSpawner, "customspawner");
		regsiterTileEntity(TileEntityCustomSpawner.class);
		
		mobGrinder = registerBlock(new BlockMobGrinder(), "mobgrinder");
		regsiterTileEntity(TileEntityMobGrinder.class);
		
		darkIronRail = new BlockReinforcedRail();
		registerBlock(darkIronRail, "reinforcedRail");
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient(){
		for(Block block : REGISTRY.values()){
			if(block instanceof ICustomModel){
				((ICustomModel)block).initModel();
			}else{
				initBasicModel(block);
			}
		}
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHDDInterface.class, new TileEntityHDDInterfaceRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPanelItem.class, new TileEntityPanelItemRenderer<TileEntityPanelItem>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCustomSpawner.class, new RenderTileEntityCustomSpawner<TileEntityCustomSpawner>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerCubePortal.class, new TileEntityPlayerCubePortalRenderer<TileEntityPlayerCubePortal>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBattery.class, new TileEntityBatteryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevator.class, new TileEntityElevatorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorCaller.class, new TileEntityElevatorCallerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWorksiteBase.class, new TileWorksiteRenderer<TileWorksiteBase>());
	}
	
	@SideOnly(Side.CLIENT)
	public static void initBasicModel(Block block){
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
	
	protected static <T extends EnumBlock<?>> T registerEnumBlock(T block, String name) {
	    registerBlock(block, new ItemBlockMeta(block), name);
	    ItemBlockMeta.setMappingProperty(block, block.prop);
	    return block;
	}
	

	
	protected static <T extends Block> T registerBlock(T block, String name) {
			return registerBlock(block, new ItemBlock(block), name);
	}
	
	protected static <T extends Block> T registerBlock(T block, ItemBlock itemBlock, String name) {
			block.setUnlocalizedName(CrystalMod.prefix(name));
			block.setRegistryName(name);
			GameRegistry.register(block);
			GameRegistry.register(itemBlock.setRegistryName(name));
			REGISTRY.put(name, block);
			return block;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static <T extends TileEntity> void regsiterTileEntity(Class... clazzs){
		for(Class clazz : clazzs){
			GameRegistry.registerTileEntity(clazz, CrystalMod.MODID+"." + clazz.getName());
		}
	}
	
}
