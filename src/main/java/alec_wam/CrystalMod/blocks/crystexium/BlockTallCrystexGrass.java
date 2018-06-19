package alec_wam.CrystalMod.blocks.crystexium;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTallCrystexGrass extends BlockBush implements ICustomModel, net.minecraftforge.common.IShearable {
	protected static final AxisAlignedBB TALL_GRASS_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

	public BlockTallCrystexGrass() {
		super(Material.VINE);
		setHardness(0.0F);
		setSoundType(SoundType.PLANT);
		this.setDefaultState(this.blockState.getBaseState().withProperty(CrystalColors.COLOR_SPECIAL, CrystalColors.Special.BLUE));
	}
	
	@Override
	public void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(CrystalColors.Special type : CrystalColors.Special.values()) {
			list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(CrystalColors.COLOR_SPECIAL, type))));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		for(CrystalColors.Special type : CrystalColors.Special.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "inventory"));
		}
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, CrystalColors.COLOR_SPECIAL);
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		if(meta < 0 || meta >= CrystalColors.Special.values().length) {
			meta = 0;
		}
		CrystalColors.Special color = CrystalColors.Special.values()[meta];
		return this.getDefaultState().withProperty(CrystalColors.COLOR_SPECIAL, color);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(CrystalColors.COLOR_SPECIAL).ordinal();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return TALL_GRASS_AABB;
    }

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        return super.canBlockStay(worldIn, pos, state);
    }

    @Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    @Override
	public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return 1 + random.nextInt(fortune * 2 + 1);
    }

    @Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && stack.getItem() == Items.SHEARS)
        {
            player.addStat(StatList.getBlockStats(this));
            spawnAsEntity(worldIn, pos, new ItemStack(this, 1, state.getValue(CrystalColors.COLOR_SPECIAL).getMeta()));
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    @Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(this, 1, state.getBlock().getMetaFromState(state));
    }
    
    @Override
	public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XYZ;
    }
    
    @Override public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){ return true; }
    @Override
    public NonNullList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, new ItemStack(this, 1, world.getBlockState(pos).getValue(CrystalColors.COLOR_SPECIAL).getMeta()));
    }
    @Override
    public NonNullList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        if (RANDOM.nextInt(8) != 0) return NonNullList.create();
        ItemStack seed = net.minecraftforge.common.ForgeHooks.getGrassSeed(RANDOM, fortune);
        if (!seed.isEmpty())
            return NonNullList.withSize(1, seed);
        else
            return NonNullList.create();
    }

	public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			CrystalColors.Special type = state.getValue(CrystalColors.COLOR_SPECIAL);
			String nameOverride = null;

			nameOverride = state.getBlock().getRegistryName().getResourcePath() + "_" + type.getName();

			ResourceLocation baseLocation = new ResourceLocation("crystalmod", nameOverride);

			return new ModelResourceLocation(baseLocation, "normal");
		}
	}
}
