package alec_wam.CrystalMod.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.registries.IForgeRegistry;

public interface IBlockVariantGroup<V extends Enum<V> & IStringSerializable, B extends Block> {

	String getGroupName();

	default V cycleVariant(final V currentVariant) {
		final Iterator<V> iterator = getVariants().iterator();

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

	Iterable<V> getVariants();

	Collection<B> getBlocks();

	void registerBlocks(IForgeRegistry<Block> registry);

	List<? extends ItemBlock> registerItems(IForgeRegistry<Item> registry);
}
