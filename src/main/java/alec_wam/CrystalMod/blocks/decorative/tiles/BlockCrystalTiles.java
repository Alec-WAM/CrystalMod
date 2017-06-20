package alec_wam.CrystalMod.blocks.decorative.tiles;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalTiles extends EnumBlock<BlockCrystalTiles.CrystalTileType> {

	public static final PropertyEnum<CrystalTileType> TYPE = PropertyEnum.<CrystalTileType>create("type", CrystalTileType.class);
	
	public BlockCrystalTiles() {
		super(Material.ROCK, TYPE, CrystalTileType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, CrystalTileType.INGOT_BLUE));
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(CrystalTileType type : CrystalTileType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
	
	@Override
	public Material getMaterial(IBlockState state){
		CrystalTileType type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log")){
			return Material.WOOD;
		}
		return Material.IRON;
	}
    
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
    {
		CrystalTileType type = blockState.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log")){
			return ModBlocks.crystalLog.getBlockHardness(blockState, worldIn, pos);
		}
		return ModBlocks.crystalIngot.getBlockHardness(blockState, worldIn, pos);
    }
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
		IBlockState state = world.getBlockState(pos);
		CrystalTileType type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log")){
			return ModBlocks.crystalLog.getExplosionResistance(world, pos, exploder, explosion);
		}
		return ModBlocks.crystalIngot.getExplosionResistance(world, pos, exploder, explosion);
    }
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
    {
		CrystalTileType type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log")){
			return SoundType.WOOD;
		}
		return SoundType.METAL;
    }
	
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
		IBlockState state = world.getBlockState(pos);
		CrystalTileType type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log")){
			return ModBlocks.crystalLog.getFlammability(world, pos, face);
		}
		return ModBlocks.crystalIngot.getFlammability(world, pos, face);
    }

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return getFlammability(world, pos, face) > 0;
    }

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
		IBlockState state = world.getBlockState(pos);
		CrystalTileType type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log")){
			return ModBlocks.crystalLog.getFireSpreadSpeed(world, pos, face);
		}
		return ModBlocks.crystalIngot.getFireSpreadSpeed(world, pos, face);
    }
	
    public static enum CrystalTileType implements IStringSerializable, IEnumMeta {
		INGOT_BLUE("ingot_blue"),
		INGOT_RED("ingot_red"),
		INGOT_GREEN("ingot_green"),
		INGOT_DARK("ingot_dark"),
		INGOT_PURE("ingot_pure"),
		INGOT_DIRON("ingot_diron"),
		LOG_BLUE("log_blue"),
		LOG_RED("log_red"),
		LOG_GREEN("log_green"),
		LOG_DARK("log_dark");

		private final String unlocalizedName;
		public final int meta;

		CrystalTileType(String name) {
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
