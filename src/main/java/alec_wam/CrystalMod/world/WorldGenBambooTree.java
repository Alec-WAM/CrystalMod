package alec_wam.CrystalMod.world;

import java.util.Random;

import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenBambooTree extends WorldGenAbstractTree
{
    private final int minTreeHeight, maxTreeHeight;

    public WorldGenBambooTree(boolean notify)
    {
        this(notify, 8, 8, false);
    }

    public WorldGenBambooTree(boolean notify, int minHeight, int maxHeight, boolean treePlants)
    {
        super(notify);
        this.minTreeHeight = minHeight;
        this.maxTreeHeight = maxHeight;
    }

    @Override
	public boolean generate(World worldIn, Random rand, BlockPos position)
    {
    	int i = MathHelper.getInt(rand, minTreeHeight, maxTreeHeight);
    	boolean flag = true;

    	if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight())
    	{
    		for (int j = position.getY(); j <= position.getY() + 1 + i; ++j)
    		{
    			int k = 1;

    			if (j == position.getY())
    			{
    				k = 0;
    			}

    			if (j >= position.getY() + 1 + i - 2)
    			{
    				k = 2;
    			}

    			BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

    			if (j >= 0 && j < worldIn.getHeight())
    			{
    				if (!this.isReplaceable(worldIn,blockpos$mutableblockpos.setPos(position.getX(), j, position.getZ())))
    				{
    					flag = false;
    				}
    			}
    			else
    			{
    				flag = false;
    			}
    		}
    	}

    	if (!flag)
    	{
    		return false;
    	}
    	else
    	{
    		IBlockState state = worldIn.getBlockState(position.down());

    		if (state.getBlock().canSustainPlant(state, worldIn, position.down(), net.minecraft.util.EnumFacing.UP, ModBlocks.normalSapling) && position.getY() < worldIn.getHeight() - i - 1)
    		{
    			for (int j3 = 0; j3 < i; ++j3)
    			{
    				BlockPos upN = position.up(j3);
    				state = worldIn.getBlockState(upN);

    				if (state.getBlock().isAir(state, worldIn, upN) || state.getBlock().isLeaves(state, worldIn, upN) || state.getMaterial() == Material.VINE)
    				{
    					IBlockState woodState = ModBlocks.bamboo.getDefaultState();
    					this.setBlockAndNotifyAdequately(worldIn, position.up(j3), woodState);
    				}
    			}

    			BlockPos top = position.up(i);
    			state = worldIn.getBlockState(top);

    			if (state.getBlock().isAir(state, worldIn, top) || state.getBlock().isLeaves(state, worldIn, top) || state.getMaterial() == Material.VINE)
    			{
    				IBlockState woodState = ModBlocks.bambooLeaves.getDefaultState();
    				this.setBlockAndNotifyAdequately(worldIn, top, woodState);
    			}

    			return true;
    		}
    		else
    		{
    			return false;
    		}
    	}
    }
}