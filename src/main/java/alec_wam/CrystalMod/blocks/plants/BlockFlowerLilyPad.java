package alec_wam.CrystalMod.blocks.plants;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFlowerLilyPad extends BlockLilyPad implements IGrowable
{
	public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
	public static final int MAX_AGE = 7;
	public BlockFlowerLilyPad(Properties builder) {
		super(builder);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(AGE);
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick(IBlockState state, World worldIn, BlockPos pos, Random random)
    {
		if (isValidPosition(state, worldIn, pos))
        {
        	if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true)) {
                int j = state.get(AGE).intValue();
                if (j == MAX_AGE)
                {
                	List<BlockPos> validPos = Lists.newArrayList();
                	
                	for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL){
                		BlockPos offPos = pos.offset(facing);
                		if(worldIn.isAirBlock(offPos) && Blocks.LILY_PAD.isValidPosition(Blocks.LILY_PAD.getDefaultState(), worldIn, offPos)){
                			validPos.add(offPos);
                		}
                	}
                	
                    if (!validPos.isEmpty())
                    {
	                	BlockPos lilyPos = validPos.get(0);
	                    worldIn.setBlockState(lilyPos, Blocks.LILY_PAD.getDefaultState());
	                    worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(0)), 2);
	                    worldIn.playSound(null, lilyPos, Blocks.LILY_PAD.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 0.6f, 0.8f);
                    }
                }
                else
                {
                    worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(j + 1)), 2);
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
        	}
        }
    }
	
	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		int age = state.get(AGE).intValue();
		return age < MAX_AGE;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos,	IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int age = state.get(AGE).intValue();
		int incr = MathHelper.nextInt(worldIn.rand, 2, 5);
		int newAge = Integer.valueOf(age + incr);
		if(newAge > MAX_AGE){
			newAge = MAX_AGE;
		}
		worldIn.setBlockState(pos, state.with(AGE, newAge), 2);
	}
    
}