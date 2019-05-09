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
import alec_wam.CrystalMod.blocks.plants.BlockFlowerLilyPad;
import alec_wam.CrystalMod.blocks.plants.BlockReedVariant;
import alec_wam.CrystalMod.blocks.plants.BlockTallFlowerVariant;
import alec_wam.CrystalMod.blocks.plants.EnumBetterRoses;
import alec_wam.CrystalMod.blocks.plants.ItemBlockWaterPlant;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.BlockVariantGroup.TileFactory;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.tiles.CustomItemRender;
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
import alec_wam.CrystalMod.tiles.energy.engine.BlockEngine;
import alec_wam.CrystalMod.tiles.energy.engine.EnumEngineType;
import alec_wam.CrystalMod.tiles.energy.engine.TileEntityEngineBase;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.TileEntityEngineFurnace;
import alec_wam.CrystalMod.tiles.fusion.BlockFusionPedistal;
import alec_wam.CrystalMod.tiles.fusion.BlockPedistal;
import alec_wam.CrystalMod.tiles.fusion.TileEntityFusionPedistal;
import alec_wam.CrystalMod.tiles.fusion.TileEntityPedistal;
import alec_wam.CrystalMod.tiles.jar.BlockJar;
import alec_wam.CrystalMod.tiles.jar.TileEntityJar;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe;
import alec_wam.CrystalMod.tiles.pipes.NetworkType;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem;
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
	
	public static BlockVariantGroup<EnumBetterRoses, BlockTallFlowerVariant<EnumBetterRoses>> betterRosesGroup;	
	public static BlockFlowerLilyPad flowerLilypad;

	public static BlockVariantGroup<EnumCrystalColor, BlockCrate> crateGroup;
	public static final TileEntityType<TileEntityCrate> TILE_CRATE = TileEntityType.register(CrystalMod.resource("crate"), TileEntityType.Builder.create(TileEntityCrate::new));
	public static BlockVariantGroup<WoodenCrystalChestType, BlockWoodenCrystalChest> woodenChestGroup;
	public static final TileEntityType<TileEntityWoodenCrystalChest> TILE_WOODEN_CHEST = TileEntityType.register(CrystalMod.resource("wooden_chest"), TileEntityType.Builder.create(TileEntityWoodenCrystalChest::new));
	public static BlockVariantGroup<MetalCrystalChestType, BlockMetalCrystalChest> metalChestGroup;
	public static final TileEntityType<TileEntityMetalCrystalChest> TILE_METAL_CHEST = TileEntityType.register(CrystalMod.resource("metal_chest"), TileEntityType.Builder.create(TileEntityMetalCrystalChest::new));
	public static BlockWirelessChest wirelessChest;
	public static final TileEntityType<TileEntityWirelessChest> TILE_WIRELESS_CHEST = TileEntityType.register(CrystalMod.resource("wireless_chest"), TileEntityType.Builder.create(TileEntityWirelessChest::new));
	
	public static BlockVariantGroup<WoodType, BlockJar> jarGroup;
	public static final TileEntityType<TileEntityJar> TILE_JAR = TileEntityType.register(CrystalMod.resource("jar"), TileEntityType.Builder.create(TileEntityJar::new));

	public static BlockPedistal pedistal;
	public static final TileEntityType<TileEntityPedistal> TILE_PEDISTAL = TileEntityType.register(CrystalMod.resource("pedistal"), TileEntityType.Builder.create(TileEntityPedistal::new));
	public static BlockFusionPedistal fusionPedistal;
	public static final TileEntityType<TileEntityFusionPedistal> TILE_FUSION_PEDISTAL = TileEntityType.register(CrystalMod.resource("fusion_pedistal"), TileEntityType.Builder.create(TileEntityFusionPedistal::new));
	
	public static BlockVariantGroup<EnumEngineType, BlockEngine> engineBasicGroup;
	public static final TileEntityType<TileEntityEngineFurnace> TILE_ENGINE_FURNACE = TileEntityType.register(CrystalMod.resource("engine_furnace"), TileEntityType.Builder.create(TileEntityEngineFurnace::new));

	public static BlockPipe pipeItem;
	public static final TileEntityType<TileEntityPipeItem> TILE_PIPE_ITEM = TileEntityType.register(CrystalMod.resource("pipe_item"), TileEntityType.Builder.create(TileEntityPipeItem::new));

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
		
		//TILES
		crateGroup = BlockVariantGroup.Builder.<EnumCrystalColor, BlockCrate>create()
				.groupName("crate")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.WOOD).hardnessAndResistance(3.0F, 5.0F).sound(SoundType.WOOD))
				.blockFactory(BlockCrate::new)
				.tileFactory(new TileFactory<EnumCrystalColor>(){
					@Override
					public TileEntityCrate createTile(EnumCrystalColor variant) {
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
		
		pedistal = new BlockPedistal(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.STONE)); 
		RegistrationHandler.createBlock(pedistal, ModItemGroups.ITEM_GROUP_BLOCKS, "pedistal");
		fusionPedistal = new BlockFusionPedistal(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.STONE)); 
		RegistrationHandler.createBlock(fusionPedistal, ModItemGroups.ITEM_GROUP_BLOCKS, "fusion_pedistal");
		
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
		
		pipeItem = new BlockPipe(NetworkType.ITEM, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.METAL)); 
		RegistrationHandler.createBlock(pipeItem, ModItemGroups.ITEM_GROUP_BLOCKS, "pipe_item");
	}


	public static void addBlocksToTags() {
		//BlockTags.PLANKS.getAllElements().add(crystalPlanksGroup.getBlock(EnumCrystalColorSpecial.BLUE));
	}	
	
}
