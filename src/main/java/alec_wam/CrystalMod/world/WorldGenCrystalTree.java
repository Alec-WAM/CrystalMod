package alec_wam.CrystalMod.world;

import java.util.Random;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalTreePlant;
import alec_wam.CrystalMod.items.ItemCrystalSeedTree;
import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenCrystalTree extends WorldGenAbstractTree
{
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;
    /** True if this tree should grow Tree Plants. */
    private final boolean plantsGrow;
    
    private CrystalColors.Basic treeType;

    public WorldGenCrystalTree(boolean notify)
    {
        this(notify, 4, CrystalColors.Basic.BLUE, false);
    }

    public WorldGenCrystalTree(boolean notify, int height, CrystalColors.Basic type, boolean treePlants)
    {
        super(notify);
        this.minTreeHeight = height;
        this.treeType = type;
        this.plantsGrow = treePlants;
    }

    @Override
	public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int i = rand.nextInt(3) + this.minTreeHeight;
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

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l)
                {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1)
                    {
                        if (j >= 0 && j < worldIn.getHeight())
                        {
                            if (!this.isReplaceable(worldIn,blockpos$mutableblockpos.setPos(l, j, i1)))
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
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                IBlockState state = worldIn.getBlockState(position.down());

                if (state.getBlock().canSustainPlant(state, worldIn, position.down(), net.minecraft.util.EnumFacing.UP, ModBlocks.crystalSapling) && position.getY() < worldIn.getHeight() - i - 1)
                {
                    this.setDirtAt(worldIn, position.down());
                    for (int j3 = 0; j3 < i; ++j3)
                    {
                        BlockPos upN = position.up(j3);
                        state = worldIn.getBlockState(upN);

                        if (state.getBlock().isAir(state, worldIn, upN) || state.getBlock().isLeaves(state, worldIn, upN) || state.getMaterial() == Material.VINE)
                        {
                        	IBlockState woodState = ModBlocks.crystalLog.getDefaultState().withProperty(CrystalColors.COLOR_BASIC, treeType);
                            this.setBlockAndNotifyAdequately(worldIn, position.up(j3), woodState);
                        }
                    }

                    for (int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3)
                    {
                        int i4 = i3 - (position.getY() + i);
                        int j1 = 1 - i4 / 2;

                        for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1)
                        {
                            int l1 = k1 - position.getX();

                            for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2)
                            {
                                int j2 = i2 - position.getZ();

                                if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0)
                                {
                                    BlockPos blockpos = new BlockPos(k1, i3, i2);
                                    state = worldIn.getBlockState(blockpos);
                                    Block block = state.getBlock();
                                    IBlockState leaveState = ModBlocks.crystalLeaves.getDefaultState().withProperty(CrystalColors.COLOR_BASIC, treeType);
                                    if (block.isAir(state, worldIn, blockpos) || block.canPlaceBlockAt(worldIn, blockpos) || worldIn.getBlockState(blockpos) == leaveState)
                                    {
                                    	//worldIn.setBlockState(blockpos, leaveState, 3);
                                    	setBlockAndNotifyAdequately(worldIn, blockpos, leaveState);
                                    }
                                }
                            }
                        }
                    }
                    
                    if (this.plantsGrow)
                    {
                    	for (int j3 = 1; j3 < i; ++j3)
                        {
                            BlockPos upN = position.up(j3);
                        	for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                            {
                        		BlockPos plantPos = upN.offset(enumfacing);
                        		
                                if (worldIn.isAirBlock(plantPos) && rand.nextInt(8-j3) == 0)
                                {
                                    EnumFacing enumfacing1 = enumfacing.getOpposite();
                                    this.placeTreePlant(worldIn, rand.nextInt(3), upN.offset(enumfacing1), enumfacing);
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
        else
        {
            return false;
        }
    }

    private void placeTreePlant(World worldIn, int p_181652_2_, BlockPos pos, EnumFacing side)
    {
    	IBlockState state = ItemCrystalSeedTree.getPlant(treeType);
    	if(state == null || !(state.getBlock() instanceof BlockCrystalTreePlant))return;
        this.setBlockAndNotifyAdequately(worldIn, pos, state.getBlock().getDefaultState().withProperty(BlockCrystalTreePlant.AGE, Integer.valueOf(p_181652_2_)).withProperty(BlockHorizontal.FACING, side));
    }
}