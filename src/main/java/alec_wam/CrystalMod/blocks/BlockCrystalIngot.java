package alec_wam.CrystalMod.blocks;

import java.util.Locale;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockCrystalIngot extends EnumBlock<BlockCrystalIngot.CrystalIngotBlockType> {

	public static final PropertyEnum<CrystalIngotBlockType> TYPE = PropertyEnum.<CrystalIngotBlockType>create("type", CrystalIngotBlockType.class);
	
	public BlockCrystalIngot() {
		super(Material.IRON, TYPE, CrystalIngotBlockType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f).setResistance(10F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, CrystalIngotBlockType.BLUE));
	}
	
	@Override
 	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
 		return true;
 	}
    
	public static enum CrystalIngotBlockType implements IStringSerializable, alec_wam.CrystalMod.util.IEnumMeta{
		BLUE, RED, GREEN, DARK, PURE, DARKIRON;

		final int meta;
		
		CrystalIngotBlockType(){
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
