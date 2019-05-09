package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {
	
	public static final ItemGroup ITEM_GROUP_BLOCKS = new ItemGroup(CrystalMod.resourceDot("blocks")){
		
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModBlocks.crystalBlockGroup.getBlock(EnumCrystalColorSpecial.BLUE));
		}
		
	};
	
	public static final ItemGroup ITEM_GROUP_ITEMS = new ItemGroup(CrystalMod.resourceDot("items")){
		
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.crystalGroup.getItem(EnumCrystalColorSpecial.BLUE));
		}
		
	};
}
