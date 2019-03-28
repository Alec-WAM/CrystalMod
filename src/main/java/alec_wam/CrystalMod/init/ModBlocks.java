package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot;
import alec_wam.CrystalMod.blocks.BlockCrystalLeaves;
import alec_wam.CrystalMod.blocks.BlockCrystalOre;
import alec_wam.CrystalMod.blocks.BlockCrystalSapling;
import alec_wam.CrystalMod.blocks.BlockVariant;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.color.EnumCrystalColor;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CrystalMod.MODID)
public class ModBlocks {
	
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockVariant<EnumCrystalColorSpecial>> crystalBlockGroup;
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalOre> crystalOreGroup;
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalOre> crystalOreNetherGroup;
	public static BlockVariantGroup<EnumCrystalColor, BlockCrystalOre> crystalOreEndGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockVariant<EnumCrystalColorSpecial>> crystalStoneBrickGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalIngot> crystalIngotBlockGroup;
	
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalSapling> crystalSaplingGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLog> crystalLogGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockCrystalLeaves> crystalLeavesGroup;
	public static BlockVariantGroup<EnumCrystalColorSpecial, BlockVariant<EnumCrystalColorSpecial>> crystalPlanksGroup;
	
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
	}	
	
}
