package alec_wam.CrystalMod.world;

import java.util.Random;

import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenCrysineMushroom extends WorldGenerator
{
    public WorldGenCrysineMushroom(boolean notify)
    {
        super(notify);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        Block block = ModBlocks.crysineMushroomBlock;

        int i = rand.nextInt(5) + 4;

        if (position.getY() >= 1 && position.getY() + i + 1 < 256)
        {
        	IBlockState state = worldIn.getBlockState(position);
        	for(int y = 0; y <= i; y++){
        		BlockPos currentPos = position.up(y);
        		state = worldIn.getBlockState(currentPos);
        		if(y == i) {
        			state = worldIn.getBlockState(currentPos);
        			if (state.getBlock().canBeReplacedByLeaves(state, worldIn, currentPos))
	        		{
	        			this.setBlockAndNotifyAdequately(worldIn, currentPos, block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.CENTER));
	        		} 
        			BlockPos[] topSpots = {
        					currentPos.offset(EnumFacing.NORTH).offset(EnumFacing.WEST), currentPos.offset(EnumFacing.NORTH), currentPos.offset(EnumFacing.NORTH).offset(EnumFacing.EAST),
        					currentPos.offset(EnumFacing.EAST), currentPos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST), currentPos.offset(EnumFacing.SOUTH),
                			currentPos.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST), currentPos.offset(EnumFacing.WEST)
                		};
                	
                	for(int p = 0; p < 8; p++){
                		BlockPos blockpos = topSpots[p];
                		state = worldIn.getBlockState(blockpos);
                		BlockHugeMushroom.EnumType type = BlockHugeMushroom.EnumType.NORTH_WEST;
                		if(p == 1) type = BlockHugeMushroom.EnumType.NORTH;
                		if(p == 2) type = BlockHugeMushroom.EnumType.NORTH_EAST;
                		if(p == 3) type = BlockHugeMushroom.EnumType.EAST;
                		if(p == 4) type = BlockHugeMushroom.EnumType.SOUTH_EAST;
                		if(p == 5) type = BlockHugeMushroom.EnumType.SOUTH;
                		if(p == 6) type = BlockHugeMushroom.EnumType.SOUTH_WEST;
                		if(p == 7) type = BlockHugeMushroom.EnumType.WEST;
                		if (state.getBlock().canBeReplacedByLeaves(state, worldIn, blockpos))
                		{
                			this.setBlockAndNotifyAdequately(worldIn, blockpos, block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, type));
                		}
                	}
        		}
        		else if(y == i-1) {
        			if (state.getBlock().canBeReplacedByLeaves(state, worldIn, currentPos))
	        		{
	        			this.setBlockAndNotifyAdequately(worldIn, currentPos, block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.ALL_INSIDE));
	        		} 
        			BlockPos[] middleSpots = 
        				{
        					currentPos.offset(EnumFacing.NORTH).offset(EnumFacing.WEST), currentPos.offset(EnumFacing.NORTH), currentPos.offset(EnumFacing.NORTH).offset(EnumFacing.EAST),
                			currentPos.offset(EnumFacing.EAST), currentPos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST), currentPos.offset(EnumFacing.SOUTH),
                			currentPos.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST), currentPos.offset(EnumFacing.WEST)
                		};
        			for(int p = 0; p < middleSpots.length; p++){
        				BlockPos middlePos = middleSpots[p];
        				state = worldIn.getBlockState(middlePos);
            			BlockHugeMushroom.EnumType type = BlockHugeMushroom.EnumType.ALL_INSIDE;
        				if(p == 1) type = BlockHugeMushroom.EnumType.NORTH;
        				if(p == 3) type = BlockHugeMushroom.EnumType.EAST;
        				if(p == 5) type = BlockHugeMushroom.EnumType.SOUTH;
        				if(p == 7) type = BlockHugeMushroom.EnumType.WEST;
	        			if (state.getBlock().canBeReplacedByLeaves(state, worldIn, middlePos))
		        		{
		        			this.setBlockAndNotifyAdequately(worldIn, middlePos, block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, type));
		        		}  
        			}
        			BlockPos[] lowerSpots = 
                		{
                			currentPos.west(2).north(), currentPos.west(2).north(2), currentPos.west().north(2),
                			currentPos.east(2).north(), currentPos.east(2).north(2), currentPos.east().north(2),
                			currentPos.west(2).south(), currentPos.west(2).south(2), currentPos.west().south(2),
                			currentPos.east(2).south(), currentPos.east(2).south(2), currentPos.east().south(2)
                		};
                	BlockHugeMushroom.EnumType lowerTypes[] =
                		{
                				BlockHugeMushroom.EnumType.SOUTH_WEST, BlockHugeMushroom.EnumType.NORTH_WEST, BlockHugeMushroom.EnumType.NORTH_EAST,
                				BlockHugeMushroom.EnumType.SOUTH_EAST, BlockHugeMushroom.EnumType.NORTH_EAST, BlockHugeMushroom.EnumType.NORTH_WEST,
                				BlockHugeMushroom.EnumType.NORTH_WEST, BlockHugeMushroom.EnumType.SOUTH_WEST, BlockHugeMushroom.EnumType.SOUTH_EAST,
                				BlockHugeMushroom.EnumType.NORTH_EAST, BlockHugeMushroom.EnumType.SOUTH_EAST, BlockHugeMushroom.EnumType.SOUTH_WEST
                		};
                	
                	for(int p = 0; p < lowerSpots.length; p++){
                		BlockPos blockpos = lowerSpots[p];
                		state = worldIn.getBlockState(blockpos);
                		BlockHugeMushroom.EnumType type = lowerTypes[p];
                		if (state.getBlock().canBeReplacedByLeaves(state, worldIn, blockpos))
                		{
                			this.setBlockAndNotifyAdequately(worldIn, blockpos, block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, type));
                		}
                	}
        		}
        		else {
	        		if (state.getBlock().canBeReplacedByLeaves(state, worldIn, currentPos))
	        		{
	        			this.setBlockAndNotifyAdequately(worldIn, currentPos, block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.STEM));
	        		}  	
        		}
        		
        		if(y == 0 || y == i-2){
        			for(EnumFacing dir : EnumFacing.HORIZONTALS){
                		BlockPos blockpos = currentPos.offset(dir);
                		state = worldIn.getBlockState(blockpos);
                		if (state.getBlock().canBeReplacedByLeaves(state, worldIn, blockpos))
                		{
                			this.setBlockAndNotifyAdequately(worldIn, blockpos, block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, y == 0 ? BlockHugeMushroom.EnumType.ALL_STEM : BlockHugeMushroom.EnumType.ALL_OUTSIDE));
                		}
                	}
        		}
        	}
            return true;
        }
        else
        {
            return false;
        }
    }
}