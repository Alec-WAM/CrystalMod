package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.tools.backpack.gui.OpenType;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BackpackOpenSourceItem implements IBackpackOpenSource {

	public EntityPlayer player;
	public ItemStack backpack;
	public BackpackOpenSourceItem(EntityPlayer player, ItemStack stack){
		this.player = player;
		this.backpack = stack;
	}
	
	@Override
	public ItemStack getBackpack() {
		return backpack;
	}

	@Override
	public void openMainInventory() {
		BlockUtil.openWorksiteGui(player, GuiHandler.GUI_ID_BACKPACK, OpenType.BACK.ordinal(), 0, 0);
	}

	@Override
	public void openUpgradesInventory(int tab) {
		BlockUtil.openWorksiteGui(player, GuiHandler.GUI_ID_BACKPACK, 0, tab, 1);
	}

}
