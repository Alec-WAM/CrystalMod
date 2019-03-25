package alec_wam.CrystalMod.blocks.decorative.tiles;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.util.IEnumMeta;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBasicTiles extends EnumBlock<BlockBasicTiles.BasicTileType> {

	public static final PropertyEnum<BasicTileType> TYPE = PropertyEnum.<BasicTileType>create("type",
			BasicTileType.class);

	public BlockBasicTiles() {
		super(Material.ROCK, TYPE, BasicTileType.class);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setHardness(2f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BasicTileType.STONE_STONE));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for (BasicTileType type : BasicTileType.values())
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(),
					new ModelResourceLocation(this.getRegistryName(), TYPE.getName() + "=" + type.getName()));
	}

	@Override
	public boolean canProvidePower(IBlockState state)
    {
		BasicTileType type = state.getValue(TYPE);
		return type == BasicTileType.COMPRESSED_REDSTONE;
    }

	@SuppressWarnings("deprecation")
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
		BasicTileType type = blockState.getValue(TYPE);
		return type == BasicTileType.COMPRESSED_REDSTONE ? 15 : super.getWeakPower(blockState, blockAccess, pos, side);
    }
	
	@Override
	public Material getMaterial(IBlockState state) {
		BasicTileType type = state.getValue(TYPE);
		if (type.unlocalizedName.startsWith("stone") || type.unlocalizedName.startsWith("compressed")) {
			return Material.ROCK;
		}
		return Material.IRON;
	}

	@SuppressWarnings("deprecation")
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		BasicTileType type = blockState.getValue(TYPE);
		if (type.unlocalizedName.startsWith("stone") || type.unlocalizedName.startsWith("compressed")) {
			return Blocks.STONE.getBlockHardness(blockState, worldIn, pos);
		}
		return Blocks.DIAMOND_BLOCK.getBlockHardness(blockState, worldIn, pos);
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		IBlockState state = world.getBlockState(pos);
		BasicTileType type = state.getValue(TYPE);
		if (type.unlocalizedName.startsWith("stone")) {
			return Blocks.STONE.getExplosionResistance(world, pos, exploder, explosion);
		}
		if (type.unlocalizedName.startsWith("compressed")) {
			return Blocks.REDSTONE_BLOCK.getExplosionResistance(world, pos, exploder, explosion);
		}
		return Blocks.DIAMOND_BLOCK.getExplosionResistance(world, pos, exploder, explosion);
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
		BasicTileType type = state.getValue(TYPE);
		if (type.unlocalizedName.startsWith("stone")) {
			return SoundType.STONE;
		}
		if (type.unlocalizedName.startsWith("compressed")) {
			return SoundType.STONE;
		}
		return SoundType.METAL;
	}

	public static enum BasicTileType implements IStringSerializable, IEnumMeta {
		STONE_STONE("stone_stone"), 
		STONE_CARVED("stone_carved"), 
		STONE_GRANITE("stone_granite"), 
		STONE_DIORITE("stone_diorite"), 
		STONE_ANDESITE("stone_andesite"), 
		COMPRESSED_REDSTONE("compressed_redstone"), 
		COMPRESSED_LAPIS("compressed_lapis"), 
		COMPRESSED_QUARTZ("compressed_quartz"), 
		COMPRESSED_FLINT("compressed_flint"), 
		METAL_IRON("metal_iron"), 
		METAL_GOLD("metal_gold"), 
		METAL_DIAMOND("metal_diamond"), 
		METAL_EMERALD("metal_emerald");

		private final String unlocalizedName;
		public final int meta;

		BasicTileType(String name) {
			meta = ordinal();
			unlocalizedName = name;
		}

		@Override
		public String getName() {
			return unlocalizedName;
		}

		@Override
		public int getMeta() {
			return meta;
		}

	}

}
