package alec_wam.CrystalMod.core;

import java.util.Iterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;

public interface ITileVariantGroup<VARIANT extends Enum<VARIANT> & IStringSerializable, TILE extends TileEntity> {
	default VARIANT cycleVariant(final VARIANT currentVariant) {
		final Iterator<VARIANT> iterator = getVariants().iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equals(currentVariant)) {
				if (iterator.hasNext()) {
					return iterator.next();
				}

				return getVariants().iterator().next();
			}
		}

		return iterator.next();
	}

	Iterable<VARIANT> getVariants();
}
