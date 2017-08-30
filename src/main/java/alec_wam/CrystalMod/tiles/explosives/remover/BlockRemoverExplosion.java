package alec_wam.CrystalMod.tiles.explosives.remover;

import java.util.Locale;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.tiles.explosives.remover.TileRemoverExplosion.RemovingType;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockRemoverExplosion extends EnumBlock<BlockRemoverExplosion.RemoverType> implements ITileEntityProvider {

	public static final PropertyEnum<RemoverType> TYPE = PropertyEnum.<RemoverType>create("type", RemoverType.class);
	
	public BlockRemoverExplosion() {
		super(Material.TNT, TYPE, RemoverType.class);
		setHardness(1.5F);
		setCreativeTab(CrystalMod.tabBlocks);
		setSoundType(SoundType.PLANT);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileRemoverExplosion(RemovingType.values()[meta]);
	}
	
	public static enum RemoverType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta {
		REDSTONE, WATER, XP;

		final int meta;
		
		RemoverType(){
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
