package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot;
import alec_wam.CrystalMod.blocks.BlockCrystalLeaves;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.blocks.BlockCrystalOre;
import alec_wam.CrystalMod.blocks.BlockCrystalSapling;
import alec_wam.CrystalMod.blocks.BlockCrystalShard;
import alec_wam.CrystalMod.blocks.BlockIngot;
import alec_wam.CrystalMod.blocks.BlockVariant;
import alec_wam.CrystalMod.blocks.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.blocks.plants.BlockCrystalBerryBush;
import alec_wam.CrystalMod.blocks.plants.BlockFlowerLilyPad;
import alec_wam.CrystalMod.blocks.plants.BlockReedVariant;
import alec_wam.CrystalMod.blocks.plants.BlockTallFlowerVariant;
import alec_wam.CrystalMod.blocks.plants.EnumBetterRoses;
import alec_wam.CrystalMod.blocks.plants.ItemBlockWaterPlant;
import alec_wam.CrystalMod.client.CustomItemRender;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.BlockVariantGroup.TileFactory;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.chests.metal.BlockMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.metal.MetalCrystalChestType;
import alec_wam.CrystalMod.tiles.chests.metal.TileEntityMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wireless.BlockWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wireless.TileEntityWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wooden.BlockWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wooden.TileEntityWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wooden.WoodenCrystalChestType;
import alec_wam.CrystalMod.tiles.crate.BlockCrate;
import alec_wam.CrystalMod.tiles.crate.TileEntityCrate;
import alec_wam.CrystalMod.tiles.energy.battery.BlockBattery;
import alec_wam.CrystalMod.tiles.energy.battery.ItemBlockBattery;
import alec_wam.CrystalMod.tiles.energy.battery.TileEntityBattery;
import alec_wam.CrystalMod.tiles.energy.engine.BlockEngine;
import alec_wam.CrystalMod.tiles.energy.engine.EnumEngineType;
import alec_wam.CrystalMod.tiles.energy.engine.TileEntityEngineBase;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.TileEntityEngineFurnace;
import alec_wam.CrystalMod.tiles.fusion.BlockFusionPedestal;
import alec_wam.CrystalMod.tiles.fusion.BlockPedestal;
import alec_wam.CrystalMod.tiles.fusion.TileEntityFusionPedestal;
import alec_wam.CrystalMod.tiles.fusion.TileEntityPedestal;
import alec_wam.CrystalMod.tiles.jar.BlockJar;
import alec_wam.CrystalMod.tiles.jar.TileEntityJar;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.EnumCraftingMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.TileEntityPoweredFurnace;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.TileEntityGrinder;
import alec_wam.CrystalMod.tiles.machine.crafting.press.TileEntityPress;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe;
import alec_wam.CrystalMod.tiles.pipes.NetworkType;
import alec_wam.CrystalMod.tiles.pipes.energy.cu.BlockPipeEnergyCU;
import alec_wam.CrystalMod.tiles.pipes.energy.cu.TileEntityPipeEnergyCU;
import alec_wam.CrystalMod.tiles.pipes.energy.rf.BlockPipeEnergyRF;
import alec_wam.CrystalMod.tiles.pipes.energy.rf.TileEntityPipeEnergyRF;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem;
import alec_wam.CrystalMod.tiles.tank.BlockTank;
import alec_wam.CrystalMod.tiles.tank.TileEntityTank;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CrystalMod.MODID)
public class ModBlocks {
	
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockVariant<EnumCrystalColorSpecial>> crystalBlockGroup;
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalOre> crystalOreGroup;
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalOre> crystalOreNetherGroup;
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalOre> crystalOreEndGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockVariant<EnumCrystalColorSpecial>> crystalStoneBrickGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalIngot> crystalIngotBlockGroup;
	public static BlockIngot darkIronIngotBlock;
	
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalSapling> crystalSaplingGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLog> crystalLogGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLeaves> crystalLeavesGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockVariant<EnumCrystalColorSpecial>> crystalPlanksGroup;	
	public static BlockVariantGroup<EnumCrystalColor, BlockReedVariant<EnumCrystalColor>> crystalReedGroup;	
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalShard> crystalShardBlock;	
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalBerryBush> crystalBerryBushGroup;	
	
	public static BlockVariantGroup<EnumBetterRoses, BlockTallFlowerVariant<EnumBetterRoses>> betterRosesGroup;	
	public static BlockFlowerLilyPad flowerLilypad;

	public static BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockCrate> crateGroup;
	public static final TileEntityType<TileEntityCrate> TILE_CRATE = TileEntityType.register(CrystalMod.resource("crate"), TileEntityType.Builder.create(TileEntityCrate::new));
	public static BlockVariantGroup<WoodenCrystalChestType, BlockWoodenCrystalChest> woodenChestGroup;
	public static final TileEntityType<TileEntityWoodenCrystalChest> TILE_WOODEN_CHEST = TileEntityType.register(CrystalMod.resource("wooden_chest"), TileEntityType.Builder.create(TileEntityWoodenCrystalChest::new));
	public static BlockVariantGroup<MetalCrystalChestType, BlockMetalCrystalChest> metalChestGroup;
	public static final TileEntityType<TileEntityMetalCrystalChest> TILE_METAL_CHEST = TileEntityType.register(CrystalMod.resource("metal_chest"), TileEntityType.Builder.create(TileEntityMetalCrystalChest::new));
	public static BlockWirelessChest wirelessChest;
	public static final TileEntityType<TileEntityWirelessChest> TILE_WIRELESS_CHEST = TileEntityType.register(CrystalMod.resource("wireless_chest"), TileEntityType.Builder.create(TileEntityWirelessChest::new));
	public static BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockTank> tankGroup;
	public static final TileEntityType<TileEntityTank> TILE_TANK = TileEntityType.register(CrystalMod.resource("tank"), TileEntityType.Builder.create(TileEntityTank::new));
	
	public static BlockVariantGroup<WoodType, BlockJar> jarGroup;
	public static final TileEntityType<TileEntityJar> TILE_JAR = TileEntityType.register(CrystalMod.resource("jar"), TileEntityType.Builder.create(TileEntityJar::new));

	public static BlockPedestal pedestal;
	public static final TileEntityType<TileEntityPedestal> TILE_PEDESTAL = TileEntityType.register(CrystalMod.resource("pedestal"), TileEntityType.Builder.create(TileEntityPedestal::new));
	public static BlockFusionPedestal fusionPedestal;
	public static final TileEntityType<TileEntityFusionPedestal> TILE_FUSION_PEDESTAL = TileEntityType.register(CrystalMod.resource("fusion_pedestal"), TileEntityType.Builder.create(TileEntityFusionPedestal::new));
	
	public static BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockBattery> batteryGroup;
	public static final TileEntityType<TileEntityBattery> TILE_BATTERY = TileEntityType.register(CrystalMod.resource("battery"), TileEntityType.Builder.create(TileEntityBattery::new));
	public static BlockVariantGroup<EnumEngineType, BlockEngine> engineBasicGroup;
	public static final TileEntityType<TileEntityEngineFurnace> TILE_ENGINE_FURNACE = TileEntityType.register(CrystalMod.resource("engine_furnace"), TileEntityType.Builder.create(TileEntityEngineFurnace::new));
	public static BlockVariantGroup<EnumCraftingMachine, BlockCraftingMachine> craftingMachine;
	public static final TileEntityType<TileEntityPoweredFurnace> TILE_MACHINE_FURNACE = TileEntityType.register(CrystalMod.resource("machine_furnace"), TileEntityType.Builder.create(TileEntityPoweredFurnace::new));
	public static final TileEntityType<TileEntityGrinder> TILE_MACHINE_GRINDER = TileEntityType.register(CrystalMod.resource("machine_grinder"), TileEntityType.Builder.create(TileEntityGrinder::new));
	public static final TileEntityType<TileEntityPress> TILE_MACHINE_PRESS = TileEntityType.register(CrystalMod.resource("machine_press"), TileEntityType.Builder.create(TileEntityPress::new));

	public static BlockPipe pipeItem;
	public static final TileEntityType<TileEntityPipeItem> TILE_PIPE_ITEM = TileEntityType.register(CrystalMod.resource("pipe_item"), TileEntityType.Builder.create(TileEntityPipeItem::new));
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockPipeEnergyCU> pipeEnergyCUGroup;
	public static final TileEntityType<TileEntityPipeEnergyCU> TILE_PIPE_ENERGY_CU = TileEntityType.register(CrystalMod.resource("pipe_energy_cu"), TileEntityType.Builder.create(TileEntityPipeEnergyCU::new));
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockPipeEnergyRF> pipeEnergyRFGroup;
	public static final TileEntityType<TileEntityPipeEnergyRF> TILE_PIPE_ENERGY_RF = TileEntityType.register(CrystalMod.resource("pipe_energy_rf"), TileEntityType.Builder.create(TileEntityPipeEnergyRF::new));

	public static void buildList(){
		//Crystal Stuff
		crystalBlockGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystalblock")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F))
				.blockFactory(BlockVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalBlockGroup);
		crystalOreGroup = BlockVariantGroup.Builder.<EnumCrystalColor, BlockCrystalOre>create()
				.groupName("crystalore")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F))
				.blockFactory(BlockCrystalOre::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalOreGroup);
		crystalOreNetherGroup = BlockVariantGroup.Builder.<EnumCrystalColor, BlockCrystalOre>create()
				.groupName("crystalore_nether")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F))
				.blockFactory(BlockCrystalOre::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalOreNetherGroup);
		crystalOreEndGroup = BlockVariantGroup.Builder.<EnumCrystalColor, BlockCrystalOre>create()
				.groupName("crystalore_end")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F))
				.blockFactory(BlockCrystalOre::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalOreEndGroup);
		crystalStoneBrickGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystal_stonebrick")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F))
				.blockFactory(BlockVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalStoneBrickGroup);
		crystalIngotBlockGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockCrystalIngot>create()
				.groupName("crystalingotblock")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL))
				.blockFactory(BlockCrystalIngot::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalIngotBlockGroup);
		darkIronIngotBlock = new BlockIngot(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL));
		RegistrationHandler.createBlock(darkIronIngotBlock, ModItemGroups.ITEM_GROUP_BLOCKS, "darkiron_block");
		
		//Wood Stuff
		crystalSaplingGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockCrystalSapling>create()
				.groupName("crystalsapling")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().hardnessAndResistance(0.0F).sound(SoundType.PLANT))
				.blockFactory(BlockCrystalSapling::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalSaplingGroup);
		crystalLogGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockCrystalLog>create()
				.groupName("crystallog")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.WOOD, MaterialColor.BLUE).hardnessAndResistance(2.0F).sound(SoundType.WOOD))
				.blockFactory(BlockCrystalLog::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalLogGroup);
		crystalLeavesGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockCrystalLeaves>create()
				.groupName("crystalleaves")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).needsRandomTick().sound(SoundType.PLANT))
				.blockFactory(BlockCrystalLeaves::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalLeavesGroup);
		crystalPlanksGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystalplanks")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD))
				.blockFactory(BlockVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalPlanksGroup);
		
		//Plants
		crystalReedGroup = BlockVariantGroup.Builder.<EnumCrystalColor, BlockReedVariant<EnumCrystalColor>>create()
				.groupName("crystalreeds")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.PLANTS).doesNotBlockMovement().needsRandomTick().hardnessAndResistance(0.0F).sound(SoundType.PLANT))
				.blockFactory(BlockReedVariant<EnumCrystalColor>::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalReedGroup);
		betterRosesGroup = BlockVariantGroup.Builder.<EnumBetterRoses, BlockTallFlowerVariant<EnumBetterRoses>>create()
				.groupName("rosebush")
				.suffix()
				.variants(EnumBetterRoses.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.PLANT))
				.blockFactory(BlockTallFlowerVariant<EnumBetterRoses>::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ItemGroup.DECORATIONS))
				.build();
		RegistrationHandler.addBlockGroup(betterRosesGroup);
		flowerLilypad = new BlockFlowerLilyPad(Block.Properties.create(Material.PLANTS).needsRandomTick().hardnessAndResistance(0.0F).sound(SoundType.PLANT)); 
		RegistrationHandler.createBlock(flowerLilypad, new ItemBlockWaterPlant(flowerLilypad, (new Item.Properties()).group(ItemGroup.DECORATIONS)), "flowerlilypad");
		
		crystalShardBlock = BlockVariantGroup.Builder.<EnumCrystalColor, BlockCrystalShard>create()
				.groupName("crystalshardblock")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.GLASS).needsRandomTick().hardnessAndResistance(0.8F).sound(SoundType.GLASS).lightValue(8))
				.blockFactory(BlockCrystalShard::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalShardBlock);
		crystalBerryBushGroup = BlockVariantGroup.Builder.<EnumCrystalColor, BlockCrystalBerryBush>create()
				.groupName("crystalbush")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.LEAVES).needsRandomTick().hardnessAndResistance(0.3F).sound(SoundType.PLANT))
				.blockFactory(BlockCrystalBerryBush::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalBerryBushGroup);
		
		//TILES
		crateGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecialWithCreative, BlockCrate>create()
				.groupName("crate")
				.suffix()
				.variants(EnumCrystalColorSpecialWithCreative.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.WOOD).hardnessAndResistance(3.0F, 5.0F).sound(SoundType.WOOD))
				.blockFactory(BlockCrate::new)
				.tileFactory(new TileFactory<EnumCrystalColorSpecialWithCreative>(){
					@Override
					public TileEntityCrate createTile(EnumCrystalColorSpecialWithCreative variant) {
						return new TileEntityCrate(variant);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(crateGroup);
		RegistrationHandler.addTile(TILE_CRATE);	
		woodenChestGroup = BlockVariantGroup.Builder.<WoodenCrystalChestType, BlockWoodenCrystalChest>create()
				.groupName("woodenchest")
				.suffix()
				.variants(WoodenCrystalChestType.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD))
				.blockFactory(BlockWoodenCrystalChest::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_BLOCKS).setTEISR(() -> CustomItemRender::new))
				.tileFactory(new TileFactory<WoodenCrystalChestType>(){
					@Override
					public TileEntityWoodenCrystalChest createTile(WoodenCrystalChestType variant) {
						return new TileEntityWoodenCrystalChest(variant);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(woodenChestGroup);
		RegistrationHandler.addTile(TILE_WOODEN_CHEST);		
		metalChestGroup = BlockVariantGroup.Builder.<MetalCrystalChestType, BlockMetalCrystalChest>create()
				.groupName("metalchest")
				.suffix()
				.variants(MetalCrystalChestType.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL))
				.blockFactory(BlockMetalCrystalChest::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_BLOCKS).setTEISR(() -> CustomItemRender::new))
				.tileFactory(new TileFactory<MetalCrystalChestType>(){
					@Override
					public TileEntityMetalCrystalChest createTile(MetalCrystalChestType variant) {
						return new TileEntityMetalCrystalChest(variant);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(metalChestGroup);
		RegistrationHandler.addTile(TILE_METAL_CHEST);				
		wirelessChest = new BlockWirelessChest(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)); 
		RegistrationHandler.createBlock(wirelessChest, new ItemBlock(wirelessChest, RegistrationHandler.defaultItemProperties(ModItemGroups.ITEM_GROUP_BLOCKS).setTEISR(() -> CustomItemRender::new)), "wirelesschest");	
		RegistrationHandler.addTile(TILE_WIRELESS_CHEST);
		tankGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecialWithCreative, BlockTank>create()
				.groupName("tank")
				.suffix()
				.variants(EnumCrystalColorSpecialWithCreative.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(1.5F, 15.0F).sound(SoundType.GLASS))
				.blockFactory(BlockTank::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_BLOCKS).setTEISR(() -> CustomItemRender::new))
				.tileFactory(new TileFactory<EnumCrystalColorSpecialWithCreative>(){
					@Override
					public TileEntityTank createTile(EnumCrystalColorSpecialWithCreative variant) {
						return new TileEntityTank(variant);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(tankGroup);
		RegistrationHandler.addTile(TILE_TANK);	
		
		jarGroup = BlockVariantGroup.Builder.<WoodType, BlockJar>create()
				.groupName("jar")
				.suffix()
				.variants(WoodType.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.GLASS).hardnessAndResistance(0.8F, 0.5F).sound(SoundType.GLASS))
				.blockFactory(BlockJar::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ItemGroup.BREWING).setTEISR(() -> CustomItemRender::new))
				.tileFactory(new TileFactory<WoodType>(){
					@Override
					public TileEntityJar createTile(WoodType variant) {
						return new TileEntityJar();
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(jarGroup);
		RegistrationHandler.addTile(TILE_JAR);		
		
		pedestal = new BlockPedestal(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.STONE)); 
		RegistrationHandler.createBlock(pedestal, ModItemGroups.ITEM_GROUP_BLOCKS, "pedestal");
		fusionPedestal = new BlockFusionPedestal(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.STONE)); 
		RegistrationHandler.createBlock(fusionPedestal, ModItemGroups.ITEM_GROUP_BLOCKS, "fusion_pedestal");
		
		batteryGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecialWithCreative, BlockBattery>create()
				.groupName("battery")
				.suffix()
				.variants(EnumCrystalColorSpecialWithCreative.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(20.0F, 50.0F).sound(SoundType.METAL))
				.blockFactory(BlockBattery::new)
				.itemFactory(ItemBlockBattery::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_BLOCKS).setTEISR(() -> CustomItemRender::new))
				.tileFactory(new TileFactory<EnumCrystalColorSpecialWithCreative>(){
					@Override
					public TileEntityBattery createTile(EnumCrystalColorSpecialWithCreative variant) {
						return new TileEntityBattery(variant);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(batteryGroup);
		RegistrationHandler.addTile(TILE_BATTERY);	
		engineBasicGroup = BlockVariantGroup.Builder.<EnumEngineType, BlockEngine>create()
				.groupName("engine_basic")
				.suffix()
				.variants(EnumEngineType.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 15.0F).sound(SoundType.STONE))
				.blockFactory(BlockEngine::new)
				.tileFactory(new TileFactory<EnumEngineType>(){
					@Override
					public TileEntityEngineBase createTile(EnumEngineType variant) {
						try {
							return (TileEntityEngineBase) variant.clazz.getConstructors()[1].newInstance(Integer.valueOf(1));
						} catch (Exception e) {
							e.printStackTrace();
							try {
								return variant.clazz.newInstance();
							} catch (Exception e1) {
								return null;
							}
						}							
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(engineBasicGroup);
		RegistrationHandler.addTile(TILE_ENGINE_FURNACE);	
		craftingMachine = BlockVariantGroup.Builder.<EnumCraftingMachine, BlockCraftingMachine>create()
				.groupName("machine")
				.suffix()
				.variants(EnumCraftingMachine.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 5.0F).sound(SoundType.METAL))
				.blockFactory(BlockCraftingMachine::new)
				.tileFactory(new TileFactory<EnumCraftingMachine>(){
					@Override
					public TileEntityMachine createTile(EnumCraftingMachine variant) {
						try {
							return variant.clazz.newInstance();
						} catch (Exception e1) {
							return null;
						}
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(craftingMachine);
		RegistrationHandler.addTile(TILE_MACHINE_FURNACE);	
		
		pipeItem = new BlockPipe(NetworkType.ITEM, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.METAL)); 
		RegistrationHandler.createBlock(pipeItem, ModItemGroups.ITEM_GROUP_BLOCKS, "pipe_item");
		RegistrationHandler.addTile(TILE_PIPE_ITEM);		
		pipeEnergyCUGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockPipeEnergyCU>create()
				.groupName("pipe_energy_cu")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.METAL))
				.blockFactory(BlockPipeEnergyCU::new)
				.build();
		RegistrationHandler.addBlockGroup(pipeEnergyCUGroup);
		RegistrationHandler.addTile(TILE_PIPE_ENERGY_CU);	
		pipeEnergyRFGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockPipeEnergyRF>create()
				.groupName("pipe_energy_rf")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.METAL))
				.blockFactory(BlockPipeEnergyRF::new)
				.build();
		RegistrationHandler.addBlockGroup(pipeEnergyRFGroup);
		RegistrationHandler.addTile(TILE_PIPE_ENERGY_RF);	
	}


	public static void addBlocksToTags() {
		/*Collection<Block> ores = Blocks.ORES.getAllElements();
		CrystalMod.LOGGER.info("Ores Size: " + ores.size());
		ores.addAll(crystalOreGroup.getBlocks());
		Collection<Block> ores2 = Blocks.ORES.getAllElements();
		CrystalMod.LOGGER.info("New Ores Size: " + ores2.size());*/
		//BlockTags.PLANKS.getAllElements().add(crystalPlanksGroup.getBlock(EnumCrystalColorSpecial.BLUE));
	}	
	
}
