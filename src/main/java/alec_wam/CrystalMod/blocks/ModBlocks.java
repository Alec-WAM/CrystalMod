package alec_wam.CrystalMod.blocks;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCompressed.CompressedBlockType;
import alec_wam.CrystalMod.blocks.crops.BlockCorn;
import alec_wam.CrystalMod.blocks.crops.BlockCrysineMushroom;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalBerryBush;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalReed;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalSapling;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalTreePlant;
import alec_wam.CrystalMod.blocks.crops.BlockFlowerLilyPad;
import alec_wam.CrystalMod.blocks.crops.BlockHugeCrysineMushroom;
import alec_wam.CrystalMod.blocks.crops.BlockNormalSapling;
import alec_wam.CrystalMod.blocks.crops.ItemBlockWater;
import alec_wam.CrystalMod.blocks.crops.bamboo.BlockBamboo;
import alec_wam.CrystalMod.blocks.crops.bamboo.BlockBambooLeaves;
import alec_wam.CrystalMod.blocks.crops.material.BlockMaterialCrop;
import alec_wam.CrystalMod.blocks.crops.material.RenderTileMaterialCrop;
import alec_wam.CrystalMod.blocks.crops.material.TileMaterialCrop;
import alec_wam.CrystalMod.blocks.crystexium.BlockCrysidian;
import alec_wam.CrystalMod.blocks.crystexium.BlockCrystexPortal;
import alec_wam.CrystalMod.blocks.crystexium.BlockCrystheriumPlant;
import alec_wam.CrystalMod.blocks.crystexium.CrystexiumBlock;
import alec_wam.CrystalMod.blocks.crystexium.CrystexiumBlock.CrystexiumBlockType;
import alec_wam.CrystalMod.blocks.crystexium.CrystexiumSlab;
import alec_wam.CrystalMod.blocks.crystexium.ItemBlockCrystheriumPlant;
import alec_wam.CrystalMod.blocks.decorative.BlockBetterRoses;
import alec_wam.CrystalMod.blocks.decorative.BlockCustomDoor;
import alec_wam.CrystalMod.blocks.decorative.BlockFailure;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyGlowstone;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyLadder;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyLadder2;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyObsidian;
import alec_wam.CrystalMod.blocks.decorative.BlockFancyPumpkin;
import alec_wam.CrystalMod.blocks.decorative.BlockFancySeaLantern;
import alec_wam.CrystalMod.blocks.decorative.BlockOctagonalBricks;
import alec_wam.CrystalMod.blocks.decorative.ItemBlockBetterRose;
import alec_wam.CrystalMod.blocks.decorative.ItemBlockFancyLadders;
import alec_wam.CrystalMod.blocks.decorative.ItemBlockFancyLadders2;
import alec_wam.CrystalMod.blocks.decorative.bridge.BlockBridge;
import alec_wam.CrystalMod.blocks.decorative.bridge.TileBridge;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockBasicTiles;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockBasicTiles2;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockBasicTiles2.BasicTileType2;
import alec_wam.CrystalMod.blocks.decorative.tiles.BlockCrystalTiles;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlassPane;
import alec_wam.CrystalMod.blocks.glass.BlockPaintedCrystalGlass;
import alec_wam.CrystalMod.blocks.glass.BlockTintedCrystalGlass;
import alec_wam.CrystalMod.blocks.rail.BlockReinforcedRail;
import alec_wam.CrystalMod.blocks.underwater.BlockCoral;
import alec_wam.CrystalMod.blocks.underwater.BlockKelp;
import alec_wam.CrystalMod.blocks.underwater.BlockSeaweed;
import alec_wam.CrystalMod.blocks.underwater.ItemBlockKelp;
import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailAscending;
import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailBasic;
import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailBasicGround;
import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailBooster;
import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailBoosterGround;
import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailDetector;
import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailEntrance;
import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailHolding;
import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailHoldingGround;
import alec_wam.CrystalMod.handler.MissingItemHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.BlockBasicTile;
import alec_wam.CrystalMod.tiles.cases.BlockCase;
import alec_wam.CrystalMod.tiles.cases.RenderTileEntityCasePiston;
import alec_wam.CrystalMod.tiles.cases.TileEntityCaseNoteblock;
import alec_wam.CrystalMod.tiles.cases.TileEntityCasePiston;
import alec_wam.CrystalMod.tiles.cauldron.BlockCrystalCauldron;
import alec_wam.CrystalMod.tiles.cauldron.RenderTileCrystalCauldron;
import alec_wam.CrystalMod.tiles.cauldron.TileEntityCrystalCauldron;
import alec_wam.CrystalMod.tiles.chest.BlockCrystalChest;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.tiles.chest.ItemBlockCrystalChest;
import alec_wam.CrystalMod.tiles.chest.wireless.BlockWirelessChest;
import alec_wam.CrystalMod.tiles.chest.wireless.TileWirelessChest;
import alec_wam.CrystalMod.tiles.chest.wooden.BlockWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chest.wooden.ItemBlockWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chest.wooden.WoodenCrystalChestType;
import alec_wam.CrystalMod.tiles.cluster.BlockCrystalCluster;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster;
import alec_wam.CrystalMod.tiles.crate.BlockCrate;
import alec_wam.CrystalMod.tiles.crate.RenderTileCrate;
import alec_wam.CrystalMod.tiles.crate.TileCrate;
import alec_wam.CrystalMod.tiles.darkinfection.BlockDarkInfection;
import alec_wam.CrystalMod.tiles.darkinfection.BlockDenseDarkness;
import alec_wam.CrystalMod.tiles.darkinfection.BlockInfected;
import alec_wam.CrystalMod.tiles.darkinfection.TileDarkInfection;
import alec_wam.CrystalMod.tiles.endertorch.BlockEnderTorch;
import alec_wam.CrystalMod.tiles.endertorch.TileEnderTorch;
import alec_wam.CrystalMod.tiles.entityhopper.BlockEntityHopper;
import alec_wam.CrystalMod.tiles.entityhopper.TileEntityEntityHopper;
import alec_wam.CrystalMod.tiles.explosives.fuser.BlockOppositeFuser;
import alec_wam.CrystalMod.tiles.explosives.fuser.TileOppositeFuser;
import alec_wam.CrystalMod.tiles.explosives.fuser.TileOppositeFuserTier2;
import alec_wam.CrystalMod.tiles.explosives.fuser.TileOppositeFuserTier3;
import alec_wam.CrystalMod.tiles.explosives.particle.BlockParticleThrower;
import alec_wam.CrystalMod.tiles.explosives.particle.TileParticleThrower;
import alec_wam.CrystalMod.tiles.explosives.remover.BlockRemoverExplosion;
import alec_wam.CrystalMod.tiles.explosives.remover.TileRemoverExplosion;
import alec_wam.CrystalMod.tiles.fusion.BlockFusionPedistal;
import alec_wam.CrystalMod.tiles.fusion.BlockPedistal;
import alec_wam.CrystalMod.tiles.fusion.ItemBlockPedistal;
import alec_wam.CrystalMod.tiles.fusion.RenderTileFusionPedistal;
import alec_wam.CrystalMod.tiles.fusion.RenderTilePedistal;
import alec_wam.CrystalMod.tiles.fusion.TileFusionPedistal;
import alec_wam.CrystalMod.tiles.fusion.TilePedistal;
import alec_wam.CrystalMod.tiles.jar.BlockJar;
import alec_wam.CrystalMod.tiles.jar.TileJar;
import alec_wam.CrystalMod.tiles.lamps.BlockAdvancedLamp;
import alec_wam.CrystalMod.tiles.lamps.BlockFakeLight;
import alec_wam.CrystalMod.tiles.lamps.TileAdvancedLamp;
import alec_wam.CrystalMod.tiles.lamps.TileAdvancedLampDark;
import alec_wam.CrystalMod.tiles.machine.advDispenser.BlockAdvDispenser;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.dna.BlockDNAMachine;
import alec_wam.CrystalMod.tiles.machine.dna.TileEntityDNAMachine;
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
import alec_wam.CrystalMod.tiles.machine.epst.BlockEPST;
import alec_wam.CrystalMod.tiles.machine.epst.TileEPST;
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
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.BlockCongealedSponge;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.BlockRedstoneCore;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.BlockRedstoneReactor;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.TileRedstoneReactor;
import alec_wam.CrystalMod.tiles.machine.sap.BlockSapExtractor;
import alec_wam.CrystalMod.tiles.machine.sap.TileSapExtractor;
import alec_wam.CrystalMod.tiles.machine.seismic.TileEntitySeismicScanner;
import alec_wam.CrystalMod.tiles.machine.specialengines.BlockSpecialEngine;
import alec_wam.CrystalMod.tiles.machine.specialengines.BlockSpecialEngine.SpecialEngineType;
import alec_wam.CrystalMod.tiles.machine.worksite.BlockWorksite;
import alec_wam.CrystalMod.tiles.machine.worksite.BlockWorksite.WorksiteType;
import alec_wam.CrystalMod.tiles.machine.worksite.ItemBlockWorksite;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteRenderer;
import alec_wam.CrystalMod.tiles.machine.xpfountain.BlockXPFountain;
import alec_wam.CrystalMod.tiles.machine.xpfountain.TileEntityXPFountain;
import alec_wam.CrystalMod.tiles.obsidiandispenser.BlockObsidianDispenser;
import alec_wam.CrystalMod.tiles.obsidiandispenser.TileObsidianDispenser;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import alec_wam.CrystalMod.tiles.pipes.ItemBlockPipe;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipeRenderer;
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
import alec_wam.CrystalMod.tiles.pipes.estorage.power.BlockNetworkPowerCore;
import alec_wam.CrystalMod.tiles.pipes.estorage.power.TileNetworkPowerCore;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.BlockSecurityController;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.BlockSecurityEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.TileSecurityController;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.TileSecurityEncoder;
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
import alec_wam.CrystalMod.tiles.portal.BlockTelePortal;
import alec_wam.CrystalMod.tiles.portal.TileTelePortal;
import alec_wam.CrystalMod.tiles.shieldrack.BlockShieldRack;
import alec_wam.CrystalMod.tiles.shieldrack.ItemBlockShieldRack;
import alec_wam.CrystalMod.tiles.shieldrack.TileShieldRack;
import alec_wam.CrystalMod.tiles.soundmuffler.TileSoundMuffler;
import alec_wam.CrystalMod.tiles.spawner.BlockCustomSpawner;
import alec_wam.CrystalMod.tiles.spawner.RenderTileEntityCustomSpawner;
import alec_wam.CrystalMod.tiles.spawner.TileEntityCustomSpawner;
import alec_wam.CrystalMod.tiles.tank.BlockTank;
import alec_wam.CrystalMod.tiles.tank.ItemBlockTank;
import alec_wam.CrystalMod.tiles.tank.TileEntityTank;
import alec_wam.CrystalMod.tiles.tooltable.BlockEnhancementTable;
import alec_wam.CrystalMod.tiles.tooltable.TileEnhancementTable;
import alec_wam.CrystalMod.tiles.weather.TileEntityWeather;
import alec_wam.CrystalMod.tiles.workbench.BlockCrystalWorkbench;
import alec_wam.CrystalMod.tiles.workbench.TileEntityCrystalWorkbench;
import alec_wam.CrystalMod.tiles.xp.BlockXPTank;
import alec_wam.CrystalMod.tiles.xp.RenderTileEntityXPTank;
import alec_wam.CrystalMod.tiles.xp.TileEntityXPTank;
import alec_wam.CrystalMod.tiles.xp.TileEntityXPVacuum;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

	public static final Map<String, Block> REGISTRY = Maps.newHashMap();

	public static BlockCrystal crystal;
	public static BlockCrystalOre crystalOre;
	public static BlockCrystalIngot crystalIngot;
	public static BlockEtchedCrystal crystalEtched;
	public static BlockDecorative decorativeBlock;
	public static BlockCrystalGlass crystalGlass;
	public static BlockTintedCrystalGlass crystalGlassTinted;
	public static BlockPaintedCrystalGlass crystalGlassPainted;
	public static BlockCrystalGlassPane crystalGlassPane;
	public static BlockMetalBars metalBars;
	public static BlockFancyGlowstone fancyGlowstone;
	public static BlockFancyObsidian fancyObsidian;
	public static BlockFancySeaLantern fancySeaLantern;
	public static BlockCrystalReed crystalReedsBlue, crystalReedsRed, crystalReedsGreen, crystalReedsDark;
	public static BlockFlowerLilyPad flowerLilypad;
	public static BlockCrystalWorkbench crystalWorkbench;
	public static BlockEnhancementTable enhancementTable;
	public static BlockCrystalChest crystalChest;
	public static BlockWoodenCrystalChest crystalWoodenChest;
	public static BlockWirelessChest wirelessChest;
	public static BlockCase storageCase;
	
	public static BlockCrystalPlant crystalPlant;
	public static BlockCrystalTreePlant crystalTreePlantBlue, crystalTreePlantRed, crystalTreePlantGreen, crystalTreePlantDark;
	public static BlockCrystalBerryBush crystalBush;	
	public static BlockGlowBerry glowBerryBlue, glowBerryRed, glowBerryGreen, glowBerryDark;	
	public static BlockCrystalLog crystalLog;
	public static BlockCrystalLeaves crystalLeaves;
	public static BlockCrystalSapling crystalSapling;
	public static BlockCrystalPlanks crystalPlanks;	
	public static BlockMaterialCrop materialCrop;
	public static BlockCorn corn;
	public static BlockBamboo bamboo;
	public static BlockBambooLeaves bambooLeaves;
	public static Block bambooPlanks;
	public static BlockNormalSapling normalSapling;
	public static BlockFancyPumpkin fancyPumpkin;
	public static BlockFancyPumpkin fancyPumpkinLit;
	public static BlockCrysineMushroom crysineMushroom;
	public static BlockHugeCrysineMushroom crysineMushroomBlock;
	
	public static BlockCrystalLadder ladder;
	public static BlockFancyLadder fancyLadders;
	public static BlockFancyLadder2 fancyLadders2;
	
	public static BlockPipe crystalPipe;
	public static BlockTank crystalTank;
	public static BlockEngine engine;
	public static BlockSpecialEngine specialengine;
	public static BlockBasicTile weather;
	public static BlockCrystalCauldron cauldron;
	public static BlockCustomSpawner customSpawner;
	public static BlockEnderTorch enderTorch;
	public static BlockMobGrinder mobGrinder;
	public static BlockEntityHopper entityHopper;
	public static BlockBasicTile xpVacuum;
	public static BlockXPFountain xpFountain;
	public static BlockXPTank xpTank;
	public static BlockBasicTile seismicScanner;
	
	public static BlockCrate crates;
	public static BlockAdvancedLamp advancedLamp;
	public static BlockFakeLight fakeLight;
	public static BlockCompressed compressed;
	public static BlockFallingCompressed fallingCompressed;
	public static BlockBlazeRod blazeRodBlock;	

	public static BlockBasicTiles tileBasic;
	public static BlockBasicTiles2 tileBasic2;
	public static BlockCrystalTiles tileCrystal;	
	public static BlockCrystalLight crystalLight;
	public static BlockFailure failureBlock;
	public static BlockOctagonalBricks octagonBricks;

	public static BlockRedstoneReactor redstoneReactor;
	public static BlockRedstoneCore redstoneCore;
	public static BlockCongealedSponge redstoneSponge;

	public static BlockHDDInterface hddInterface;
	public static BlockHDDArray hddArray;
	public static BlockExternalInterface externalInterface;
	public static BlockPanel storagePanel;
	public static BlockWirelessPanel wirelessPanel;
	public static BlockWirelessPipeWrapper wirelessPipe;
	public static BlockCraftingController craftingController;
	public static BlockNetworkPowerCore powerCore;
	public static BlockCrafter crafter;
	public static BlockStocker stocker;
	public static BlockPatternEncoder encoder;
	public static BlockSecurityController securityController;
	public static BlockSecurityEncoder securityEncoder;

	public static BlockPlayerCubeBlock cubeBlock;
	public static BlockPlayerCubeCore cubeCore;
	public static BlockPlayerCubePortal cubePortal;
	public static BlockTelePortal telePortal;

	public static BlockBattery battery;
	public static BlockElevator elevator;
	public static BlockElevatorFloor elevatorFloor;
	public static BlockElevatorCaller elevatorCaller;
	public static BlockInventoryCharger invCharger;
	public static BlockCrystalMachine crystalMachine;
	public static BlockSapExtractor sapExtractor;
	public static BlockDNAMachine dnaMachine;
	public static BlockEnderBuffer enderBuffer;
	public static BlockWorksite worksite;

	public static BlockPowerConverter converter;
	public static BlockAdvDispenser advDispenser;
	public static BlockObsidianDispenser obsidianDispenser;
	public static BlockEPST epst;

	public static BlockReinforcedRail darkIronRail;
	public static BlockBridge bridge;
	public static BlockJar jar;
	public static BlockShieldRack shieldRack;
	public static BlockBasicTile muffler;
	public static BlockCustomDoor bambooDoor;
	
	public static BlockFlumeRailBasic flumeRailBasic;
	public static BlockFlumeRailHolding flumeRailHolding;
	public static BlockFlumeRailBooster flumeRailBooster;
	public static BlockFlumeRailDetector flumeRailDetector;
	public static BlockFlumeRailEntrance flumeRailEntrance;
	
	public static BlockFlumeRailBasicGround flumeRailBasicGround;
	public static BlockFlumeRailHoldingGround flumeRailHoldingGround;
	public static BlockFlumeRailBoosterGround flumeRailBoosterGround;
	public static BlockFlumeRailAscending flumeRailRamp;

	public static BlockParticleThrower particleThrower;
	public static BlockRemoverExplosion remover;
	public static BlockOppositeFuser oppositeFuser;
	public static BlockDarkInfection darkInfection;
	public static BlockInfected infectedBlock;
	public static BlockDenseDarkness denseDarkness;

	public static BlockPedistal pedistal;
	public static BlockFusionPedistal fusionPedistal;

	public static BlockCrystalCluster crystalCluster;
	
	public static BlockKelp kelp;
	public static BlockSeaweed seaweed;
	public static BlockCoral coral;
	
	public static BlockBetterRoses roseBush;
	
	public static CrystexiumBlock crystexiumBlock;
	public static CrystexiumBlock blueCrystexiumBlock;
	public static CrystexiumBlock redCrystexiumBlock;
	public static CrystexiumBlock greenCrystexiumBlock;
	public static CrystexiumBlock darkCrystexiumBlock;
	public static CrystexiumBlock pureCrystexiumBlock;
	public static CrystexiumSlab crystexiumSlab;
	public static BlockCrysidian crysidian;
	public static BlockCrystheriumPlant crystheriumPlant;
	public static BlockCrystexPortal crystexPortal;
	
	public static BlockStairs crystexiumStairs;
	public static BlockStairs blueCrystexiumStairs;
	public static BlockStairs redCrystexiumStairs;
	public static BlockStairs greenCrystexiumStairs;
	public static BlockStairs darkCrystexiumStairs;
	public static BlockStairs pureCrystexiumStairs;

	public static final EnumPlantType crystalPlantType = EnumPlantType.getPlantType("crystal");

	public static void init() {
		crystal = registerEnumBlock(new BlockCrystal(), "crystalblock");
		crystalOre = registerEnumBlock(new BlockCrystalOre(), "crystalore");
		crystalIngot = registerEnumBlock(new BlockCrystalIngot(), "crystalingotblock");
		crystalEtched = registerEnumBlock(new BlockEtchedCrystal(), "etchedcrystalblock");
		decorativeBlock = registerEnumBlock(new BlockDecorative(), "decorativeblock");

		crystalGlass = registerEnumBlock(new BlockCrystalGlass(), "crystalglass");
		crystalGlassTinted = registerEnumBlock(new BlockTintedCrystalGlass(), "crystalglasstinted");
		crystalGlassPainted = registerEnumBlock(new BlockPaintedCrystalGlass(), "crystalglasspainted");

		crystalGlassPane = new BlockCrystalGlassPane();
		registerBlock(crystalGlassPane, new ItemBlockMeta(crystalGlassPane), "crystalglasspane");
		ItemBlockMeta.setMappingProperty(crystalGlassPane, BlockCrystalGlass.TYPE);

		fancyGlowstone = registerEnumBlock(new BlockFancyGlowstone(), "fancyglowstone");
		fancyObsidian = registerEnumBlock(new BlockFancyObsidian(), "fancyobsidian");
		fancySeaLantern = registerEnumBlock(new BlockFancySeaLantern(), "fancysealantern");
		
		metalBars = new BlockMetalBars();
		registerBlock(metalBars, new ItemBlockMeta(metalBars), "metalbars");
		ItemBlockMeta.setMappingProperty(metalBars, BlockMetalBars.TYPE);

		crystalReedsBlue = registerBlock(new BlockCrystalReed(PlantType.BLUE), "bluecrystalreedblock");
		crystalReedsRed = registerBlock(new BlockCrystalReed(PlantType.RED), "redcrystalreedblock");
		crystalReedsGreen = registerBlock(new BlockCrystalReed(PlantType.GREEN), "greencrystalreedblock");
		crystalReedsDark = registerBlock(new BlockCrystalReed(PlantType.DARK), "darkcrystalreedblock");

		flowerLilypad = new BlockFlowerLilyPad();
		registerBlock(flowerLilypad, new ItemBlockWater(flowerLilypad), "flowerlilypad");

		crystalWorkbench = new BlockCrystalWorkbench();
		registerEnumBlock(crystalWorkbench, "crystalworkbench");
		registerTileEntity(TileEntityCrystalWorkbench.class);

		enhancementTable = new BlockEnhancementTable();
		registerBlock(enhancementTable, "enhancementtable");
		registerTileEntity(TileEnhancementTable.class);

		crystalChest = new BlockCrystalChest();
		registerBlock(crystalChest, new ItemBlockCrystalChest(crystalChest), "crystalchest");

		for (CrystalChestType typ : CrystalChestType.values()) {
			registerTileEntity(typ.clazz);
		}

		crystalWoodenChest = new BlockWoodenCrystalChest();
		registerBlock(crystalWoodenChest, new ItemBlockWoodenCrystalChest(crystalWoodenChest), "woodencrystalchest");

		for (WoodenCrystalChestType typ : WoodenCrystalChestType.values()) {
			registerTileEntity(typ.clazz);
		}

		wirelessChest = new BlockWirelessChest();
		registerBlock(wirelessChest, "wirelesschest");
		registerTileEntity(TileWirelessChest.class);
		
		storageCase = new BlockCase();
		registerEnumBlock(storageCase, "storagecase");
		registerTileEntity(TileEntityCaseNoteblock.class, TileEntityCasePiston.class);

		crystalPlant = new BlockCrystalPlant();
		registerBlock(crystalPlant, new ItemBlockMeta(crystalPlant), "crystalplant");
		ItemBlockMeta.setMappingProperty(crystalPlant, BlockCrystalPlant.TYPE);

		crystalTreePlantBlue = new BlockCrystalTreePlant(PlantType.BLUE);
		registerBlock(crystalTreePlantBlue, "bluecrystaltreeplant");

		crystalTreePlantRed = new BlockCrystalTreePlant(PlantType.RED);
		registerBlock(crystalTreePlantRed, "redcrystaltreeplant");

		crystalTreePlantGreen = new BlockCrystalTreePlant(PlantType.GREEN);
		registerBlock(crystalTreePlantGreen, "greencrystaltreeplant");

		crystalTreePlantDark = new BlockCrystalTreePlant(PlantType.DARK);
		registerBlock(crystalTreePlantDark, "darkcrystaltreeplant");
		
		crystalBush = new BlockCrystalBerryBush();
		registerBlock(crystalBush, new ItemBlockMeta(crystalBush), "crystalbush");
		ItemBlockMeta.setMappingProperty(crystalBush, BlockCrystalBerryBush.TYPE);
		
		glowBerryBlue = new BlockGlowBerry(PlantType.BLUE);
		registerBlock(glowBerryBlue, "blueglowberry");
		glowBerryRed = new BlockGlowBerry(PlantType.RED);
		registerBlock(glowBerryRed, "redglowberry");
		glowBerryGreen = new BlockGlowBerry(PlantType.GREEN);
		registerBlock(glowBerryGreen, "greenglowberry");
		glowBerryDark = new BlockGlowBerry(PlantType.DARK);
		registerBlock(glowBerryDark, "darkglowberry");

		crystalLog = new BlockCrystalLog();
		registerBlock(crystalLog, new ItemBlockMeta(crystalLog), "crystallog");
		ItemBlockMeta.setMappingProperty(crystalLog, BlockCrystalLog.VARIANT);

		crystalLeaves = new BlockCrystalLeaves();
		registerBlock(crystalLeaves, new ItemBlockMeta(crystalLeaves) {
			@Override
			public int getMetadata(int m) {
				return m | 4;
			}
		}, "crystalleaves");
		ItemBlockMeta.setMappingProperty(crystalLeaves, BlockCrystalLeaves.VARIANT);

		crystalSapling = new BlockCrystalSapling();
		registerBlock(crystalSapling, new ItemBlockMeta(crystalSapling), "crystalsapling");
		ItemBlockMeta.setMappingProperty(crystalSapling, BlockCrystalSapling.VARIANT);

		crystalPlanks = registerEnumBlock(new BlockCrystalPlanks(), "crystalplanks");

		materialCrop = new BlockMaterialCrop();
		registerBlock(materialCrop, "materialcrop");
		registerTileEntity(TileMaterialCrop.class);
		
		corn = registerBlock(new BlockCorn(), "corn");
		
		bamboo = new BlockBamboo();
		registerBlock(bamboo, "bamboo");
		
		bambooLeaves = new BlockBambooLeaves();
		registerBlock(bambooLeaves, new ItemBlock(bambooLeaves) {
			@Override
			public int getMetadata(int m) {
				return m | 4;
			}
		}, "bambooleaves");
		
		bambooPlanks = (new Block(Material.WOOD){
			public SoundType getSoundType(){
				return SoundType.WOOD;
			}
		}).setHardness(2.0F).setResistance(5.0F).setCreativeTab(CrystalMod.tabBlocks);
		registerBlock(bambooPlanks, "bambooplanks");
		
		normalSapling = new BlockNormalSapling();
		registerBlock(normalSapling, new ItemBlockMeta(normalSapling), "sapling");
		ItemBlockMeta.setMappingProperty(normalSapling, BlockNormalSapling.VARIANT);
		
		fancyPumpkin = new BlockFancyPumpkin();
		registerBlock(fancyPumpkin, new ItemBlockMeta(fancyPumpkin), "fancypumpkin");
		ItemBlockMeta.setMappingProperty(fancyPumpkin, BlockFancyPumpkin.TYPE);
		
		fancyPumpkinLit = (BlockFancyPumpkin) new BlockFancyPumpkin().setLightLevel(1.0F);
		registerBlock(fancyPumpkinLit, new ItemBlockMeta(fancyPumpkinLit), "fancypumpkinlit");
		ItemBlockMeta.setMappingProperty(fancyPumpkinLit, BlockFancyPumpkin.TYPE);
		
		crysineMushroom = registerBlock(new BlockCrysineMushroom(), "crysinemushroom");
		crysineMushroomBlock = (BlockHugeCrysineMushroom) new BlockHugeCrysineMushroom(Material.WOOD, MapColor.PURPLE, crysineMushroom).setHardness(0.2f);
		registerBlock(crysineMushroomBlock, "crysinemushroomblock");
		
		ladder = new BlockCrystalLadder();
		registerBlock(ladder, new ItemBlockCrystalLadder(ladder), "crystalladder");		
		fancyLadders = new BlockFancyLadder();
		registerBlock(fancyLadders, new ItemBlockFancyLadders(fancyLadders), "fancyladder");	
		fancyLadders2 = new BlockFancyLadder2();
		registerBlock(fancyLadders2, new ItemBlockFancyLadders2(fancyLadders2), "fancyladder2");
		
		crystalPipe = new BlockPipe();
		registerBlock(crystalPipe, new ItemBlockPipe(crystalPipe), "crystalpipe");
		for (PipeType type : PipeType.values()) {
			registerTileEntity(type.clazz);
		}

		crystalTank = new BlockTank();
		registerBlock(crystalTank, new ItemBlockTank(crystalTank), "crystaltank");
		ItemBlockMeta.setMappingProperty(crystalTank, BlockTank.TYPE);
		registerTileEntity(TileEntityTank.class);

		compressed = registerEnumBlock(new BlockCompressed(), "compressedblock");
		fallingCompressed = new BlockFallingCompressed();
		registerBlock(fallingCompressed, new ItemBlockMeta(fallingCompressed), "fallingcompressedblock");
		ItemBlockMeta.setMappingProperty(fallingCompressed, BlockFallingCompressed.TYPE);
		
		blazeRodBlock = registerBlock(new BlockBlazeRod(), "blazerodblock");
		GameRegistry.registerFuelHandler(new IFuelHandler() {

			@Override
			public int getBurnTime(ItemStack fuel) {
				if (fuel.getItem() == Item.getItemFromBlock(ModBlocks.compressed)) {
					int meta = fuel.getMetadata();
					if (meta == CompressedBlockType.CHARCOAL.getMeta()) {
						return 16000;
					}
				}
				if (fuel.getItem() == Item.getItemFromBlock(ModBlocks.blazeRodBlock))
					return 2400 * 9;
				return 0;
			}

		});

		tileBasic = registerEnumBlock(new BlockBasicTiles(), "tiles_basic");		
		tileBasic2 = registerEnumBlock(new BlockBasicTiles2(), "tiles_basic2");		
		tileCrystal = registerEnumBlock(new BlockCrystalTiles(), "tiles_crystal");		
		GameRegistry.registerFuelHandler(new IFuelHandler() {

			@Override
			public int getBurnTime(ItemStack fuel) {
				if (fuel.getItem() == Item.getItemFromBlock(ModBlocks.tileBasic2)) {
					int meta = fuel.getMetadata();
					if (meta == BasicTileType2.LOG_OAK.getMeta() 
							|| meta == BasicTileType2.LOG_SPRUCE.getMeta() 
							|| meta == BasicTileType2.LOG_BIRCH.getMeta()
							|| meta == BasicTileType2.LOG_JUNGLE.getMeta()
							|| meta == BasicTileType2.LOG_ACACIA.getMeta()
							|| meta == BasicTileType2.LOG_DARK_OAK.getMeta()) {
						return 300 * 4;
					}
				}
				return 0;
			}

		});
		crystalLight = registerEnumBlock(new BlockCrystalLight(), "crystallightblock");		
		failureBlock = registerBlock(new BlockFailure(), "failureblock");	
		octagonBricks = registerEnumBlock(new BlockOctagonalBricks(), "octagonbricks");	

		engine = new BlockEngine();
		registerBlock(engine, new ItemBlockEngine(engine), "engine");
		for (EngineType type : EngineType.values()) {
			registerTileEntity(type.clazz);
		}

		specialengine = new BlockSpecialEngine();
		registerEnumBlock(specialengine, "specialengine");
		for (SpecialEngineType type : SpecialEngineType.values()) {
			registerTileEntity(type.clazz);
		}

		weather = (BlockBasicTile) new BlockBasicTile(TileEntityWeather.class, Material.IRON).setHardness(1.0F);
		registerBlock(weather, "weather");
		registerTileEntity(TileEntityWeather.class);

		redstoneReactor = new BlockRedstoneReactor();
		registerBlock(redstoneReactor, "redstonereactor");
		registerTileEntity(TileRedstoneReactor.class);

		redstoneCore = new BlockRedstoneCore();
		registerBlock(redstoneCore, "redstonecore");

		redstoneSponge = new BlockCongealedSponge();
		registerBlock(redstoneSponge, "congealedredstonesponge");

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
		for (PanelType type : PanelType.values()) {
			registerTileEntity(type.clazz);
		}

		wirelessPanel = new BlockWirelessPanel();
		registerBlock(wirelessPanel, "ewirelesspanel");
		registerTileEntity(TileEntityWirelessPanel.class);

		wirelessPipe = new BlockWirelessPipeWrapper();
		registerBlock(wirelessPipe, "pipewrapper");
		registerTileEntity(TileEntityPipeWrapper.class);

		craftingController = new BlockCraftingController();
		registerBlock(craftingController, "craftingcontroller");
		registerTileEntity(TileCraftingController.class);

		powerCore = new BlockNetworkPowerCore();
		registerBlock(powerCore, "powercore");
		registerTileEntity(TileNetworkPowerCore.class);

		crafter = new BlockCrafter();
		registerBlock(crafter, "autocrafter");
		registerTileEntity(TileCrafter.class);

		stocker = registerBlock(new BlockStocker(), "stocker");
		registerTileEntity(TileEntityStocker.class);

		encoder = new BlockPatternEncoder();
		registerEnumBlock(encoder, "craftingencoder");
		registerTileEntity(TilePatternEncoder.class);
		registerTileEntity(TileProcessingPatternEncoder.class);

		securityController = new BlockSecurityController();
		registerBlock(securityController, "securitycontroller");
		registerTileEntity(TileSecurityController.class);

		securityEncoder = new BlockSecurityEncoder();
		registerBlock(securityEncoder, "securityencoder");
		registerTileEntity(TileSecurityEncoder.class);

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

		telePortal = new BlockTelePortal();
		registerBlock(telePortal, "teleportal");
		registerTileEntity(TileTelePortal.class);

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
		for (MachineType type : MachineType.values()) {
			registerTileEntity(type.clazz);
		}

		sapExtractor = new BlockSapExtractor();
		registerBlock(sapExtractor, "sapextractor");
		registerTileEntity(TileSapExtractor.class);

		dnaMachine = new BlockDNAMachine();
		registerBlock(dnaMachine, "dnamachine");
		registerTileEntity(TileEntityDNAMachine.class);		
		
		enderBuffer = new BlockEnderBuffer();
		registerBlock(enderBuffer, "enderbuffer");
		registerTileEntity(TileEntityEnderBuffer.class);

		worksite = new BlockWorksite();
		registerBlock(worksite, new ItemBlockWorksite(worksite), "worksite");
		for (WorksiteType type : WorksiteType.values()) {
			registerTileEntity(type.clazz);
		}

		converter = new BlockPowerConverter();
		registerEnumBlock(converter, "powerconverter");
		registerTileEntity(TileEnergyConverterRFtoCU.class, TileEnergyConverterCUtoRF.class);

		advDispenser = new BlockAdvDispenser();
		registerBlock(advDispenser, "advdispenser");
		registerTileEntity(TileAdvDispenser.class);
		
		obsidianDispenser = new BlockObsidianDispenser();
		registerBlock(obsidianDispenser, "obsidiandispenser");
		registerTileEntity(TileObsidianDispenser.class);
		
		epst = new BlockEPST();
		registerBlock(epst, "epst");
		registerTileEntity(TileEPST.class);

		customSpawner = new BlockCustomSpawner();
		registerBlock(customSpawner, "customspawner");
		registerTileEntity(TileEntityCustomSpawner.class);

		enderTorch = registerBlock(new BlockEnderTorch(), "endertorch");
		registerTileEntity(TileEnderTorch.class);

		advancedLamp = new BlockAdvancedLamp();
		registerEnumBlock(advancedLamp, "advancedlamp");
		registerTileEntity(TileAdvancedLamp.class, TileAdvancedLampDark.class);

		fakeLight = new BlockFakeLight();
		registerEnumBlock(fakeLight, "fakelight");

		mobGrinder = registerBlock(new BlockMobGrinder(), "mobgrinder");
		registerTileEntity(TileEntityMobGrinder.class);

		entityHopper = registerBlock(new BlockEntityHopper(), "entityhopper");
		registerTileEntity(TileEntityEntityHopper.class);
		
		xpVacuum = (BlockBasicTile)registerBlock(new BlockBasicTile(TileEntityXPVacuum.class, Material.IRON).setHardness(1.5F), "xpvacuum");
		registerTileEntity(TileEntityXPVacuum.class);
		xpFountain = registerBlock(new BlockXPFountain(), "xpfountain");
		registerTileEntity(TileEntityXPFountain.class);
		xpTank = registerBlock(new BlockXPTank(), "xptank");
		registerTileEntity(TileEntityXPTank.class);
		seismicScanner = (BlockBasicTile)registerBlock(new BlockBasicTile(TileEntitySeismicScanner.class, Material.IRON).setHardness(1.5F), "seismic_scanner");
		registerTileEntity(TileEntitySeismicScanner.class);

		crates = new BlockCrate();
		registerEnumBlock(crates, "crate");
		registerTileEntity(TileCrate.class);

		darkIronRail = new BlockReinforcedRail();
		registerBlock(darkIronRail, "reinforcedrail");

		bridge = new BlockBridge();
		registerEnumBlock(bridge, "bridge");
		registerTileEntity(TileBridge.class);

		jar = new BlockJar();
		registerEnumBlock(jar, "jar");
		registerTileEntity(TileJar.class);

		shieldRack = new BlockShieldRack();
		registerEnumBlock(shieldRack, new ItemBlockShieldRack(shieldRack), "shieldrack");
		registerTileEntity(TileShieldRack.class);

		muffler = (BlockBasicTile)new BlockBasicTile(TileSoundMuffler.class, Material.IRON).setHardness(2F);
		registerBlock(muffler, "soundmuffler");
		registerTileEntity(TileSoundMuffler.class);
		
		bambooDoor = (BlockCustomDoor)new BlockCustomDoor(Material.WOOD, SoundType.WOOD, ModItems.bambooDoor).setHardness(3.0F).setCreativeTab(null);
		registerBlock(bambooDoor, "bamboodoor");
		
		flumeRailBasic = (BlockFlumeRailBasic) new BlockFlumeRailBasic().setHardness(0.7F);
		registerBlock(flumeRailBasic, new ItemBlockWater(flumeRailBasic), "flumerailbasic");
		flumeRailHolding = (BlockFlumeRailHolding) new BlockFlumeRailHolding().setHardness(0.7F);
		registerBlock(flumeRailHolding, new ItemBlockWater(flumeRailHolding), "flumerailholding");
		flumeRailBooster = (BlockFlumeRailBooster) new BlockFlumeRailBooster().setHardness(0.7F);
		registerBlock(flumeRailBooster, new ItemBlockWater(flumeRailBooster), "flumerailbooster");
		flumeRailDetector = (BlockFlumeRailDetector) new BlockFlumeRailDetector().setHardness(0.7F);
		registerBlock(flumeRailDetector, new ItemBlockWater(flumeRailDetector), "flumeraildetector");
		flumeRailEntrance = (BlockFlumeRailEntrance) new BlockFlumeRailEntrance().setHardness(0.7F);
		registerBlock(flumeRailEntrance, new ItemBlockWater(flumeRailEntrance), "flumerailentrance");
		
		flumeRailBasicGround = (BlockFlumeRailBasicGround) new BlockFlumeRailBasicGround().setHardness(0.7F);
		registerBlock(flumeRailBasicGround, "flumerailbasic_ground");
		flumeRailHoldingGround = (BlockFlumeRailHoldingGround) new BlockFlumeRailHoldingGround().setHardness(0.7F);
		registerBlock(flumeRailHoldingGround, "flumerailholding_ground");
		flumeRailBoosterGround = (BlockFlumeRailBoosterGround) new BlockFlumeRailBoosterGround().setHardness(0.7F);
		registerBlock(flumeRailBoosterGround, "flumerailbooster_ground");
		flumeRailRamp = (BlockFlumeRailAscending) new BlockFlumeRailAscending().setHardness(0.7F);
		registerBlock(flumeRailRamp, "flumerailramp");

		particleThrower = new BlockParticleThrower();
		registerBlock(particleThrower, "particlethrower");
		registerTileEntity(TileParticleThrower.class);

		remover = new BlockRemoverExplosion();
		registerEnumBlock(remover, "removerexplosion");
		registerTileEntity(TileRemoverExplosion.class);

		oppositeFuser = new BlockOppositeFuser();
		registerBlock(oppositeFuser, new ItemBlockMeta(oppositeFuser), "oppositefuser");
		ItemBlockMeta.setMappingProperty(oppositeFuser, BlockOppositeFuser.TIER);
		registerTileEntity(TileOppositeFuser.class, TileOppositeFuserTier2.class, TileOppositeFuserTier3.class);

		darkInfection = new BlockDarkInfection();
		registerBlock(darkInfection, "darkinfection");
		registerTileEntity(TileDarkInfection.class);

		infectedBlock = registerEnumBlock(new BlockInfected(), "infectedblock");
		denseDarkness = registerBlock(new BlockDenseDarkness(), "densedarkness");

		pedistal = new BlockPedistal();
		registerBlock(pedistal, new ItemBlockPedistal(pedistal), "pedistal");
		registerTileEntity(TilePedistal.class);

		fusionPedistal = new BlockFusionPedistal();
		registerBlock(fusionPedistal, new ItemBlockPedistal(fusionPedistal), "fusionpedistal");
		registerTileEntity(TileFusionPedistal.class);

		crystalCluster = new BlockCrystalCluster();
		registerEnumBlock(crystalCluster, new ItemBlockFacing(crystalCluster), "crystalcluster");
		registerTileEntity(TileCrystalCluster.class);

		kelp = new BlockKelp();
		registerBlock(kelp, new ItemBlockKelp(kelp), "kelp");
		seaweed = registerBlock(new BlockSeaweed(), "seaweed");
		coral = new BlockCoral();
		registerBlock(coral, new ItemCloth(coral), "coral");
		
		roseBush = new BlockBetterRoses();
		registerBlock(roseBush, new ItemBlockBetterRose(roseBush), "rosebush");
		ItemBlockMeta.setMappingProperty(roseBush, BlockBetterRoses.COLOR);
		
		crystexiumBlock = new CrystexiumBlock();
		registerEnumBlock(crystexiumBlock, "crystexiumblock");
		blueCrystexiumBlock = new CrystexiumBlock();
		registerEnumBlock(blueCrystexiumBlock, "bluecrystexiumblock");
		redCrystexiumBlock = new CrystexiumBlock();
		registerEnumBlock(redCrystexiumBlock, "redcrystexiumblock");
		greenCrystexiumBlock = new CrystexiumBlock();
		registerEnumBlock(greenCrystexiumBlock, "greencrystexiumblock");
		darkCrystexiumBlock = new CrystexiumBlock();
		registerEnumBlock(darkCrystexiumBlock, "darkcrystexiumblock");
		pureCrystexiumBlock = new CrystexiumBlock();
		registerEnumBlock(pureCrystexiumBlock, "purecrystexiumblock");
		
		crystexiumSlab = new CrystexiumSlab();
		registerBlock(crystexiumSlab, new ItemBlockMeta(crystexiumSlab), "crystexiumslab");
		ItemBlockMeta.setMappingProperty(crystexiumSlab, CrystexiumSlab.VARIANT);
		
		crysidian = registerBlock(new BlockCrysidian(), "crysidian");
		crystexPortal = registerBlock(new BlockCrystexPortal(), "crystexportal");
		crystheriumPlant = new BlockCrystheriumPlant();
		registerBlock(crystheriumPlant, new ItemBlockCrystheriumPlant(crystheriumPlant), "crystheriumplant");
		ItemBlockMeta.setMappingProperty(crystheriumPlant, BlockCrystheriumPlant.TYPE);
		
		crystexiumStairs = new BlockCustomStairs(crystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.BRICK));
		registerBlock(crystexiumStairs, "crystexiumstairs");
		blueCrystexiumStairs = new BlockCustomStairs(blueCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.BRICK));
		registerBlock(blueCrystexiumStairs, "bluecrystexiumstairs");
		redCrystexiumStairs = new BlockCustomStairs(redCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.BRICK));
		registerBlock(redCrystexiumStairs, "redcrystexiumstairs");
		greenCrystexiumStairs = new BlockCustomStairs(greenCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.BRICK));
		registerBlock(greenCrystexiumStairs, "greencrystexiumstairs");
		darkCrystexiumStairs = new BlockCustomStairs(darkCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.BRICK));
		registerBlock(darkCrystexiumStairs, "darkcrystexiumstairs");
		pureCrystexiumStairs = new BlockCustomStairs(pureCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.BRICK));
		registerBlock(pureCrystexiumStairs, "purecrystexiumstairs");
	}

	@SideOnly(Side.CLIENT)
	public static void initClient() {
		for (Block block : REGISTRY.values()) {
			if (block instanceof ICustomModel) {
				((ICustomModel) block).initModel();
			} else {
				initBasicModel(block);
			}
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new TileEntityPipeRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalCauldron.class, new RenderTileCrystalCauldron());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHDDInterface.class, new TileEntityHDDInterfaceRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileHDDArray.class, new TileHDDArrayRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPanelItem.class,
				new TileEntityPanelItemRenderer<TileEntityPanelItem>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCustomSpawner.class,
				new RenderTileEntityCustomSpawner<TileEntityCustomSpawner>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlayerCubePortal.class,
				new TileEntityPlayerCubePortalRenderer<TileEntityPlayerCubePortal>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBattery.class, new TileEntityBatteryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevator.class, new TileEntityElevatorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorCaller.class,
				new TileEntityElevatorCallerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWorksiteBase.class,
				new TileWorksiteRenderer<TileWorksiteBase>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMaterialCrop.class,
				new RenderTileMaterialCrop<TileMaterialCrop>());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePedistal.class, new RenderTilePedistal<TilePedistal>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFusionPedistal.class,
				new RenderTileFusionPedistal<TileFusionPedistal>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrate.class, new RenderTileCrate<TileCrate>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCasePiston.class, new RenderTileEntityCasePiston());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityXPTank.class, new RenderTileEntityXPTank());
	}

	@SideOnly(Side.CLIENT)
	public static void initBasicModel(Block block) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void initBasicModel(Block block, int meta) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta,
				new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}

	protected static <T extends EnumBlock<?>, I extends ItemBlockMeta> T registerEnumBlock(T block, I itemblock,
			String name) {
		registerBlock(block, itemblock, name);
		ItemBlockMeta.setMappingProperty(block, block.prop);
		return block;
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
		String finalName = name;
		String lowerCase = name.toLowerCase();
		if (name != lowerCase) {
			ModLogger.warning("Registering a Block and Item that has a non-lowercase registry name! (" + name + " vs. "
					+ lowerCase + ") setting it to " + lowerCase);
			finalName = lowerCase;

			ModContainer mc = Loader.instance().activeModContainer();
			String prefix = mc == null || (mc instanceof InjectedModContainer
					&& ((InjectedModContainer) mc).wrappedContainer instanceof FMLContainer) ? "minecraft"
							: mc.getModId().toLowerCase();
			MissingItemHandler.remapItems.put(new ResourceLocation(prefix, name), itemBlock);
			MissingItemHandler.remapBlocks.put(new ResourceLocation(prefix, name), block);
		}

		block.setUnlocalizedName(CrystalMod.prefix(finalName));
		block.setRegistryName(CrystalMod.resource(finalName));
		GameRegistry.register(block);
		GameRegistry.register(itemBlock.setRegistryName(CrystalMod.resource(finalName)));
		REGISTRY.put(finalName, block);
		return block;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static <T extends TileEntity> void registerTileEntity(Class... clazzs) {
		for (Class clazz : clazzs) {
			GameRegistry.registerTileEntity(clazz, CrystalMod.MODID + "." + clazz.getName());
		}
	}

}
