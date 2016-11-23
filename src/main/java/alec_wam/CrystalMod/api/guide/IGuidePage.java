package alec_wam.CrystalMod.api.guide;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface IGuidePage {

    void getItemStacksForPage(List<ItemStack> list);

    void getFluidStacksForPage(List<FluidStack> list);

    IGuideChapter getChapter();

    void setChapter(IGuideChapter chapter);

    String getInfoText();

    @SideOnly(Side.CLIENT)
    void mouseClicked(GuiGuideBase gui, int mouseX, int mouseY, int mouseButton);

    @SideOnly(Side.CLIENT)
    void mouseReleased(GuiGuideBase gui, int mouseX, int mouseY, int state);

    @SideOnly(Side.CLIENT)
    void mouseClickMove(GuiGuideBase gui, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick);

    @SideOnly(Side.CLIENT)
    void actionPerformed(GuiGuideBase gui, GuiButton button);

    @SideOnly(Side.CLIENT)
    void initGui(GuiGuideBase gui, int startX, int startY);

    @SideOnly(Side.CLIENT)
    void updateScreen(GuiGuideBase gui, int startX, int startY, int pageTimer);

    @SideOnly(Side.CLIENT)
    void drawScreenPre(GuiGuideBase gui, int startX, int startY, int mouseX, int mouseY, float partialTicks);

    @SideOnly(Side.CLIENT)
    void drawScreenPost(GuiGuideBase gui, int startX, int startY, int mouseX, int mouseY, float partialTicks);

    boolean shouldBeOnLeftSide();

    String getIdentifier();

    IGuidePage addTextReplacement(String key, String value);

    IGuidePage addTextReplacement(String key, float value);

    IGuidePage addTextReplacement(String key, int value);
}
