package alec_wam.CrystalMod.blocks;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCompressed extends EnumBlock<BlockCompressed.CompressedBlockType> {

	public static final PropertyEnum<CompressedBlockType> TYPE = PropertyEnum.<CompressedBlockType>create("type", CompressedBlockType.class);
	
	public BlockCompressed() {
		super(Material.ROCK, TYPE, CompressedBlockType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, CompressedBlockType.FLINT));
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(CompressedBlockType type : CompressedBlockType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
	
	@Override
	public Material getMaterial(IBlockState state){
		CompressedBlockType type = state.getValue(TYPE);
		if(type == CompressedBlockType.GUNPOWDER){
			return Material.SAND;
		}
		if(type == CompressedBlockType.FLINT){
			return Material.ROCK;
		}
		if(type == CompressedBlockType.CHARCOAL){
			return Material.ROCK;
		}
		if(type == CompressedBlockType.BLAZEROD){
			return Material.IRON;
		}
		return super.getMaterial(state);
	}
    
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
    {
		CompressedBlockType type = blockState.getValue(TYPE);
		if(type == CompressedBlockType.GUNPOWDER){
			return 0.5f;
		}
		if(type == CompressedBlockType.FLINT){
			return 4.0F;
		}
		if(type == CompressedBlockType.CHARCOAL){
			return 5.0F;
		}
		if(type == CompressedBlockType.BLAZEROD){
			return 5.0F;
		}
		return super.getBlockHardness(blockState, worldIn, pos);
    }
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
		IBlockState state = world.getBlockState(pos);
		CompressedBlockType type = state.getValue(TYPE);
		if(type == CompressedBlockType.FLINT){
			return 6.0F;
		}
		if(type == CompressedBlockType.CHARCOAL){
			return 6.0F;
		}
		if(type == CompressedBlockType.BLAZEROD){
			return 6.0F;
		}
		return super.getExplosionResistance(world, pos, exploder, explosion);
    }
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
    {
		CompressedBlockType type = state.getValue(TYPE);
		if(type == CompressedBlockType.GUNPOWDER){
			return SoundType.SAND;
		}
		if(type == CompressedBlockType.FLINT){
			return SoundType.STONE;
		}
		if(type == CompressedBlockType.CHARCOAL){
			return SoundType.STONE;
		}
		if(type == CompressedBlockType.BLAZEROD){
			return SoundType.METAL;
		}
		return super.getSoundType(state, world, pos, entity);
    }
	
    public static enum CompressedBlockType implements IStringSerializable, IEnumMeta {
		FLINT("flint"),
		GUNPOWDER("gunpowder"),
		CHARCOAL("charcoal"),
		BLAZEROD("blazerod");

		private final String unlocalizedName;
		public final int meta;

		CompressedBlockType(String name) {
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
