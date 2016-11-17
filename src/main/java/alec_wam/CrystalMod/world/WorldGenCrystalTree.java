package alec_wam.CrystalMod.world;

import java.util.Random;

import alec_wam.CrystalMod.blocks.BlockCrystalLeaves;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalTreePlant;
import alec_wam.CrystalMod.items.ItemCrystalSeedTree;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
    
    private WoodType treeType;

    public WorldGenCrystalTree(boolean notify)
    {
        this(notify, 4, WoodType.BLUE, false);
    }

    public WorldGenCrystalTree(boolean notify, int height, WoodType type, boolean treePlants)
    {
        super(notify);
        this.minTreeHeight = height;
        this.treeType = type;
        this.plantsGrow = treePlants;
    }

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

                if (state.getBlock().canSustainPlant(state, worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)ModBlocks.crystalSapling) && position.getY() < worldIn.getHeight() - i - 1)
                {
                    this.setDirtAt(worldIn, position.down());
                    int k2 = 3;
                    int l2 = 0;

                    for (int j3 = 0; j3 < i; ++j3)
                    {
                        BlockPos upN = position.up(j3);
                        state = worldIn.getBlockState(upN);

                        if (state.getBlock().isAir(state, worldIn, upN) || state.getBlock().isLeaves(state, worldIn, upN) || state.getMaterial() == Material.VINE)
                        {
                        	IBlockState woodState = ModBlocks.crystalLog.getDefaultState().withProperty(BlockCrystalLog.VARIANT, treeType);
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
                                    IBlockState leaveState = ModBlocks.crystalLeaves.getDefaultState().withProperty(BlockCrystalLeaves.VARIANT, treeType);
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
                        /*if (rand.nextInt(5) == 0)
                        {*/
                            /*for (int l3 = 0; l3 < 2; ++l3)
                            {
                                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                                {
                                    if (rand.nextInt(3 - l3) == 0)
                                    {
                                        EnumFacing enumfacing1 = enumfacing.getOpposite();
                                        this.placeTreePlant(worldIn, rand.nextInt(3), position.add(enumfacing1.getFrontOffsetX(), i - 5 + l3, enumfacing1.getFrontOffsetZ()), enumfacing);
                                    }
                                }
                            }*/
                        //}
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
        this.setBlockAndNotifyAdequately(worldIn, pos, state.getBlock().getDefaultState().withProperty(BlockCrystalTreePlant.AGE, Integer.valueOf(p_181652_2_)).withProperty(BlockCrystalTreePlant.FACING, side));
    }
}