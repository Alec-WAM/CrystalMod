package alec_wam.CrystalMod.blocks.crops;

import java.util.Random;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Util;
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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalBerryBush extends BlockBush implements IGrowable, ICustomModel {

	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
    public final PlantType TYPE;

    public BlockCrystalBerryBush(PlantType type)
    {
        super(Material.LEAVES);
        this.TYPE = type;
        this.setHardness(0.3f);
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setCreativeTab(CrystalMod.tabCrops);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "age=3"));
	}

	public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;

			builder.append(AGE.getName());
			builder.append("=");
			builder.append(state.getValue(AGE));

			nameOverride = state.getBlock().getRegistryName().getResourcePath();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);

			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	float pixel = 1.0f / 16f;
        return new AxisAlignedBB(pixel*2, 0, pixel*2, pixel*14, pixel*12, pixel*14);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
    	return getBoundingBox(blockState, worldIn, pos);
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
    	return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT;
    }

    @Nonnull
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
      return EnumPlantType.Plains;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        int i = state.getValue(AGE).intValue();

        if (i < 3 && rand.nextInt(4) == 0)
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
        return state.getValue(AGE).intValue();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE});
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	if((state.getValue(AGE)) < 3)return false;
    	
    	if(worldIn.isRemote){
    		return true;
    	}
    	
    	Random rand = worldIn instanceof World ? worldIn.rand : Util.rand;
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
    	return new ItemStack(ModItems.crystalBerry, 1, TYPE.getMeta());
    }
    
    @Override
    public java.util.List<ItemStack> getDrops(net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        java.util.List<ItemStack> ret = super.getDrops(world, pos, state, fortune);
        Random rand = world instanceof World ? ((World)world).rand : Util.rand;
        int count = 0;

        if ((state.getValue(AGE)) >= 3)
        {
            count = 1 + rand.nextInt(2) + (fortune > 0 ? rand.nextInt(fortune + 1) : 0);
        }

        ItemStack crop = getCrop();
        if(ItemStackTools.isValid(crop)){
	        for (int i = 0; i < count; i++)
	        {
	            ret.add(crop);
	        }
        }

        return ret;
    }

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return state.getValue(AGE) < 3;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return worldIn.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		if(state.getValue(AGE) < 3){
			worldIn.setBlockState(pos, state.cycleProperty(AGE), 4);
		}
	}
	
}
