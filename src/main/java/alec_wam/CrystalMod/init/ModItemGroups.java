package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {
	
	public static final ItemGroup ITEM_GROUP_BLOCKS = addGroupSafe(new ItemGroup(CrystalMod.resourceDot("blocks")){
		
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModBlocks.crystalBlockGroup.getBlock(EnumCrystalColorSpecial.BLUE));
		}
		
	});
	
	public static final ItemGroup ITEM_GROUP_ITEMS = addGroupSafe(new ItemGroup(CrystalMod.resourceDot("items")){
		
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.crystalGroup.getItem(EnumCrystalColorSpecial.BLUE));
		}
		
	});

	public static synchronized ItemGroup addGroupSafe(ItemGroup newGroup) {
		int index = ItemGroup.getGroupCountSafe();
		if(index == -1) {
			index = ItemGroup.GROUPS.length;
		}
		if (index >= ItemGroup.GROUPS.length) {
			ItemGroup[] tmp = new ItemGroup[index + 1];
			System.arraycopy(ItemGroup.GROUPS, 0, tmp, 0, ItemGroup.GROUPS.length);
			ItemGroup.GROUPS = tmp;
		}
		ItemGroup.GROUPS[index] = newGroup;
		return newGroup;
	}
}
