package alec_wam.CrystalMod.world.generation;

import java.util.List;
import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.world.WorldGenCrysineMushroom;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class CrysineMushroomFeature implements IGenerationFeature {

	public static final WorldGenCrysineMushroom genBigMushroom = new WorldGenCrysineMushroom(false);
	
	@Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
		int x = chunkX * 16;
        int y = chunkZ * 16;
        BlockPos blockpos = new BlockPos(x, 0, y);
        boolean changed = false;
		if(world.provider.getDimensionType() == DimensionType.NETHER && Config.generateNetherCrysineMushrooms){
        	if(random.nextBoolean()){
        		BlockPos genPos = blockpos.add(random.nextInt(16) + 8, random.nextInt(128), random.nextInt(16) + 8);
        		for (int i = 0; i < 64; ++i)
                {
                    BlockPos pos = genPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

                    if (world.isAirBlock(pos) && (!world.provider.hasNoSky() || pos.getY() < world.getHeight() - 1) && ModBlocks.crysineMushroom.canBlockStay(world, pos, ModBlocks.crysineMushroom.getDefaultState()))
                    {
                    	world.setBlockState(pos, ModBlocks.crysineMushroom.getDefaultState(), 2);
                    	changed = true;
                    }
                }
        	}
        	
        	if(Config.giantCrysineMushroomChance > 0 && random.nextInt(Config.giantCrysineMushroomChance) == 0){
        		int i = random.nextInt(16) + 8;
        		int j = random.nextInt(16) + 8;
        		BlockPos pos = new BlockPos((chunkX * 16) + i, 0, (chunkZ * 16) + j);
        		BlockPos genPos = null;
        		
        		int ground = 1;
        		while(genPos == null && ground < 120){
        			BlockPos testPos = pos.up(ground);
        			List<BlockPos> posList = BlockUtil.getBlocksInBB(testPos, 3, 1, 3);
        			int rackNeeded = posList.size();
        			for(BlockPos pos2 : posList){
        				if(world.getBlockState(pos2) == Blocks.NETHERRACK.getDefaultState()){
        					rackNeeded--;
        				}
        			}
        			if(rackNeeded <=0){
        				List<BlockPos> airPosList = BlockUtil.getBlocksInBB(testPos.up(4), 5, 7, 5);
        				int airNeeded = airPosList.size();
        				for(BlockPos pos2 : airPosList){
            				if(world.isAirBlock(pos2) || world.getBlockState(pos2).getBlock().canBeReplacedByLeaves(world.getBlockState(pos2), world, pos2)){
            					airNeeded--;
            				}
            			}
        				if(airNeeded <= 0){
        					genPos = testPos;
        				} else {
        					ground++;
        				}
        			}else {
        				ground++;
        			}
        		}
        		
        		if(genPos !=null){
        			genBigMushroom.generate(world, random, genPos.up());
        			changed = true;
        		}
        	}
        	return changed && !newGen;
        }
		return false;
	}

	@Override
	public boolean isRetroGenAllowed() {
		return false;
	}

}
