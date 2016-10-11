package alec_wam.CrystalMod.blocks;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.world.WorldGenCrystalTree;

public class BlockCrystalSapling extends BlockSapling implements ICustomModel {

  public static final PropertyEnum<BlockCrystalLog.WoodType> VARIANT = PropertyEnum.<BlockCrystalLog.WoodType>create("variant", BlockCrystalLog.WoodType.class);

  public BlockCrystalSapling() {
    setCreativeTab(CrystalMod.tabItems);
    setDefaultState(this.blockState.getBaseState());
    this.setSoundType(SoundType.PLANT);
  }

  @Override
  public void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, @Nonnull List<ItemStack> list) {
    for(WoodType type : WoodType.values()) {
      list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(VARIANT, type))));
    }
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
  		ModelLoader.setCustomStateMapper(this, new SaplingBlockStateMapper());
		for(WoodType type : WoodType.values()){
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
			WoodType type = state.getValue(VARIANT);
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
    if(meta < 0 || meta >= WoodType.values().length) {
      meta = 0;
    }
    WoodType grass = WoodType.values()[meta];
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
    WoodType type = (WoodType)state.getValue(VARIANT);
    WorldGenerator gen = (WorldGenerator)(new WorldGenCrystalTree(true, MathHelper.getRandomIntegerInRange(rand, 4, 6), type, false));

    // replace sapling with air
    worldIn.setBlockToAir(pos);
    if (!gen.generate(worldIn, rand, pos))
    {
        worldIn.setBlockState(pos, state, 4);
    }
  }
}
