package alec_wam.CrystalMod.blocks.crystexium;

import java.util.Random;

import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCrystexGrass extends EnumBlock<CrystalColors.SuperSpecial> implements IGrowable {
	

	public BlockCrystexGrass() {
		super(Material.GRASS, CrystalColors.COLOR_SUPER, CrystalColors.SuperSpecial.class);
		this.setTickRandomly(true);
		this.setHardness(0.6F);
		this.setSoundType(SoundType.PLANT);
		this.setDefaultState(this.blockState.getBaseState().withProperty(CrystalColors.COLOR_SUPER, CrystalColors.SuperSpecial.BLUE));
		this.setHarvestLevel("shovel", 0);
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2)
            {
                worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
            }
            else
            {
                if (worldIn.getLightFromNeighbors(pos.up()) >= 9)
                {
                    for (int i = 0; i < 4; ++i)
                    {
                        BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

                        if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos))
                        {
                            return;
                        }

                        IBlockState iblockstate = worldIn.getBlockState(blockpos.up());
                        IBlockState iblockstate1 = worldIn.getBlockState(blockpos);

                        if (iblockstate1.getBlock() == Blocks.DIRT && iblockstate1.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT && worldIn.getLightFromNeighbors(blockpos.up()) >= 4 && iblockstate.getLightOpacity(worldIn, pos.up()) <= 2)
                        {
                            worldIn.setBlockState(blockpos, getDefaultState().withProperty(CrystalColors.COLOR_SUPER, state.getValue(CrystalColors.COLOR_SUPER)));
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Blocks.DIRT.getItemDropped(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
    }
	
	@Override
	public int damageDropped(IBlockState state) {
		return BlockDirt.DirtType.DIRT.getMetadata();
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
    {
		return Blocks.GRASS.canSustainPlant(state, world, pos, direction, plantable);
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
    	//Copied from normal grass
        BlockPos blockpos = pos.up();

        for (int i = 0; i < 128; ++i)
        {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true)
            {
                if (j >= i / 16)
                {
                    if (worldIn.isAirBlock(blockpos1))
                    {
                        if (rand.nextInt(8) == 0)
                        {
                        	//TODO Think about this ability
                            worldIn.getBiome(blockpos1).plantFlower(worldIn, rand, blockpos1);
                        }
                        else
                        {
                            IBlockState iblockstate1 = ModBlocks.crystexTallGrass.getDefaultState().withProperty(CrystalColors.COLOR_SUPER, state.getValue(CrystalColors.COLOR_SUPER));

                            if (ModBlocks.crystexTallGrass.canBlockStay(worldIn, blockpos1, iblockstate1))
                            {
                                worldIn.setBlockState(blockpos1, iblockstate1, 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(blockpos1.down()).getBlock() != this || worldIn.getBlockState(blockpos1).isNormalCube())
                {
                    break;
                }

                ++j;
            }
        }
    }
	
}
