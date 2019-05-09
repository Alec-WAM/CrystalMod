package alec_wam.CrystalMod.util;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.resources.I18n;


public class Lang {
  
  public static String localize(String s) {
    return localize(s, true);
  }	
  
  public static String localizeFormat(String key, Object... format) {
	  String s = localize(key);
	  return String.format(s, format);
  }

  public static String localize(String s, boolean appendCM) {
    if(appendCM) {
      s = CrystalMod.resourceDot(s);
    }
    return translateToLocal(s);
  }

  public static String translateToLocal(String key) {
	  if (I18n.hasKey(key)) {
		  return I18n.format(key);
	  } else {
		  return key;
	  }
  }

  public static String translateToLocalFormatted(String key, Object... format) {
	  if (I18n.hasKey(key)) {
		  return I18n.format(key, format);
	  } else {
		  return key;
	  }
  }
  
  public static boolean canBeTranslated(String string){
	  String translated = translateToLocal(string);
	  return string != translated;
  }
  
  public static String[] localizeList(String string) {
    String s = localize(string);
    return s.split("\\|");
  }

}