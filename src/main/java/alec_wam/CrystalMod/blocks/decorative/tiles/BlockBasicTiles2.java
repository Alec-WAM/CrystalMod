package alec_wam.CrystalMod.blocks.decorative.tiles;

import java.util.Random;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.util.IEnumMeta;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBasicTiles2 extends EnumBlock<BlockBasicTiles2.BasicTileType2> {

	public static final PropertyEnum<BasicTileType2> TYPE = PropertyEnum.<BasicTileType2>create("type", BasicTileType2.class);
	
	public BlockBasicTiles2() {
		super(Material.ROCK, TYPE, BasicTileType2.class);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setHardness(2f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BasicTileType2.LOG_OAK));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(BasicTileType2 type : BasicTileType2.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(BasicTileType2 type : BasicTileType2.values()) {
			if(type !=BasicTileType2.LAMP_ON){
				list.add(new ItemStack(this, 1, type.getMeta()));
			}
		}
	}
	
	@Override
	public Material getMaterial(IBlockState state){
		BasicTileType2 type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log") || type.unlocalizedName.startsWith("noteblock")){
			return Material.WOOD;
		}
		if(type.unlocalizedName.contains("lamp")){
			return Material.REDSTONE_LIGHT;
		}
		return Material.ROCK;
	}
    
	@SuppressWarnings("deprecation")
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
    {
		BasicTileType2 type = blockState.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log") || type.unlocalizedName.startsWith("noteblock")){
			return Blocks.LOG.getBlockHardness(blockState, worldIn, pos);
		}
		if(type.unlocalizedName.contains("lamp")){
			return Blocks.REDSTONE_LAMP.getBlockHardness(blockState, worldIn, pos);
		}
		return Blocks.FURNACE.getBlockHardness(blockState, worldIn, pos);
    }
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
		IBlockState state = world.getBlockState(pos);
		BasicTileType2 type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log") || type.unlocalizedName.startsWith("noteblock")){
			return Blocks.LOG.getExplosionResistance(world, pos, exploder, explosion);
		}
		if(type.unlocalizedName.contains("lamp")){
			return Blocks.REDSTONE_LAMP.getExplosionResistance(world, pos, exploder, explosion);
		}
		return Blocks.FURNACE.getExplosionResistance(world, pos, exploder, explosion);
    }
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
    {
		BasicTileType2 type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log") || type.unlocalizedName.startsWith("noteblock")){
			return SoundType.WOOD;
		}
		if(type.unlocalizedName.contains("lamp")){
			return SoundType.GLASS;
		}
		return SoundType.STONE;
    }
	
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
		IBlockState state = world.getBlockState(pos);
		BasicTileType2 type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log") || type.unlocalizedName.startsWith("noteblock")){
			return Blocks.LOG.getFlammability(world, pos, face);
		}
		return super.getFlammability(world, pos, face);
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
		BasicTileType2 type = state.getValue(TYPE);
		if(type.unlocalizedName.startsWith("log") || type.unlocalizedName.startsWith("noteblock")){
			return Blocks.LOG.getFireSpreadSpeed(world, pos, face);
		}
		return super.getFireSpreadSpeed(world, pos, face);
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public int getLightValue(IBlockState state)
    {
		BasicTileType2 type = state.getValue(TYPE);
		if(type == BasicTileType2.LAMP_ON || type == BasicTileType2.SEALANTERN){
			return 15;
		}
		return super.getLightValue(state);
	}
	
	@Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
		BasicTileType2 type = state.getValue(TYPE);
		if (!worldIn.isRemote)
        {
            if (type == BasicTileType2.LAMP_ON && !worldIn.isBlockPowered(pos))
            {
                worldIn.setBlockState(pos, getDefaultState().withProperty(TYPE, BasicTileType2.LAMP_OFF), 2);
            }
            else if (type == BasicTileType2.LAMP_OFF && worldIn.isBlockPowered(pos))
            {
                worldIn.setBlockState(pos, getDefaultState().withProperty(TYPE, BasicTileType2.LAMP_ON), 2);
            }
        }
    }

	@Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
		BasicTileType2 type = state.getValue(TYPE);
		if (!worldIn.isRemote)
        {
            if (type == BasicTileType2.LAMP_ON && !worldIn.isBlockPowered(pos))
            {
                worldIn.scheduleUpdate(pos, this, 4);
            }
            else if (type == BasicTileType2.LAMP_OFF && worldIn.isBlockPowered(pos))
            {
                worldIn.setBlockState(pos, getDefaultState().withProperty(TYPE, BasicTileType2.LAMP_ON), 2);
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
    	BasicTileType2 type = state.getValue(TYPE);
		if (!worldIn.isRemote)
        {
            if(type == BasicTileType2.LAMP_ON && !worldIn.isBlockPowered(pos))
            {
                worldIn.setBlockState(pos, getDefaultState().withProperty(TYPE, BasicTileType2.LAMP_OFF), 2);
            }
        }
    }
	
    public static enum BasicTileType2 implements IStringSerializable, IEnumMeta {
		LOG_OAK("log_oak"),
		LOG_BIRCH("log_birch"),
		LOG_SPRUCE("log_spruce"),
		LOG_JUNGLE("log_jungle"),
		LOG_ACACIA("log_acacia"),
		LOG_DARK_OAK("log_dark_oak"),
		NOTEBLOCK("noteblock"),
		PISTON("piston"),
		PISTON_STICKY("piston_sticky"),
		SEALANTERN("sealantern"),
		FURNACE("furnace"),
		LAMP_OFF("lamp_off"),
		LAMP_ON("lamp_on");

		private final String unlocalizedName;
		public final int meta;

		BasicTileType2(String name) {
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
