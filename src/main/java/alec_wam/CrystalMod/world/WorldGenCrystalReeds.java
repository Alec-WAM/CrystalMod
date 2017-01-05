package alec_wam.CrystalMod.world;

import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenCrystalReeds extends WorldGenerator
{
	public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int i = 0; i < Config.reedPlacementTrys; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));

            if (worldIn.isAirBlock(blockpos))
            {
                BlockPos blockpos1 = blockpos.down();

                boolean ignorePlacement = false;
                
                if (ignorePlacement || worldIn.getBlockState(blockpos1.west()).getMaterial() == Material.WATER || worldIn.getBlockState(blockpos1.east()).getMaterial() == Material.WATER || worldIn.getBlockState(blockpos1.north()).getMaterial() == Material.WATER || worldIn.getBlockState(blockpos1.south()).getMaterial() == Material.WATER)
                {
                	IBlockState[] reeds = 
	                	{
	                			ModBlocks.crystalReedsBlue.getDefaultState(), 
	                			ModBlocks.crystalReedsRed.getDefaultState(), 
	                			ModBlocks.crystalReedsGreen.getDefaultState(),
	                			ModBlocks.crystalReedsDark.getDefaultState()
	                	}; 
	                IBlockState reed = null;
	            	try{
	            		reed = reeds[MathHelper.getRandomIntegerInRange(rand, 0, reeds.length-1)];
	            	} catch(Exception e){}
                	
                    int j = 2 + rand.nextInt(rand.nextInt(3) + 1);

                    for (int k = 0; k < j; ++k)
                    {
                        if (ignorePlacement || reed.getBlock().canPlaceBlockAt(worldIn, blockpos))
                        {
                            worldIn.setBlockState(blockpos.up(k), reed, 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}
