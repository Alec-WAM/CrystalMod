package alec_wam.CrystalMod.tiles.machine.power.redstonereactor;

import java.util.List;
import java.util.Queue;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCongealedSponge extends Block {

	public BlockCongealedSponge()
    {
        super(Material.SPONGE);
        setHardness(0.6f);
        setSoundType(SoundType.PLANT);
        setCreativeTab(CrystalMod.tabBlocks);
    }

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!entityIn.isImmuneToFire() && entityIn instanceof EntityLivingBase && !EnchantmentHelper.hasFrostWalkerEnchantment((EntityLivingBase)entityIn))
        {
            entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
        }

        super.onEntityWalk(worldIn, pos, entityIn);
    }
	
	@Override	
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.tryAbsorb(worldIn, pos, state);
    }

    @SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        this.tryAbsorb(worldIn, pos, state);
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    protected void tryAbsorb(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.absorb(worldIn, pos))
        {
            worldIn.playEvent(2001, pos, Block.getIdFromBlock(Blocks.WATER));
        }
    }

    private boolean absorb(World worldIn, BlockPos pos)
    {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.<Tuple<BlockPos, Integer>>newLinkedList();
        List<BlockPos> list = Lists.<BlockPos>newArrayList();
        queue.add(new Tuple<BlockPos, Integer>(pos, Integer.valueOf(0)));
        int i = 0;

        while (!((Queue<?>)queue).isEmpty())
        {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getFirst();
            int j = tuple.getSecond().intValue();

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                BlockPos blockpos1 = blockpos.offset(enumfacing);

                if (worldIn.getBlockState(blockpos1).getMaterial() == Material.WATER)
                {
                    worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 2);
                    list.add(blockpos1);
                    ++i;

                    if (j < 6)
                    {
                        queue.add(new Tuple<BlockPos, Integer>(blockpos1, Integer.valueOf(j + 1)));
                    }
                }
            }

            if (i > 64)
            {
                break;
            }
        }

        for (BlockPos blockpos2 : list)
        {
            worldIn.notifyNeighborsOfStateChange(blockpos2, Blocks.AIR, false);
        }

        return i > 0;
    }
	
	@Override
    public MapColor getMapColor(IBlockState state)
    {
        return MapColor.RED;
    }
	
}
