package alec_wam.CrystalMod.blocks.crops;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.FluidUtil;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFlowerLilyPad extends BlockBush implements IGrowable, ICustomModel 
{
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    protected static final AxisAlignedBB LILY_PAD_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.09375D, 0.9375D);

    public BlockFlowerLilyPad()
    {
    	super(Material.PLANTS);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setSoundType(new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_GRASS_BREAK, SoundEvents.BLOCK_GRASS_STEP, SoundEvents.BLOCK_WATERLILY_PLACE, SoundEvents.BLOCK_GRASS_HIT, SoundEvents.BLOCK_GRASS_FALL));
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        disableStats();
    }
    
    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return net.minecraftforge.common.EnumPlantType.Water;
    }
    
    @SuppressWarnings("deprecation")
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
    	if (canBlockStay(worldIn, pos, state))
        {
        	List<BlockPos> validPos = Lists.newArrayList();
        	
        	for(EnumFacing facing : EnumFacing.HORIZONTALS){
        		BlockPos offPos = pos.offset(facing);
        		if(Blocks.WATERLILY.canPlaceBlockAt(worldIn, offPos)){
        			validPos.add(offPos);
        		}
        	}
        	
            if (!validPos.isEmpty())
            {
                int j = state.getValue(AGE).intValue();
                if (j == 15)
                {
                	BlockPos lilyPos = validPos.get(0);
                    worldIn.setBlockState(lilyPos, Blocks.WATERLILY.getDefaultState());
                    worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(0)), 2);
                    worldIn.playSound(null, lilyPos, Blocks.WATERLILY.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 0.6f, 0.8f);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 2);
                }
            }
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
    {
    	if (!(entityIn instanceof EntityBoat))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, LILY_PAD_AABB);
        }
    }

    /**
     * Called When an Entity Collided with the Block
     */
    @Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);

        if (entityIn instanceof EntityBoat)
        {
            worldIn.destroyBlock(new BlockPos(pos), true);
        }
    }

    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return LILY_PAD_AABB;
    }

    /**
     * Return true if the block can sustain a Bush
     */
    @Override
	protected boolean canSustainBush(IBlockState state)
    {
        return true;
    }

    @Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        return canPlaceBlockAt(worldIn, pos);
    }
    
    @Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
    	if (pos.getY() >= 0 && pos.getY() < 256)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());
            Material material = iblockstate.getMaterial();
            return material == Material.WATER && FluidUtil.isFluidSource(worldIn, pos.down(), iblockstate) || material == Material.ICE;
        }
        else
        {
            return false;
        }
    }
    
    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE});
    }
    
    @Override
	public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE).intValue();
    }
    
    @Override
	public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		int age = state.getValue(AGE).intValue();
		return age < 15;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos,	IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int age = state.getValue(AGE).intValue();
		int incr = MathHelper.getInt(worldIn.rand, 2, 5);
		int newAge = Integer.valueOf(age + incr);
		if(newAge > 15){
			newAge = 15;
		}
		worldIn.setBlockState(pos, state.withProperty(AGE, newAge), 2);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(Item.getItemFromBlock(this));
		ModelLoader.setCustomStateMapper(this, new LilypadBlockStateMapper());
	}
	
	public class LilypadBlockStateMapper extends StateMapperBase {

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return new ModelResourceLocation(state.getBlock().getRegistryName(), "normal");
		}

	}
}