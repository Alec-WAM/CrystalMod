package alec_wam.CrystalMod.util.tool;

import net.minecraft.item.ItemStack;

public interface IToolProvider {

  ITool getTool(ItemStack stack);

}
