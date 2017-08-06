package alec_wam.CrystalMod.config;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class ConfigFactoryCM implements IModGuiFactory{

  @Override
  public void initialize(Minecraft minecraftInstance) {
    ;
  }

  @Override
  public Class<? extends GuiScreen> mainConfigGuiClass() {
    return GuiConfigFactoryCM.class;
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
    return null;
  }

  @SuppressWarnings("deprecation")
  @Override
  public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
    return null;
  }

  @Override
  public boolean hasConfigGui() {
	  return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {
	  return new GuiConfigFactoryCM(parentScreen);
  }
}
