package alec_wam.CrystalMod.tiles.lamps;

import java.util.Locale;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAdvancedLamp extends EnumBlock<BlockAdvancedLamp.LampType> implements ITileEntityProvider {

	public static final PropertyEnum<LampType> TYPE = PropertyEnum.<LampType>create("type", LampType.class);
	
	public BlockAdvancedLamp() {
		super(Material.GLASS, TYPE, LampType.class);
		setHardness(0.3F);
		setSoundType(SoundType.GLASS);
		setCreativeTab(CrystalMod.tabBlocks);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if(meta == 1)return new TileAdvancedLampDark();
		return new TileAdvancedLamp();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileAdvancedLamp){
			((TileAdvancedLamp)tile).onBlockBreak();
		}
		super.breakBlock(worldIn, pos, state);
	}
	
	public static enum LampType implements IStringSerializable, alec_wam.CrystalMod.util.IEnumMeta {
		PURE, DARK;

		final int meta;
		
		LampType(){
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
