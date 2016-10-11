package alec_wam.CrystalMod.blocks;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;

public class BlockCrystalPlant extends BlockBush
{
	public static enum PlantType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		BLUE, RED, GREEN, DARK;

		final int meta;
		
		PlantType(){
			meta = ordinal();
		}
		
		@Override
		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return this.toString().toLowerCase(Locale.US);
		}
		
	}

    private static final AxisAlignedBB[] AGE_AABB = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.6875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D)};

	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
    public final PlantType TYPE;

    public BlockCrystalPlant(PlantType type)
    {
        super(Material.PLANTS);
        this.TYPE = type;
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setCreativeTab((CreativeTabs)null);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AGE_AABB[((Integer)state.getValue(AGE)).intValue()];
    }

    protected boolean canSustainBush(IBlockState state)
    {
    	return getTypeFromBlock(state) == TYPE;
    }

    @Nonnull
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
      return ModBlocks.crystalPlantType;
    }

    public ItemStack getSeeds(){
    	switch(TYPE){
	        case BLUE : {
	        	return new ItemStack(ModItems.crystalSeedsBlue);
	        }
	        case RED : {
	        	return new ItemStack(ModItems.crystalSeedsRed);
	        }
	        case GREEN : {
	        	return new ItemStack(ModItems.crystalSeedsGreen);
	        }
	        case DARK : {
	        	return new ItemStack(ModItems.crystalSeedsDark);
	        }
    	}
    	return null;
    }
    
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return getSeeds();
    }
    
    public static PlantType getTypeFromBlock(IBlockState state){
    	if(state == null)return null;
    	if(state.getBlock() == ModBlocks.crystal){
        	int meta = state.getBlock().getMetaFromState(state);
        	if(meta == CrystalBlockType.BLUE.getMeta()){
        		return PlantType.BLUE;
        	}else if(meta == CrystalBlockType.RED.getMeta()){
            		return PlantType.RED;
        	}else if(meta == CrystalBlockType.GREEN.getMeta()){
        		return PlantType.GREEN;
        	}else if(meta == CrystalBlockType.DARK.getMeta()){
        		return PlantType.DARK;
        	}
        }else if(state.getBlock() == ModBlocks.crystalIngot){
        	int meta = state.getBlock().getMetaFromState(state);
        	if(meta == CrystalIngotBlockType.BLUE.getMeta()){
        		return PlantType.BLUE;
        	}else if(meta == CrystalIngotBlockType.RED.getMeta()){
            		return PlantType.RED;
        	}else if(meta == CrystalIngotBlockType.GREEN.getMeta()){
        		return PlantType.GREEN;
        	}else if(meta == CrystalIngotBlockType.DARK.getMeta()){
        		return PlantType.DARK;
        	}
        }
    	return null;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        int i = ((Integer)state.getValue(AGE)).intValue();

        if (i < 3 && rand.nextInt(10) == 0)
        {
            state = state.withProperty(AGE, Integer.valueOf(i + 1));
            worldIn.setBlockState(pos, state, 2);
        }

        super.updateTick(worldIn, pos, state, rand);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    @SuppressWarnings("unused")
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        if (false && !worldIn.isRemote)
        {
            int i = 1;

            if (((Integer)state.getValue(AGE)).intValue() >= 3)
            {
                i = 2 + worldIn.rand.nextInt(3);

                if (fortune > 0)
                {
                    i += worldIn.rand.nextInt(fortune + 1);
                }
            }
            ItemStack crop = null;
            PlantType type = TYPE;
        	if(type == PlantType.BLUE){
        		crop = new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata());
        	}else if(type == PlantType.RED){
        		crop = new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata());
        	}else if(type == PlantType.GREEN){
        		crop = new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata());
        	}else if(type == PlantType.DARK){
        		crop = new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata());
        	}
            spawnAsEntity(worldIn, pos, getSeeds());
            if(crop !=null){
	            for (int j = 0; j < i; ++j)
	            {
	            	spawnAsEntity(worldIn, pos, crop);
	            }
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(AGE)).intValue();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE});
    }

    @Override
    public java.util.List<ItemStack> getDrops(net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        int count = 1;

        if (((Integer)state.getValue(AGE)) >= 3)
        {
            count = 2 + rand.nextInt(3) + (fortune > 0 ? rand.nextInt(fortune + 1) : 0);
        }

        ItemStack crop = null;
        PlantType type = TYPE;
    	if(type == PlantType.BLUE){
    		crop = new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata());
    	}else if(type == PlantType.RED){
    		crop = new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata());
    	}else if(type == PlantType.GREEN){
    		crop = new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata());
    	}else if(type == PlantType.DARK){
    		crop = new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata());
    	}
        ret.add(getSeeds());
        if(crop !=null){
	        for (int i = 0; i < count; i++)
	        {
	            ret.add(crop);
	        }
        }

        return ret;
    }
}