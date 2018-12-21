package alec_wam.CrystalMod.blocks.decorative;

import alec_wam.CrystalMod.blocks.ItemBlockMeta;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class ItemBlockLanternDyed extends ItemBlockMeta {

	public ItemBlockLanternDyed(Block block) {
		super(block);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return block.getUnlocalizedName();
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack){
		String name = ItemUtil.getDyeName(EnumDyeColor.byMetadata(stack.getMetadata()));
		return String.format(Lang.translateToLocal(block.getUnlocalizedName() + ".name"), name);
	}

}
