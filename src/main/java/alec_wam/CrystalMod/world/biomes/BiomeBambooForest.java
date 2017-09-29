package alec_wam.CrystalMod.world.biomes;

import java.util.Random;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.world.WorldGenBambooTree;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeBambooForest extends Biome
{
    protected static final WorldGenBambooTree BAMBOO_TREE = new WorldGenBambooTree(false);

    public BiomeBambooForest(Biome.BiomeProperties properties)
    {
        super(properties);
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.grassPerChunk = 8;
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return super.genBigTreeChance(rand);
    }

    @Override
    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos)
    {
        return super.pickRandomFlower(rand, pos);
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        boolean grid = false;
    	if(!grid){
	    	for (int j = 0; j < 20; ++j) {
	            int k = rand.nextInt(16) + 8;
	            int l = rand.nextInt(16) + 8;
	            BlockPos blockpos = worldIn.getHeight(pos.add(k, 0, l));
	            IBlockState below = worldIn.getBlockState(blockpos.down());
	    		if(below.getBlock().canSustainPlant(below, worldIn, blockpos.down(), EnumFacing.UP, ModBlocks.normalSapling)){
	    			BAMBOO_TREE.generate(worldIn, rand, blockpos);
	    		}
	    	}
    	} else {
	    	for(int k = 1; k <= 16; k+=2){
	        	for(int l = 1; l <= 16; l+=2){
	        		BlockPos blockpos = worldIn.getHeight(pos.add(k, 0, l));
	        		IBlockState below = worldIn.getBlockState(blockpos.down());
		    		if(below.getBlock().canSustainPlant(below, worldIn, blockpos.down(), EnumFacing.UP, ModBlocks.normalSapling)){
		    			BAMBOO_TREE.generate(worldIn, rand, blockpos);
		    		}
	        	}
	        }
    	}
        super.decorate(worldIn, rand, pos);
    }

    @Override
    public Class <? extends Biome > getBiomeClass()
    {
        return BiomeBambooForest.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos pos)
    {
        return 0x7AC151;
    }
}