package alec_wam.CrystalMod.world.crystex.biomes;

import java.util.Random;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.CrystalColors;
import alec_wam.CrystalMod.world.WorldGenCrystalTree;
import alec_wam.CrystalMod.world.generation.WorldGenCustomTallGrass;
import net.minecraft.world.biome.BiomeForest;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeCrystexForest extends BiomeForest implements ICrystexColorBiome {

	public final WorldGenCrystalTree CRYSTAL_TREE;
	public final WorldGenCrystalTree CRYSTAL_TREE_SPECIAL;
    public final CrystalColors.Basic color;
	public BiomeCrystexForest(CrystalColors.Basic color, BiomeProperties properties) {
		super(Type.NORMAL, properties);
		this.color = color;
		CRYSTAL_TREE = new WorldGenCrystalTree(false, 4, color, false);
		CRYSTAL_TREE_SPECIAL = new WorldGenCrystalTree(false, 6, color, true);
		topBlock = ModBlocks.crystexGrass.getDefaultState().withProperty(CrystalColors.COLOR_SPECIAL, CrystalColors.Special.convert(color));
	}

	@Override
	public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return (WorldGenAbstractTree)(rand.nextInt(10) == 0 ? CRYSTAL_TREE_SPECIAL : CRYSTAL_TREE);
    }
	
	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random rand)
    {
		return new WorldGenCustomTallGrass(ModBlocks.crystexTallGrass.getDefaultState().withProperty(CrystalColors.COLOR_SPECIAL, CrystalColors.Special.convert(color)));
    }

	@Override
	public CrystalColors.Basic getColor() {
		return color;
	}

	//TODO Add Crystex Flower Gen
	
}
