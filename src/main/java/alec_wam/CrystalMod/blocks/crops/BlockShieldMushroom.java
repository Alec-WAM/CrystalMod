package alec_wam.CrystalMod.blocks.crops;

import java.util.Random;

import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class BlockShieldMushroom extends BlockBush
{
    public static final AxisAlignedBB MUSHROOM_AABB = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.4000000059604645D, 0.699999988079071D);

    public BlockShieldMushroom()
    {
    	super(Material.PLANTS);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.BREWING);
        this.setSoundType(SoundType.PLANT);
    }
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return MUSHROOM_AABB;
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
    	return EnumPlantType.Cave;
    }
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
    	//Normally 25 for normal mushrooms
    	int speed = 30;
    	IBlockState below = worldIn.getBlockState(pos.down());
    	if(below.getBlock() == Blocks.MYCELIUM || below.getBlock() == Blocks.DIRT && below.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL){
    		speed = 20;
    	}
    	if(below.getBlock() == ModBlocks.crystexGrass){
    		speed = 1;
    	}
        if (rand.nextInt(speed) == 0)
        {
            int i = 5;

            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4)))
            {
                if (worldIn.getBlockState(blockpos).getBlock() == this)
                {
                    --i;

                    if (i <= 0)
                    {
                        return;
                    }
                }
            }

            BlockPos blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

            for (int k = 0; k < 4; ++k)
            {
                if (worldIn.isAirBlock(blockpos1) && this.canBlockStay(worldIn, blockpos1, this.getDefaultState()))
                {
                    pos = blockpos1;
                }

                blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
            }

            if (worldIn.isAirBlock(blockpos1) && this.canBlockStay(worldIn, blockpos1, this.getDefaultState()))
            {
                worldIn.setBlockState(blockpos1, this.getDefaultState(), 2);
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
    }

    /**
     * Return true if the block can sustain a Bush
     */
    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return state.isFullBlock();
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());
            return iblockstate.getBlock() == Blocks.MYCELIUM ? true : (iblockstate.getBlock() == Blocks.DIRT && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL ? true : worldIn.getLight(pos) < 13 && iblockstate.getBlock().canSustainPlant(iblockstate, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this));
        }
        else
        {
            return false;
        }
    }
}