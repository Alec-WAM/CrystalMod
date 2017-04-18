package alec_wam.CrystalMod.blocks.crops;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Util;
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
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AGE_AABB[((Integer)state.getValue(AGE)).intValue()];
    }

    @Override
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
    	return ItemStackTools.getEmptyStack();
    }
    
    @Override
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

    @Override
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
    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(AGE)).intValue();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE});
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	if(((Integer)state.getValue(AGE)) < 3)return false;
    	
    	if(worldIn.isRemote){
    		return true;
    	}
    	
    	Random rand = worldIn instanceof World ? ((World)worldIn).rand : Util.rand;
    	int fortune = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FORTUNE, playerIn);
    	int count = 1 + rand.nextInt(2) + (fortune > 0 ? rand.nextInt(fortune + 1) : 0);
    	ItemStack crop = getCrop();
    	worldIn.setBlockState(pos, getDefaultState());
    	if(ItemStackTools.isValid(crop)){
    		for (int i = 0; i < count; i++)
    		{
    			ItemUtil.spawnItemInWorldWithoutMotion(worldIn, crop, pos);
    		}
    	}
    	return true;
    }
    
    public ItemStack getCrop(){
    	switch(TYPE){
    		case BLUE : return new ItemStack(ModItems.crystals, 1, CrystalType.BLUE_SHARD.getMetadata());
    		case RED : return new ItemStack(ModItems.crystals, 1, CrystalType.RED_SHARD.getMetadata());
    		case GREEN : return new ItemStack(ModItems.crystals, 1, CrystalType.GREEN_SHARD.getMetadata());
    		case DARK : return new ItemStack(ModItems.crystals, 1, CrystalType.DARK_SHARD.getMetadata());
    	}
    	return ItemStackTools.getEmptyStack();
    }
    
    @Override
    public java.util.List<ItemStack> getDrops(net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        Random rand = world instanceof World ? ((World)world).rand : Util.rand;
        int count = 0;

        if (((Integer)state.getValue(AGE)) >= 3)
        {
            count = 3 + rand.nextInt(3) + (fortune > 0 ? rand.nextInt(fortune + 1) : 0);
        }

        ItemStack crop = getCrop();
        ret.add(getSeeds());
        if(ItemStackTools.isValid(crop)){
	        for (int i = 0; i < count; i++)
	        {
	            ret.add(crop);
	        }
        }

        return ret;
    }
}