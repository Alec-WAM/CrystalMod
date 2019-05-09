package alec_wam.CrystalMod.core;

import java.util.Arrays;

import com.google.common.base.Preconditions;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;

public class TileVariantGroup<VARIANT extends Enum<VARIANT> & IStringSerializable, TILE extends TileEntity> implements ITileVariantGroup<VARIANT, TILE> {
	private final Iterable<VARIANT> variants;
	private final TileFactory<VARIANT, TILE> tileFactory;

	private TileVariantGroup(final Iterable<VARIANT> variants, final TileFactory<VARIANT, TILE> tileFactory) {
		this.variants = variants;
		this.tileFactory = tileFactory;
	}

	@Override
	public Iterable<VARIANT> getVariants() {
		return variants;
	}

	public TileFactory<VARIANT, TILE> getTileFactory(){
		return tileFactory;
	}
	
	@FunctionalInterface
	public interface TileFactory<VARIANT extends Enum<VARIANT> & IStringSerializable, TILE extends TileEntity> {
		TILE createTile(VARIANT variant);
	}

	public static class Builder<VARIANT extends Enum<VARIANT> & IStringSerializable, TILE extends TileEntity> {
		private Iterable<VARIANT> variants;
		private TileFactory<VARIANT, TILE> tileFactory;

		public static <VARIANT extends Enum<VARIANT> & IStringSerializable, TILE extends TileEntity> TileVariantGroup.Builder<VARIANT, TILE> create() {
			return new TileVariantGroup.Builder<>();
		}

		private Builder() {
		}

		public TileVariantGroup.Builder<VARIANT, TILE> variants(final Iterable<VARIANT> variants) {
			Preconditions.checkNotNull(variants, "variants");
			this.variants = variants;
			return this;
		}

		public TileVariantGroup.Builder<VARIANT, TILE> variants(final VARIANT[] variants) {
			Preconditions.checkNotNull(variants, "variants");
			return variants(Arrays.asList(variants));
		}
		
		public TileVariantGroup.Builder<VARIANT, TILE> tileFactory(final TileFactory<VARIANT, TILE> tileFactory) {
			Preconditions.checkNotNull(tileFactory, "tileFactory");
			this.tileFactory = tileFactory;
			return this;
		}

		public TileVariantGroup<VARIANT, TILE> build() {
			Preconditions.checkState(variants != null, "Variants not provided");
			Preconditions.checkState(tileFactory != null, "TileFactory not provided");

			return new TileVariantGroup<>(variants, tileFactory);
		}
	}
}