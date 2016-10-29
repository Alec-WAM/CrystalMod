package alec_wam.CrystalMod.integration.jei;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.estorage.INetworkGui;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import mezz.jei.gui.Focus;
import mezz.jei.input.IShowsRecipeFocuses;

public class PanelFocusWrapper implements IShowsRecipeFocuses{

	@Nullable
	@Override
	public Focus<ItemStack> getFocusUnderMouse(int mouseX, int mouseY) {
		GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
		if (!(guiScreen instanceof INetworkGui)) {
			return null;
		}
		ItemStackData data = ((INetworkGui)guiScreen).getDataUnderMouse(mouseX, mouseY);
		if (data != null && data.stack !=null) {
			return new Focus<ItemStack>(data.stack);
		}
		return null;
	}

	@Override
	public boolean canSetFocusWithMouse() {
		return false;
	}

}
