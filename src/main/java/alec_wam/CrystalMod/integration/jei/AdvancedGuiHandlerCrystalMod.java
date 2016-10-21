package alec_wam.CrystalMod.integration.jei;

import java.awt.Rectangle;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.client.util.GuiContainerTabbed;
import mezz.jei.api.gui.IAdvancedGuiHandler;

public class AdvancedGuiHandlerCrystalMod implements IAdvancedGuiHandler<GuiContainerTabbed> {

  public AdvancedGuiHandlerCrystalMod() {
  }

  @Override
  @Nonnull
  public Class<GuiContainerTabbed> getGuiContainerClass() {
    return GuiContainerTabbed.class;
  }

  @Override
  @Nullable
  public List<Rectangle> getGuiExtraAreas(GuiContainerTabbed guiContainer) {
    return guiContainer.getBlockingAreas();
  }

}
