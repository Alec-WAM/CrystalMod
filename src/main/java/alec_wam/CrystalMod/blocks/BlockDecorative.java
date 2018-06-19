package alec_wam.CrystalMod.blocks;

import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDecorative extends EnumBlock<BlockDecorative.DecorativeBlockType> {

	public static final PropertyEnum<DecorativeBlockType> TYPE = PropertyEnum.<DecorativeBlockType>create("type", DecorativeBlockType.class);
	
	public BlockDecorative() {
		super(Material.ROCK, TYPE, DecorativeBlockType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f).setResistance(10F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, DecorativeBlockType.REDSTONE_BRICKS));
	}
    
	@Override
	public Material getMaterial(IBlockState state)
    {
		DecorativeBlockType type = state.getValue(TYPE);
		if(type == DecorativeBlockType.SQUARE_TURN){
			return Material.IRON;
		}
        return Material.ROCK;
    }
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
    {
		DecorativeBlockType type = state.getValue(TYPE);
		if(type == DecorativeBlockType.SQUARE_TURN){
			return SoundType.METAL;
		}
		return SoundType.STONE;
    }
	
	@Override
	public MapColor getMapColor(IBlockState state)
    {
		DecorativeBlockType type = state.getValue(TYPE);
		if(type == DecorativeBlockType.SQUARE_TURN){
			return MapColor.IRON;
		}
        return MapColor.RED;
    }
	
	public static enum DecorativeBlockType implements IStringSerializable, alec_wam.CrystalMod.util.IEnumMeta{
		REDSTONE_BRICKS, SQUARE_TURN;

		final int meta;
		
		DecorativeBlockType(){
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

}
