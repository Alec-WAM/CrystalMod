package alec_wam.CrystalMod.blocks.crops;

import java.util.Random;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.world.WorldGenBambooTree;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class BlockNormalSapling extends BlockSapling implements ICustomModel {

	public static final PropertyEnum<SaplingType> VARIANT = PropertyEnum.<SaplingType>create("variant", SaplingType.class);

	public BlockNormalSapling() {
		setCreativeTab(CrystalMod.tabCrops);
		setDefaultState(this.blockState.getBaseState());
		this.setSoundType(SoundType.PLANT);
	}

	@Override
	public void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(SaplingType type : SaplingType.values()) {
			list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(VARIANT, type))));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new SaplingBlockStateMapper());
		for(SaplingType type : SaplingType.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "inventory"));
		}
	}

	public static class SaplingBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			SaplingType type = state.getValue(VARIANT);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;

			builder.append(STAGE.getName());
			builder.append("=");
			builder.append(state.getValue(STAGE));

			nameOverride = state.getBlock().getRegistryName().getResourcePath() + "_" + type.getName();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);

			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}

	//Because Default is Weird
	@Override
    public String getLocalizedName()
	{
		return I18n.translateToLocal(this.getUnlocalizedName() + ".name");
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		// TYPE has to be included because of the BlockSapling constructor.. but it's never used.
		return new BlockStateContainer(this, VARIANT, STAGE, TYPE);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		if(meta < 0 || meta >= SaplingType.values().length) {
			meta = 0;
		}
		SaplingType grass = SaplingType.values()[meta];
		return this.getDefaultState().withProperty(VARIANT, grass);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, @Nonnull BlockPos pos) {
		return false;
	}

	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
		IBlockState iblockstate = world.getBlockState(pos);
		int meta = iblockstate.getBlock().getMetaFromState(iblockstate);
		return new ItemStack(Item.getItemFromBlock(this), 1, meta);
	}

	@Override
	public void generateTree(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
		if(!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
			return;
		}
		SaplingType type = state.getValue(VARIANT);
		WorldGenerator gen = null;
		if(type == SaplingType.BAMBOO){
			gen = new WorldGenBambooTree(false);
		}		
		if(gen == null)return;
		// replace sapling with air
		worldIn.setBlockToAir(pos);
		if (!gen.generate(worldIn, rand, pos))
		{
			worldIn.setBlockState(pos, state, 4);
		}
	}
	
	public static enum SaplingType implements IEnumMeta, IStringSerializable {
    	BAMBOO;

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		@Override
		public int getMeta() {
			return ordinal();
		}

		public static SaplingType byMetadata(int meta)
        {
            if (meta < 0 || meta >= values().length)
            {
                meta = 0;
            }

            return values()[meta];
        }
    }
}
