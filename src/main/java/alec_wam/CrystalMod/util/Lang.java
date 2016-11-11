package alec_wam.CrystalMod.util;

import java.util.IllegalFormatException;

import net.minecraft.util.text.translation.I18n;


@SuppressWarnings("deprecation")
public class Lang {
  
  public static final String prefix = "crystalmod.";

  public static String localize(String s) {
    return localize(s, true);
  }
  
  public static String localizeFormat(String key, Object... format) {
	  String s = localize(key);
	  try {
		  return String.format(s, format);
	  } catch (IllegalFormatException e) {
		  String errorMessage = "Format error: " + s;
		  ModLogger.warning(errorMessage, e);
		  return errorMessage;
	  }
  }

  public static String localize(String s, boolean appendCM) {
    if(appendCM) {
      s = prefix + s;
    }
    return translateToLocal(s);
  }

  public static String translateToLocal(String key) {
	  if (I18n.canTranslate(key)) {
		  return I18n.translateToLocal(key);
	  } else {
		  return I18n.translateToFallback(key);
	  }
  }

  public static String translateToLocalFormatted(String key, Object... format) {
	  String s = translateToLocal(key);
	  try {
		  return String.format(s, format);
	  } catch (IllegalFormatException e) {
		  String errorMessage = "Format error: " + s;
		  ModLogger.warning(errorMessage, e);
		  return errorMessage;
	  }
  }
  
  public static String[] localizeList(String string) {
    String s = localize(string);
    return s.split("\\|");
  }

}