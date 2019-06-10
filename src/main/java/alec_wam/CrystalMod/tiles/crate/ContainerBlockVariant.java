package alec_wam.CrystalMod.tiles.crate;

import com.google.common.base.Preconditions;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.BlockVariantGroup.TileFactory;
import alec_wam.CrystalMod.tiles.ContainerBlockCustom;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ContainerBlockVariant<VARIANT extends Enum<VARIANT>> extends ContainerBlockCustom {

	protected final BlockVariantGroup<? extends Enum<VARIANT>, ? extends ContainerBlockVariant<VARIANT>> variantGroup;
	public final VARIANT type;
	protected TileFactory<VARIANT> tileFactory;
	
	public ContainerBlockVariant(VARIANT type, BlockVariantGroup<? extends Enum<VARIANT>, ? extends ContainerBlockVariant<VARIANT>> variantGroup,Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}
	
	public ContainerBlockVariant<VARIANT> tileFactory(final TileFactory<VARIANT> tileFactory) {
		Preconditions.checkNotNull(tileFactory, "tileFactory");
		this.tileFactory = tileFactory;
		return this;
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		Preconditions.checkNotNull(tileFactory, "TileFactory is null");
		return tileFactory.createTile(type);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

}
