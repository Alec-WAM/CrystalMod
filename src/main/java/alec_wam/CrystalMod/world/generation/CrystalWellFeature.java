package alec_wam.CrystalMod.world.generation;

import java.util.List;
import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.world.IGenerationFeature;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.world.structures.CrystalWell;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class CrystalWellFeature implements IGenerationFeature {

	@Override
	public boolean generateFeature(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
		if(world.provider.getDimensionType() == DimensionType.OVERWORLD && Config.generateOverworldWell){
        	if(Config.overworldWellChance > 0 && random.nextInt(Config.overworldWellChance) == 0){
        		int i = random.nextInt(16) + 8;
        		int j = random.nextInt(16) + 8;
        		int bX = (chunkX * 16) + i;
        		int bZ = (chunkZ * 16) + j;
        		int height = world.getHeight(bX, bZ);
        		BlockPos pos = new BlockPos(bX, height, bZ);
        		BlockPos blockpos = null;
        		
        		int y = pos.getY();
        		while(blockpos == null && y >= 16){
        			BlockPos testPos = new BlockPos(bX, y, bZ);
        			List<BlockPos> posList = BlockUtil.getBlocksInBB(testPos.north().west(), 7, 1, 7);
        			int groundNeeded = posList.size();
        			for(BlockPos pos2 : posList){
        				IBlockState state = world.getBlockState(pos2);
        				if(state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.STONE || state.getBlock() == Blocks.DIRT){
        					groundNeeded--;
        				}
        			}
        			if(groundNeeded <=0){
        				int airNeeded = 75;
        				for(int x = 0; x < 5; x++){
        					for(int y2 = 0; y2 < 3; y2++){
        						for(int z = 0; z < 5; z++){
        							BlockPos pos3 = testPos.up().add(x, y2, z);
        							if(world.isAirBlock(pos3) || world.getBlockState(pos3).getBlock().isReplaceable(world, pos3)){
        								airNeeded--;
        							}
        						}
        					}
        				}
        				if(airNeeded <= 0){
        					blockpos = testPos;
        				} else {
        					y--;
        				}
        			}else {
        				y--;
        			}
        		}
        		if(blockpos !=null){
        			int color = MathHelper.getInt(random, 0, 3);
        			if(Config.retrogenInfo)ModLogger.info("Overworld Well: "+blockpos+" Type: "+color);
        			CrystalWell.generateOverworldWell(world, blockpos, random, color);
        		}
        	}
        }
        
        if(world.provider.getDimensionType() == DimensionType.NETHER && Config.generateNetherWell){
        	if(Config.netherWellChance > 0 && random.nextInt(Config.netherWellChance) == 0){
        		int i = random.nextInt(16) + 8;
        		int j = random.nextInt(16) + 8;
        		BlockPos pos = new BlockPos((chunkX * 16) + i, 0, (chunkZ * 16) + j);
        		BlockPos blockpos = null;
        		
        		int y = 11;
        		while(blockpos == null && y < 120){
        			BlockPos testPos = pos.up(y);
        			List<BlockPos> posList = BlockUtil.getBlocksInBB(testPos.north().west(), 7, 1, 7);
        			int rackNeeded = posList.size();
        			for(BlockPos pos2 : posList){
        				if(world.getBlockState(pos2) == Blocks.NETHERRACK.getDefaultState()){
        					rackNeeded--;
        				}
        			}
        			if(rackNeeded <=0){
        				int airNeeded = 75;
        				for(int x = 0; x < 5; x++){
        					for(int y2 = 0; y2 < 3; y2++){
        						for(int z = 0; z < 5; z++){
        							if(world.isAirBlock(testPos.up().add(x, y2, z))){
        								airNeeded--;
        							}
        						}
        					}
        				}
        				if(airNeeded <= 0){
        					blockpos = testPos;
        				} else {
        					y++;
        				}
        			}else {
        				y++;
        			}
        		}
        		if(blockpos !=null){
        			if(Config.retrogenInfo)ModLogger.info("Nether Well: "+blockpos);
                	CrystalWell.generateNetherWell(world, blockpos, random);
        		}
        	}
        }
        
        if(world.provider.getDimensionType() == DimensionType.THE_END && Config.generateEndWell){
        	//Outside of Main Island Range
        	if((long)chunkX * (long)chunkX + (long)chunkZ * (long)chunkZ > 4096L){
        		if(Config.endWellChance > 0 && random.nextInt(Config.endWellChance) == 0){
        			int i = random.nextInt(16) + 8;
                    int j = random.nextInt(16) + 8;
        			BlockPos pos = new BlockPos((chunkX * 16) + i, 0, (chunkZ * 16) + j);
                    BlockPos blockpos = world.getHeight(pos).up();
                    while (world.isAirBlock(blockpos) && blockpos.getY() > 7)
                    {
                    	blockpos = blockpos.down();
                    }
                    
                    BlockPos bottom = blockpos.down(7);
                    List<BlockPos> posList = BlockUtil.getBlocksInBB(bottom, 7, 1, 7);
                    boolean pass = true;
                    check : for(BlockPos pos2 : posList){
                    	if(world.isAirBlock(pos2)){
                    		pass = false;
                    		break check;
                    	}
                    }
                    if(pass){
                    	if(Config.retrogenInfo)ModLogger.info("End Well: "+blockpos);
                    	CrystalWell.generateEndWell(world, blockpos, random);
                    }
        		}
        	}
        }
		return false;
	}

	@Override
	public boolean isRetroGenAllowed() {
		return false;
	}

}
