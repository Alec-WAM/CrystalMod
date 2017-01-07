package alec_wam.CrystalMod.blocks;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalReed;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalSapling;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalTreePlant;
import alec_wam.CrystalMod.blocks.crops.BlockFlowerLilyPad;
import alec_wam.CrystalMod.blocks.crops.material.BlockMaterialCrop;
import alec_wam.CrystalMod.blocks.crops.material.RenderTileMaterialCrop;
import alec_wam.CrystalMod.blocks.crops.material.TileMaterialCrop;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass;
import alec_wam.CrystalMod.blocks.rail.BlockReinforcedRail;
import alec_wam.CrystalMod.tiles.cauldron.BlockCrystalCauldron;
import alec_wam.CrystalMod.tiles.cauldron.TileEntityCrystalCauldron;
import alec_wam.CrystalMod.tiles.chest.BlockCrystalChest;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.chest.ItemBlockCrystalChest;
import alec_wam.CrystalMod.tiles.chest.wireless.BlockWirelessChest;
import alec_wam.CrystalMod.tiles.chest.wireless.TileWirelessChest;
import alec_wam.CrystalMod.tiles.endertorch.BlockEnderTorch;
import alec_wam.CrystalMod.tiles.endertorch.TileEnderTorch;
import alec_wam.CrystalMod.tiles.entityhopper.BlockEntityHopper;
import alec_wam.CrystalMod.tiles.entityhopper.TileEntityEntityHopper;
import alec_wam.CrystalMod.tiles.fusion.BlockFusionPedistal;
import alec_wam.CrystalMod.tiles.fusion.BlockPedistal;
import alec_wam.CrystalMod.tiles.fusion.ItemBlockPedistal;
import alec_wam.CrystalMod.tiles.fusion.RenderTileFusionPedistal;
import alec_wam.CrystalMod.tiles.fusion.RenderTilePedistal;
import alec_wam.CrystalMod.tiles.fusion.TileFusionPedistal;
import alec_wam.CrystalMod.tiles.fusion.TilePedistal;
import alec_wam.CrystalMod.tiles.machine.advDispenser.BlockAdvDispenser;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.elevator.BlockElevator;
import alec_wam.CrystalMod.tiles.machine.elevator.TileEntityElevator;
import alec_wam.CrystalMod.tiles.machine.elevator.TileEntityElevatorRenderer;
import alec_wam.CrystalMod.tiles.machine.elevator.caller.BlockElevatorCaller;
import alec_wam.CrystalMod.tiles.machine.elevator.caller.TileEntityElevatorCaller;
import alec_wam.CrystalMod.tiles.machine.elevator.caller.TileEntityElevatorCallerRenderer;
import alec_wam.CrystalMod.tiles.machine.elevator.floor.BlockElevatorFloor;
import alec_wam.CrystalMod.tiles.machine.elevator.floor.TileEntityElevatorFloor;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.BlockEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.inventory.charger.BlockInventoryCharger;
import alec_wam.CrystalMod.tiles.machine.inventory.charger.BlockStateInventoryCharger;
import alec_wam.CrystalMod.tiles.machine.inventory.charger.TileEntityInventoryChargerCU;
import alec_wam.CrystalMod.tiles.machine.inventory.charger.TileEntityInventoryChargerRF;
import alec_wam.CrystalMod.tiles.machine.mobGrinder.BlockMobGrinder;
import alec_wam.CrystalMod.tiles.machine.mobGrinder.TileEntityMobGrinder;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery;
import alec_wam.CrystalMod.tiles.machine.power.battery.TileEntityBattery;
import alec_wam.CrystalMod.tiles.machine.power.battery.TileEntityBatteryRenderer;
import alec_wam.CrystalMod.tiles.machine.power.converter.BlockPowerConverter;
import alec_wam.CrystalMod.tiles.machine.power.converter.TileEnergyConverterCUtoRF;
import alec_wam.CrystalMod.tiles.machine.power.converter.TileEnergyConverterRFtoCU;
import alec_wam.CrystalMod.tiles.machine.power.engine.BlockEngine;
import alec_wam.CrystalMod.tiles.machine.power.engine.BlockEngine.EngineType;
import alec_wam.CrystalMod.tiles.machine.power.engine.ItemBlockEngine;
import alec_wam.CrystalMod.tiles.machine.worksite.BlockWorksite;
import alec_wam.CrystalMod.tiles.machine.worksite.BlockWorksite.WorksiteType;
import alec_wam.CrystalMod.tiles.machine.worksite.ItemBlockWorksite;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteRenderer;
import alec_wam.CrystalMod.tiles.matter.BlockMatterCollector;
import alec_wam.CrystalMod.tiles.matter.TileEntityMatterCollector;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import alec_wam.CrystalMod.tiles.pipes.ItemBlockPipe;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.BlockCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.BlockCraftingController;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.BlockPatternEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileCraftingController;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TilePatternEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileProcessingPatternEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.BlockPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.BlockPanel.PanelType;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.ItemBlockPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.display.TileEntityPanelItem;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.display.TileEntityPanelItemRenderer;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.BlockWirelessPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.TileEntityWirelessPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.stocker.BlockStocker;
import alec_wam.CrystalMod.tiles.pipes.estorage.stocker.TileEntityStocker;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.external.BlockExternalInterface;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.external.TileEntityExternalInterface;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.BlockHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.TileEntityHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.TileEntityHDDInterfaceRenderer;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array.BlockHDDArray;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array.TileHDDArray;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array.TileHDDArrayRenderer;
import alec_wam.CrystalMod.tiles.pipes.wireless.BlockWirelessPipeWrapper;
import alec_wam.CrystalMod.tiles.pipes.wireless.TileEntityPipeWrapper;
import alec_wam.CrystalMod.tiles.playercube.BlockPlayerCubeBlock;
import alec_wam.CrystalMod.tiles.playercube.BlockPlayerCubeCore;
import alec_wam.CrystalMod.tiles.playercube.BlockPlayerCubePortal;
import alec_wam.CrystalMod.tiles.playercube.TileEntityPlayerCubePortal;
import alec_wam.CrystalMod.tiles.playercube.TileEntityPlayerCubePortalRenderer;
import alec_wam.CrystalMod.tiles.spawner.BlockCustomSpawner;
import alec_wam.CrystalMod.tiles.spawner.RenderTileEntityCustomSpawner;
import alec_wam.CrystalMod.tiles.spawner.TileEntityCustomSpawner;
import alec_wam.CrystalMod.tiles.tank.BlockTank;
import alec_wam.CrystalMod.tiles.tank.ItemBlockTank;
import alec_wam.CrystalMod.tiles.tank.TileEntityTank;
import alec_wam.CrystalMod.tiles.weather.BlockWeather;
import alec_wam.CrystalMod.tiles.weather.TileEntityWeather;
import alec_wam.CrystalMod.tiles.workbench.BlockCrystalWorkbench;
import alec_wam.CrystalMod.tiles.workbench.TileEntityCrystalWorkbench;
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

public class ModBlocks {

	public static final Map<String, Block> REGISTRY = Maps.newHashMap();
	
	public static BlockCrystal crystal;
	public static BlockCrystalOre crystalOre;
	public static BlockCrystalIngot crystalIngot;
	public static BlockCrystalGlass crystalGlass;
	public static BlockCrystalReed crystalReedsBlue, crystalReedsRed, crystalReedsGreen, crystalReedsDark;
	public static BlockFlowerLilyPad flowerLilypad;
	public static BlockCrystalWorkbench crystalWorkbench;
	public static BlockCrystalChest crystalChest;
	public static BlockWirelessChest wirelessChest;
	public static BlockCrystalPlant crystalPlantBlue, crystalPlantRed, crystalPlantGreen, crystalPlantDark;
	public static BlockCrystalTreePlant crystalTreePlantBlue, crystalTreePlantRed, crystalTreePlantGreen, crystalTreePlantDark;
	public static BlockCrystalLog crystalLog;
	public static BlockCrystalLeaves crystalLeaves;
	public static BlockCrystalSapling crystalSapling;
	public static BlockMaterialCrop materialCrop;
	public static BlockPipe crystalPipe;
	public static BlockTank crystalTank;
	public static BlockEngine engine;
	public static BlockWeather weather;
	public static BlockCrystalCauldron cauldron;
	public static BlockCustomSpawner customSpawner;
	public static BlockEnderTorch enderTorch;
	public static BlockMobGrinder mobGrinder;
	public static BlockEntityHopper entityHopper;
	
	public static BlockHDDInterface hddInterface;
	public static BlockHDDArray hddArray;
	public static BlockExternalInterface externalInterface;
	public static BlockPanel storagePanel;
	public static BlockWirelessPanel wirelessPanel;
	public static BlockWirelessPipeWrapper wirelessPipe;
	public static BlockCraftingController craftingController;
	public static BlockCrafter crafter;
	public static BlockStocker stocker;
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
	public static BlockAdvDispenser advDispenser;
	
	public static BlockReinforcedRail darkIronRail;
	
	public static BlockPedistal pedistal;
	public static BlockFusionPedistal fusionPedistal;

	public static final EnumPlantType crystalPlantType = EnumPlantType.getPlantType("crystal");
	
	public static void init() {
		crystal = registerEnumBlock(new BlockCrystal(), "crystalblock");
		
		crystalOre = registerEnumBlock(new BlockCrystalOre(), "crystalore");
		
		crystalIngot = registerEnumBlock(new BlockCrystalIngot(), "crystalingotblock");
		
		crystalGlass = registerEnumBlock(new BlockCrystalGlass(), "crystalglass");
		
		crystalReedsBlue = registerBlock(new BlockCrystalReed(PlantType.BLUE), "bluecrystalreedblock");
		crystalReedsRed = registerBlock(new BlockCrystalReed(PlantType.RED), "redcrystalreedblock");
		crystalReedsGreen = registerBlock(new BlockCrystalReed(PlantType.GREEN), "greencrystalreedblock");
		crystalReedsDark = registerBlock(new BlockCrystalReed(PlantType.DARK), "darkcrystalreedblock");

		
		flowerLilypad = new BlockFlowerLilyPad();
		registerBlock(flowerLilypad, "flowerlilypad");
		
		crystalWorkbench = new BlockCrystalWorkbench();
		registerEnumBlock(crystalWorkbench, "crystalworkbench");
		registerTileEntity(TileEntityCrystalWorkbench.class);
		
		crystalChest = new BlockCrystalChest();
		registerBlock(crystalChest, new ItemBlockCrystalChest(crystalChest), "crystalchest");
		
		for (CrystalChestType typ : CrystalChestType.values())
        {
            GameRegistry.registerTileEntityWithAlternatives(typ.clazz, CrystalMod.MODID+"." + typ.name(), typ.name());
        }
		
		wirelessChest = new BlockWirelessChest();
		registerBlock(wirelessChest, "wirelesschest");
		registerTileEntity(TileWirelessChest.class);
		
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
		
		materialCrop = new BlockMaterialCrop();
		registerBlock(materialCrop, "materialcrop");
		registerTileEntity(TileMaterialCrop.class);
		
		crystalPipe = new BlockPipe();
		registerBlock(crystalPipe, new ItemBlockPipe(crystalPipe), "crystalpipe");
		for(PipeType type : PipeType.values()){
			registerTileEntity(type.clazz);
		}
		
		crystalTank = new BlockTank();
		registerBlock(crystalTank, new ItemBlockTank(crystalTank), "crystaltank");
		ItemBlockMeta.setMappingProperty(crystalTank, BlockTank.TYPE);
	    registerTileEntity(TileEntityTank.class);
		
		engine = new BlockEngine();
		registerBlock(engine, new ItemBlockEngine(engine), "engine");
		for(EngineType type : EngineType.values()){
			registerTileEntity(type.clazz);
		}
		
		weather = new BlockWeather();
		registerBlock(weather, "weather");
		registerTileEntity(TileEntityWeather.class);
		
		hddInterface = new BlockHDDInterface();
		registerBlock(hddInterface, "hddinterface");
		registerTileEntity(TileEntityHDDInterface.class);
		
		hddArray = new BlockHDDArray();
		registerBlock(hddArray, "hddarray");
		registerTileEntity(TileHDDArray.class);
		
		externalInterface = new BlockExternalInterface();
		registerBlock(externalInterface, "externalinterface");
		registerTileEntity(TileEntityExternalInterface.class);
		
		storagePanel = new BlockPanel();
		registerBlock(storagePanel, new ItemBlockPanel(storagePanel), "estoragepanel");
		for(PanelType type : PanelType.values()){
			registerTileEntity(type.clazz);
		}
		
		wirelessPanel = new BlockWirelessPanel();
		registerBlock(wirelessPanel, "ewirelesspanel");
		registerTileEntity(TileEntityWirelessPanel.class);
		
		wirelessPipe = new BlockWirelessPipeWrapper();
		registerBlock(wirelessPipe, "pipewrapper");
		registerTileEntity(TileEntityPipeWrapper.class);
		
		craftingController = new BlockCraftingController();
		registerBlock(craftingController, "craftingController");
		registerTileEntity(TileCraftingController.class);
		
		crafter = new BlockCrafter();
		registerBlock(crafter, "autocrafter");
		registerTileEntity(TileCrafter.class);
		
		stocker = registerBlock(new BlockStocker(), "stocker");
		registerTileEntity(TileEntityStocker.class);
		
		encoder = new BlockPatternEncoder();
		registerEnumBlock(encoder, "craftingEncoder");
		registerTileEntity(TilePatternEncoder.class);
		registerTileEntity(TileProcessingPatternEncoder.class);
		
		cauldron = new BlockCrystalCauldron();
		registerBlock(cauldron, "crystalcauldron");
		registerTileEntity(TileEntityCrystalCauldron.class);
		
		cubeBlock = new BlockPlayerCubeBlock();
		registerBlock(cubeBlock, "playercubewall");
		
		cubeCore = new BlockPlayerCubeCore();
		registerBlock(cubeCore, "playercubecore");
		
		cubePortal = new BlockPlayerCubePortal();
		registerBlock(cubePortal, "playercubeportal");
		registerTileEntity(TileEntityPlayerCubePortal.class);
		
		matterCollector = new BlockMatterCollector();
		registerBlock(matterCollector, "mattercollector");
		registerTileEntity(TileEntityMatterCollector.class);
		
		battery = new BlockBattery();
		registerBlock(battery, new ItemBlockMeta(battery), "battery");
		ItemBlockMeta.setMappingProperty(battery, BlockBattery.TYPE);
		registerTileEntity(TileEntityBattery.class);
		
		elevator = new BlockElevator();
		registerBlock(elevator, "elevator");
		registerTileEntity(TileEntityElevator.class);
		
		elevatorFloor = new BlockElevatorFloor();
		registerBlock(elevatorFloor, "elevatorfloor");
		registerTileEntity(TileEntityElevatorFloor.class);
		
		elevatorCaller = new BlockElevatorCaller();
		registerBlock(elevatorCaller, "elevatorcaller");
		registerTileEntity(TileEntityElevatorCaller.class);
		
		invCharger = new BlockInventoryCharger();
		registerBlock(invCharger, new ItemBlockMeta(invCharger), "inventorycharger");
		ItemBlockMeta.setMappingProperty(invCharger, BlockStateInventoryCharger.typeProperty);
		registerTileEntity(TileEntityInventoryChargerCU.class, TileEntityInventoryChargerRF.class);
		
		crystalMachine = new BlockCrystalMachine();
		registerBlock(crystalMachine, new ItemBlockMeta(crystalMachine), "crystalmachine");
		ItemBlockMeta.setMappingProperty(crystalMachine, BlockCrystalMachine.MACHINE_TYPE);
		for(MachineType type : MachineType.values()){
			registerTileEntity(type.clazz);
		}
		
		enderBuffer = new BlockEnderBuffer();
		registerBlock(enderBuffer, "enderbuffer");
		registerTileEntity(TileEntityEnderBuffer.class);
		
		worksite = new BlockWorksite();
		registerBlock(worksite, new ItemBlockWorksite(worksite), "worksite");
		for(WorksiteType type : WorksiteType.values()){
			registerTileEntity(type.clazz);
		}
		
		converter = new BlockPowerConverter();
		registerEnumBlock(converter, "powerconverter");
		registerTileEntity(TileEnergyConverterRFtoCU.class, TileEnergyConverterCUtoRF.class);
		
		advDispenser = new BlockAdvDispenser();
		registerBlock(advDispenser, "advdispenser");
		registerTileEntity(TileAdvDispenser.class);
		
		customSpawner = new BlockCustomSpawner();
		registerBlock(customSpawner, "customspawner");
		registerTileEntity(TileEntityCustomSpawner.class);
		
		enderTorch = registerBlock(new BlockEnderTorch(), "endertorch");
		registerTileEntity(TileEnderTorch.class);
		
		mobGrinder = registerBlock(new BlockMobGrinder(), "mobgrinder");
		registerTileEntity(TileEntityMobGrinder.class);
		
		entityHopper = registerBlock(new BlockEntityHopper(), "entityhopper");
		registerTileEntity(TileEntityEntityHopper.class);
		
		darkIronRail = new BlockReinforcedRail();
		registerBlock(darkIronRail, "reinforcedRail");
		
		pedistal = new BlockPedistal();
		registerBlock(pedistal, new ItemBlockPedistal(pedistal), "pedistal");
		registerTileEntity(TilePedistal.class);
		
		fusionPedistal = new BlockFusionPedistal();
		registerBlock(fusionPedistal, new ItemBlockPedistal(fusionPedistal), "fusionpedistal");
		registerTileEntity(TileFusionPedistal.class);
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileHDDArray.class, new TileHDDArrayRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPanelItem.class, new TileEntityPanelItemRenderer<TileEntityPanelItem>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCustomSpawner.class, new RenderTileEntityCustomSpawner<TileEntityCustomSpawner>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerCubePortal.class, new TileEntityPlayerCubePortalRenderer<TileEntityPlayerCubePortal>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBattery.class, new TileEntityBatteryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevator.class, new TileEntityElevatorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorCaller.class, new TileEntityElevatorCallerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWorksiteBase.class, new TileWorksiteRenderer<TileWorksiteBase>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMaterialCrop.class, new RenderTileMaterialCrop<TileMaterialCrop>());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePedistal.class, new RenderTilePedistal<TilePedistal>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFusionPedistal.class, new RenderTileFusionPedistal<TileFusionPedistal>());
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
	
	public static <T extends Block> T registerBlock(T block, String name) {
			return registerBlock(block, new ItemBlock(block), name);
	}
	
	public static <T extends Block> T registerBlock(T block, ItemBlock itemBlock, String name) {
			block.setUnlocalizedName(CrystalMod.prefix(name));
			block.setRegistryName(name);
			GameRegistry.register(block);
			GameRegistry.register(itemBlock.setRegistryName(name));
			REGISTRY.put(name, block);
			return block;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static <T extends TileEntity> void registerTileEntity(Class... clazzs){
		for(Class clazz : clazzs){
			GameRegistry.registerTileEntity(clazz, CrystalMod.MODID+"." + clazz.getName());
		}
	}
	
}
