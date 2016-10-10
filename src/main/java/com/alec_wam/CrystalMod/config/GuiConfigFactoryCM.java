package com.alec_wam.CrystalMod.config;

import java.util.ArrayList;
import java.util.List;

import com.alec_wam.CrystalMod.Config;
import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.util.Lang;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfigFactoryCM extends GuiConfig {

  public GuiConfigFactoryCM(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), CrystalMod.MODID, false, false, Lang.localize("config.title"));
  }

  private static List<IConfigElement> getConfigElements(GuiScreen parent) {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    String[] names = new String[]{
    		Config.CATEGORY_GENERAL,
    		Config.CATEGORY_ENTITY,
    		Config.CATEGORY_MACHINE,
    		Config.CATEGORY_MINIONS
    };
    for (String section : names) {
      list.add(new ConfigElement(CrystalMod.proxy.mainConfig.getCategory(section).setLanguageKey(Lang.prefix+"config." + section.toLowerCase())));
    }

    return list;
  }
}