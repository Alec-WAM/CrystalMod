package alec_wam.CrystalMod.tiles.machine.worksite.gui.elements;

import net.minecraft.item.ItemStack;

public interface ITooltipRenderer
{

	public void handleItemStackTooltipRender(ItemStack stack);

	public void handleElementTooltipRender(Tooltip o);

}
