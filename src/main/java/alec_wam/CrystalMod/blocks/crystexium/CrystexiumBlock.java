package alec_wam.CrystalMod.blocks.crystexium;

import java.util.Locale;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CrystexiumBlock extends EnumBlock<CrystexiumBlock.CrystexiumBlockType> {

	public static final PropertyEnum<CrystexiumBlockType> TYPE = PropertyEnum.<CrystexiumBlockType>create("type", CrystexiumBlockType.class);
	
	public CrystexiumBlock(){
		super(Material.ROCK, TYPE, CrystexiumBlockType.class);
		//Normal Iron
		//Bricks Iron+2
		//Must be at least diamond
		setHardness(3.0F).setResistance(5.0F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		IBlockState other = worldIn.getBlockState(pos.offset(side));
		if(other.getBlock() == this && getMetaFromState(state) == getMetaFromState(other)){
			return false;
		}
		return super.shouldSideBeRendered(state, worldIn, pos, side);
	}
	
	public static enum CrystexiumBlockType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		NORMAL, BRICK, SQUARE_BRICK;

		final int meta;
		
		CrystexiumBlockType(){
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
