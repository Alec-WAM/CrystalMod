package alec_wam.CrystalMod.init;

import java.lang.reflect.Method;

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
import alec_wam.CrystalMod.blocks.decoration.FenceBlockVariant;
import alec_wam.CrystalMod.blocks.decoration.FenceGateBlockVariant;
import alec_wam.CrystalMod.blocks.plants.BlockCrystalBerryBush;
import alec_wam.CrystalMod.blocks.plants.BlockFlowerLilyPad;
import alec_wam.CrystalMod.blocks.plants.BlockItemWaterPlant;
import alec_wam.CrystalMod.blocks.plants.BlockReedVariant;
import alec_wam.CrystalMod.blocks.plants.BlockTallFlowerVariant;
import alec_wam.CrystalMod.blocks.plants.EnumBetterRoses;
import alec_wam.CrystalMod.client.CustomItemRender;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.BlockVariantGroup.TileFactory;
import alec_wam.CrystalMod.core.BlockVariantGroup.TileTypeFactory;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.tiles.ContainerBlockCustom;
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
import alec_wam.CrystalMod.tiles.energy.battery.BlockItemBattery;
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
import alec_wam.CrystalMod.tiles.xp.BlockXPTank;
import alec_wam.CrystalMod.tiles.xp.TileEntityXPTank;
import alec_wam.CrystalMod.tiles.xp.TileEntityXPVacuum;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraft.world.IBlockReader;
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
	public static BlockVariantGroup<EnumCrystalColorSpecial, FenceBlockVariant<EnumCrystalColorSpecial>> crystalFencesGroup;	
	public static BlockVariantGroup<EnumCrystalColorSpecial, FenceGateBlockVariant<EnumCrystalColorSpecial>> crystalFenceGateGroup;		
	
	public static BlockVariantGroup<EnumCrystalColor, BlockReedVariant<EnumCrystalColor>> crystalReedGroup;	
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalShard> crystalShardBlock;	
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalBerryBush> crystalBerryBushGroup;	
	
	public static BlockVariantGroup<EnumBetterRoses, BlockTallFlowerVariant<EnumBetterRoses>> betterRosesGroup;	
	public static BlockFlowerLilyPad flowerLilypad;

	public static BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockCrate> crateGroup;
	public static BlockVariantGroup<WoodenCrystalChestType, BlockWoodenCrystalChest> woodenChestGroup;
	public static BlockVariantGroup<MetalCrystalChestType, BlockMetalCrystalChest> metalChestGroup;
	public static BlockWirelessChest wirelessChest;
	public static TileEntityType<TileEntityWirelessChest> TILE_WIRELESS_CHEST;
	public static BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockTank> tankGroup;
	
	public static BlockXPTank xpTank;
	public static TileEntityType<TileEntityXPTank> TILE_XP_TANK;
	public static ContainerBlockCustom xpVacuum;
	public static TileEntityType<TileEntityXPVacuum> TILE_XP_VACUUM;
	
	public static BlockVariantGroup<WoodType, BlockJar> jarGroup;

	public static BlockPedestal pedestal;
	public static TileEntityType<TileEntityPedestal> TILE_PEDESTAL;
	public static BlockFusionPedestal fusionPedestal;
	public static TileEntityType<TileEntityFusionPedestal> TILE_FUSION_PEDESTAL;
	
	public static BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockBattery> batteryGroup;
	public static BlockVariantGroup<EnumEngineType, BlockEngine> engineBasicGroup;
	public static BlockVariantGroup<EnumCraftingMachine, BlockCraftingMachine> craftingMachine;

	public static BlockPipe pipeItem;
	public static TileEntityType<TileEntityPipeItem> TILE_PIPE_ITEM;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockPipeEnergyCU> pipeEnergyCUGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockPipeEnergyRF> pipeEnergyRFGroup;

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
				.blockPropertiesFactory(type -> Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT))
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
				.blockPropertiesFactory(type -> Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT))
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
		crystalFencesGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, FenceBlockVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystalfence")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD))
				.blockFactory(FenceBlockVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalFencesGroup);
		crystalFenceGateGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, FenceGateBlockVariant<EnumCrystalColorSpecial>>create()
				.groupName("crystalfence_gate")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD))
				.blockFactory(FenceGateBlockVariant<EnumCrystalColorSpecial>::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalFenceGateGroup);
		
		//Plants
		crystalReedGroup = BlockVariantGroup.Builder.<EnumCrystalColor, BlockReedVariant<EnumCrystalColor>>create()
				.groupName("crystalreeds")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT))
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
		flowerLilypad = new BlockFlowerLilyPad(Block.Properties.create(Material.PLANTS).tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT)); 
		RegistrationHandler.createBlock(flowerLilypad, new BlockItemWaterPlant(flowerLilypad, (new Item.Properties()).group(ItemGroup.DECORATIONS)), "flowerlilypad");
		
		crystalShardBlock = BlockVariantGroup.Builder.<EnumCrystalColor, BlockCrystalShard>create()
				.groupName("crystalshardblock")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.GLASS).tickRandomly().hardnessAndResistance(0.8F).sound(SoundType.GLASS).lightValue(8))
				.blockFactory(BlockCrystalShard::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalShardBlock);
		crystalBerryBushGroup = BlockVariantGroup.Builder.<EnumCrystalColor, BlockCrystalBerryBush>create()
				.groupName("crystalbush")
				.suffix()
				.variants(EnumCrystalColor.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.LEAVES).tickRandomly().hardnessAndResistance(0.3F).sound(SoundType.PLANT))
				.blockFactory(BlockCrystalBerryBush::new)
				.build();
		RegistrationHandler.addBlockGroup(crystalBerryBushGroup);
		
		//TILES
		crateGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecialWithCreative, BlockCrate>create()
				.groupName("crate")
				.suffix()
				.variants(EnumCrystalColorSpecialWithCreative.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.WOOD).hardnessAndResistance(3.0F, 5.0F).sound(SoundType.WOOD))
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES))
				.blockFactory(BlockCrate::new)
				.tileFactory(new TileFactory<EnumCrystalColorSpecialWithCreative>(){
					@Override
					public TileEntityCrate createTile(EnumCrystalColorSpecialWithCreative variant) {
						return new TileEntityCrate(variant);
					}
				})
				.tileTypeFactory(new TileTypeFactory<EnumCrystalColorSpecialWithCreative, BlockCrate>(){
					@Override
					public Builder<?> createTileType(EnumCrystalColorSpecialWithCreative variant, BlockCrate block) {
						return TileEntityType.Builder.func_223042_a(TileEntityCrate::new, block);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(crateGroup);
		woodenChestGroup = BlockVariantGroup.Builder.<WoodenCrystalChestType, BlockWoodenCrystalChest>create()
				.groupName("woodenchest")
				.suffix()
				.variants(WoodenCrystalChestType.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD))
				.blockFactory(BlockWoodenCrystalChest::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES).setTEISR(() -> CustomItemRender::new))
				.tileFactory(new TileFactory<WoodenCrystalChestType>(){
					@Override
					public TileEntityWoodenCrystalChest createTile(WoodenCrystalChestType variant) {
						return new TileEntityWoodenCrystalChest(variant);
					}
				})
				.tileTypeFactory(new TileTypeFactory<WoodenCrystalChestType, BlockWoodenCrystalChest>(){
					@Override
					public Builder<?> createTileType(WoodenCrystalChestType variant, BlockWoodenCrystalChest block) {
						return TileEntityType.Builder.func_223042_a(TileEntityWoodenCrystalChest::new, block);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(woodenChestGroup);
		metalChestGroup = BlockVariantGroup.Builder.<MetalCrystalChestType, BlockMetalCrystalChest>create()
				.groupName("metalchest")
				.suffix()
				.variants(MetalCrystalChestType.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL))
				.blockFactory(BlockMetalCrystalChest::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES).setTEISR(() -> CustomItemRender::new))
				.tileFactory(new TileFactory<MetalCrystalChestType>(){
					@Override
					public TileEntityMetalCrystalChest createTile(MetalCrystalChestType variant) {
						return new TileEntityMetalCrystalChest(variant);
					}
				})
				.tileTypeFactory(new TileTypeFactory<MetalCrystalChestType, BlockMetalCrystalChest>(){
					@Override
					public Builder<?> createTileType(MetalCrystalChestType variant, BlockMetalCrystalChest block) {
						return TileEntityType.Builder.func_223042_a(TileEntityMetalCrystalChest::new, block);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(metalChestGroup);			
		wirelessChest = new BlockWirelessChest(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)); 
		RegistrationHandler.createBlock(wirelessChest, new BlockItem(wirelessChest, RegistrationHandler.defaultItemProperties(ModItemGroups.ITEM_GROUP_MACHINES).setTEISR(() -> CustomItemRender::new)), "wirelesschest");	
		TILE_WIRELESS_CHEST = registerTileEntity(CrystalMod.resource("wireless_chest"), TileEntityType.Builder.func_223042_a(TileEntityWirelessChest::new, wirelessChest));
		
		tankGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecialWithCreative, BlockTank>create()
				.groupName("tank")
				.suffix()
				.variants(EnumCrystalColorSpecialWithCreative.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(1.5F, 15.0F).sound(SoundType.GLASS))
				.blockFactory(BlockTank::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES).setTEISR(() -> CustomItemRender::new))
				.tileFactory(new TileFactory<EnumCrystalColorSpecialWithCreative>(){
					@Override
					public TileEntityTank createTile(EnumCrystalColorSpecialWithCreative variant) {
						return new TileEntityTank(variant);
					}
				})
				.tileTypeFactory(new TileTypeFactory<EnumCrystalColorSpecialWithCreative, BlockTank>(){
					@Override
					public Builder<?> createTileType(EnumCrystalColorSpecialWithCreative variant, BlockTank block) {
						return TileEntityType.Builder.func_223042_a(TileEntityTank::new, block);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(tankGroup);
		
		xpTank = new BlockXPTank(Block.Properties.create(Material.IRON).hardnessAndResistance(2.0F, 15.0F).sound(SoundType.GLASS)); 
		RegistrationHandler.createBlock(xpTank, new BlockItem(xpTank, RegistrationHandler.defaultItemProperties(ModItemGroups.ITEM_GROUP_MACHINES).setTEISR(() -> CustomItemRender::new)), "xptank");	
		TILE_XP_TANK = registerTileEntity(CrystalMod.resource("xp_tank"), TileEntityType.Builder.func_223042_a(TileEntityXPTank::new, xpTank));
		
		
		//TODO Add Gui Tank Overlay
		xpVacuum = new ContainerBlockCustom(Block.Properties.create(Material.IRON).hardnessAndResistance(2.0F, 15.0F).sound(SoundType.METAL)){

			@Override
			public TileEntity createNewTileEntity(IBlockReader worldIn) {
				return new TileEntityXPVacuum();
			}
			
		}; 
		RegistrationHandler.createBlock(xpVacuum, ModItemGroups.ITEM_GROUP_MACHINES, "xpvacuum");	
		TILE_XP_VACUUM = registerTileEntity(CrystalMod.resource("xp_vacuum"), TileEntityType.Builder.func_223042_a(TileEntityXPVacuum::new, xpVacuum));

		
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
						return new TileEntityJar(variant);
					}
				})
				.tileTypeFactory(new TileTypeFactory<WoodType, BlockJar>(){
					@Override
					public Builder<?> createTileType(WoodType variant, BlockJar block) {
						return TileEntityType.Builder.func_223042_a(TileEntityJar::new, block);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(jarGroup);	
		
		pedestal = new BlockPedestal(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.STONE)); 
		RegistrationHandler.createBlock(pedestal, ModItemGroups.ITEM_GROUP_MACHINES, "pedestal");
		TILE_PEDESTAL = registerTileEntity(CrystalMod.resource("pedestal"), TileEntityType.Builder.func_223042_a(TileEntityPedestal::new, pedestal));
		
		fusionPedestal = new BlockFusionPedestal(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.STONE)); 
		RegistrationHandler.createBlock(fusionPedestal, ModItemGroups.ITEM_GROUP_MACHINES, "fusion_pedestal");
		TILE_FUSION_PEDESTAL = registerTileEntity(CrystalMod.resource("fusion_pedestal"), TileEntityType.Builder.func_223042_a(TileEntityFusionPedestal::new, fusionPedestal));
		
		batteryGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecialWithCreative, BlockBattery>create()
				.groupName("battery")
				.suffix()
				.variants(EnumCrystalColorSpecialWithCreative.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(20.0F, 50.0F).sound(SoundType.METAL))
				.blockFactory(BlockBattery::new)
				.itemFactory(BlockItemBattery::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES).setTEISR(() -> CustomItemRender::new))
				.tileFactory(new TileFactory<EnumCrystalColorSpecialWithCreative>(){
					@Override
					public TileEntityBattery createTile(EnumCrystalColorSpecialWithCreative variant) {
						return new TileEntityBattery(variant);
					}
				})
				.tileTypeFactory(new TileTypeFactory<EnumCrystalColorSpecialWithCreative, BlockBattery>(){
					@Override
					public Builder<?> createTileType(EnumCrystalColorSpecialWithCreative variant, BlockBattery block) {
						return TileEntityType.Builder.func_223042_a(TileEntityBattery::new, block);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(batteryGroup);
		engineBasicGroup = BlockVariantGroup.Builder.<EnumEngineType, BlockEngine>create()
				.groupName("engine_basic")
				.suffix()
				.variants(EnumEngineType.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 15.0F).sound(SoundType.STONE))
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES))
				.blockFactory(BlockEngine::new)
				.tileFactory(new TileFactory<EnumEngineType>(){
					@Override
					public TileEntityEngineBase createTile(EnumEngineType variant) {
						return new TileEntityEngineFurnace(1);				
					}
				})
				.tileTypeFactory(new TileTypeFactory<EnumEngineType, BlockEngine>(){
					@Override
					public Builder<?> createTileType(EnumEngineType variant, BlockEngine block) {
						return TileEntityType.Builder.func_223042_a(TileEntityEngineFurnace::new, block);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(engineBasicGroup);
		
		craftingMachine = BlockVariantGroup.Builder.<EnumCraftingMachine, BlockCraftingMachine>create()
				.groupName("machine")
				.suffix()
				.variants(EnumCraftingMachine.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 5.0F).sound(SoundType.METAL))
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES))
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
				.tileTypeFactory(new TileTypeFactory<EnumCraftingMachine, BlockCraftingMachine>(){
					@Override
					public Builder<?> createTileType(EnumCraftingMachine variant, BlockCraftingMachine block) {
						if(variant == EnumCraftingMachine.FURNACE){
							return TileEntityType.Builder.func_223042_a(TileEntityPoweredFurnace::new, block);
						}
						if(variant == EnumCraftingMachine.GRINDER){
							return TileEntityType.Builder.func_223042_a(TileEntityGrinder::new, block);
						}
						if(variant == EnumCraftingMachine.PRESS){
							return TileEntityType.Builder.func_223042_a(TileEntityPress::new, block);
						}
						return null;
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(craftingMachine);
		
		pipeItem = new BlockPipe(NetworkType.ITEM, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.METAL)); 
		RegistrationHandler.createBlock(pipeItem, ModItemGroups.ITEM_GROUP_MACHINES, "pipe_item");
		TILE_PIPE_ITEM = registerTileEntity(CrystalMod.resource("pipe_item"), TileEntityType.Builder.func_223042_a(TileEntityPipeItem::new, pipeItem));
		
		pipeEnergyCUGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockPipeEnergyCU>create()
				.groupName("pipe_energy_cu")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.METAL))
				.blockFactory(BlockPipeEnergyCU::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES))
				.tileTypeFactory(new TileTypeFactory<EnumCrystalColorSpecial, BlockPipeEnergyCU>(){
					@Override
					public Builder<?> createTileType(EnumCrystalColorSpecial variant, BlockPipeEnergyCU block) {
						return TileEntityType.Builder.func_223042_a(TileEntityPipeEnergyCU::new, block);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(pipeEnergyCUGroup);	
		pipeEnergyRFGroup = BlockVariantGroup.Builder.<EnumCrystalColorSpecial, BlockPipeEnergyRF>create()
				.groupName("pipe_energy_rf")
				.suffix()
				.variants(EnumCrystalColorSpecial.values())
				.blockPropertiesFactory(type -> Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 10.0F).sound(SoundType.METAL))
				.blockFactory(BlockPipeEnergyRF::new)
				.itemPropertiesFactory(variant -> new Item.Properties().group(ModItemGroups.ITEM_GROUP_MACHINES))
				.tileTypeFactory(new TileTypeFactory<EnumCrystalColorSpecial, BlockPipeEnergyRF>(){
					@Override
					public Builder<?> createTileType(EnumCrystalColorSpecial variant, BlockPipeEnergyRF block) {
						return TileEntityType.Builder.func_223042_a(TileEntityPipeEnergyRF::new, block);
					}
				})
				.build();
		RegistrationHandler.addBlockGroup(pipeEnergyRFGroup);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> TileEntityType<T> registerTileEntity(String id, TileEntityType.Builder<T> builder) {
		try {
			Method method = null;
			for(Method m : TileEntityType.class.getDeclaredMethods()){
				if(m.getName().equals("register")){
					method = m;
				}
			}
			method.setAccessible(true);
			return (TileEntityType<T>) method.invoke(null, id, builder);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/*@SuppressWarnings("unchecked")
	public static <T extends TileEntity> TileEntityType<T> registerTileEntityGroup(String id, Supplier<? extends T> p_223042_0_, BlockVariantGroup<?, ?> group) {
		try {
			System.out.println("Blocks:" + group.getBlocks().size());
			TileEntityType.Builder<T> builder = TileEntityType.Builder.func_223042_a(p_223042_0_, new ArrayList<Block>(group.getBlocksMap().values()).get(0));
			Method method = null;
			try{	
				method = TileEntityType.class.getMethods()[1];
			} catch(Exception e){
				e.printStackTrace();
			}
			try{
				method.setAccessible(true);
				CrystalMod.LOGGER.warn("" + method.getName() + " " + method.toGenericString());
				return (TileEntityType<T>) method.invoke(null, id, builder);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			//throw new RuntimeException(group + " " + e.getMessage());
		}
		return null;
	}*/

	public static void addBlocksToTags() {
		/*Collection<Block> ores = Blocks.ORES.getAllElements();
		CrystalMod.LOGGER.info("Ores Size: " + ores.size());
		ores.addAll(crystalOreGroup.getBlocks());
		Collection<Block> ores2 = Blocks.ORES.getAllElements();
		CrystalMod.LOGGER.info("New Ores Size: " + ores2.size());*/
		//BlockTags.PLANKS.getAllElements().add(crystalPlanksGroup.getBlock(EnumCrystalColorSpecial.BLUE));
	}	
	
}
