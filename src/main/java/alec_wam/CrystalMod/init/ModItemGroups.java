package alec_wam.CrystalMod.init;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {
	
	public static final ItemGroup ITEM_GROUP_BLOCKS = new ItemGroup(CrystalMod.resourceDot("blocks")){
		
		public final ItemStack icon = new ItemStack(ModBlocks.crystalBlockGroup.getBlock(EnumCrystalColorSpecial.BLUE));
		
		@Override
		public ItemStack createIcon() {
			return icon;
		}
		
	};
	
	public static final ItemGroup ITEM_GROUP_ITEMS = new ItemGroup(CrystalMod.resourceDot("items")){
		
		public final ItemStack icon = new ItemStack(ModItems.crystalGroup.getItem(EnumCrystalColorSpecial.BLUE));
		
		@Override
		public ItemStack createIcon() {
			return icon;
		}
		
	};
}
