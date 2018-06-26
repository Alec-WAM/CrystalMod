package alec_wam.CrystalMod.world.crystex.biomes;

import java.util.Random;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.CrystalColors;
import alec_wam.CrystalMod.world.WorldGenCrystalTree;
import alec_wam.CrystalMod.world.generation.WorldGenCustomTallGrass;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomePlains;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeCrystexPlains extends BiomePlains implements ICrystexColorBiome {

	public final WorldGenCrystalTree CRYSTAL_TREE;
	public final WorldGenCrystalTree CRYSTAL_TREE_SPECIAL;
	public final CrystalColors.SuperSpecial color;
	public BiomeCrystexPlains(CrystalColors.SuperSpecial color, BiomeProperties properties) {
		super(false, properties);
		this.color = color;
		CRYSTAL_TREE = new WorldGenCrystalTree(false, 4, color, false);
		CRYSTAL_TREE_SPECIAL = new WorldGenCrystalTree(false, 6, color, true);
		
		topBlock = ModBlocks.crystexGrass.getDefaultState().withProperty(CrystalColors.COLOR_SUPER, color);
	}

	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos)
	{
		this.theBiomeDecorator.decorate(worldIn, rand, this, pos);
	}

	@Override
	public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return (WorldGenAbstractTree)(rand.nextInt(3) == 0 ? CRYSTAL_TREE_SPECIAL : CRYSTAL_TREE);
    }
	
	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random rand)
    {
		return new WorldGenCustomTallGrass(ModBlocks.crystexTallGrass.getDefaultState().withProperty(CrystalColors.COLOR_SUPER, color));
    }

	@Override
	public CrystalColors.SuperSpecial getColor() {
		return color;
	}
	
	 /*public BiomeDecorator createBiomeDecorator()
	 {
		 return getModdedBiomeDecorator(new CrystexBiomeDecorator(color));
	 }*/
}
