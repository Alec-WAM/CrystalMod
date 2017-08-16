package alec_wam.CrystalMod.api.guide;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public abstract class GuiGuideBase extends GuiScreen{

    public abstract void renderScaledString(String text, int x, int y, int color, boolean shadow, float scale);

    public abstract void renderSplitScaledString(String text, int x, int y, int color, boolean shadow, float scale, int length);

    public abstract List<GuiButton> getButtonList();

    public abstract int getGuiLeft();

    public abstract int getGuiTop();

    public abstract int getSizeX();

    public abstract int getSizeY();

    public abstract void addOrModifyItemRenderer(ItemStack renderedStack, int x, int y, float scale, boolean shouldTryTransfer);
}
