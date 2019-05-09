package alec_wam.CrystalMod.tiles.crate;

import com.google.common.base.Preconditions;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.BlockVariantGroup.TileFactory;
import alec_wam.CrystalMod.tiles.BlockContainerCustom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.IBlockReader;

public class BlockContainerVariant<VARIANT extends Enum<VARIANT>> extends BlockContainerCustom {

	protected final BlockVariantGroup<? extends Enum<VARIANT>, ? extends BlockContainerVariant<VARIANT>> variantGroup;
	public final VARIANT type;
	protected TileFactory<VARIANT> tileFactory;
	
	public BlockContainerVariant(VARIANT type, BlockVariantGroup<? extends Enum<VARIANT>, ? extends BlockContainerVariant<VARIANT>> variantGroup,Properties properties) {
		super(properties);
		this.type = type;
		this.variantGroup = variantGroup;
	}
	
	public BlockContainerVariant<VARIANT> tileFactory(final TileFactory<VARIANT> tileFactory) {
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
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

}
