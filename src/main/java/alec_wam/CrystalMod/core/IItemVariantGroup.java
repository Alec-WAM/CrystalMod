package alec_wam.CrystalMod.core;

import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.Iterator;

public interface IItemVariantGroup<VARIANT extends Enum<VARIANT> & IStringSerializable, ITEM extends Item> {
	String getGroupName();

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

	Collection<ITEM> getItems();

	void registerItems(IForgeRegistry<Item> registry);
}
