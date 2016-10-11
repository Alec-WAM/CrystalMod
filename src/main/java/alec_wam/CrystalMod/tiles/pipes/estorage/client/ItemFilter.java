package alec_wam.CrystalMod.tiles.pipes.estorage.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;

public abstract class ItemFilter {

  private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s+");

  public abstract boolean matches(ItemStackData entry);

  public static ItemFilter parse(String filter, Locale locale) {
    ArrayList<ItemFilter> list = new ArrayList<ItemFilter>();

    String[] parts = SPLIT_PATTERN.split(filter);
    /*try{
	    for(int s = 0; s < parts.length; s++){
		    parts[s] = parts[s].split("\\|");
	    }
    }catch(Exception e){
    	e.printStackTrace();
    }*/
    for(String part : parts) {
      if(part.startsWith("@")) {
        part = part.substring(1);
        if(!part.isEmpty()) {
          list.add(new ModFilter(part, locale));
        }
      }
      else if(part.startsWith("$")) {
    	  part = part.substring(1);
    	  if(!part.isEmpty()) {
    		  list.add(new OreFilter(part, locale));
    	  }
      }
      else if(!part.isEmpty()) {
    	String[] fixed = new String[]{part}; 
    	try{
    		fixed = part.split("\\|");
        }catch(Exception e){
        	e.printStackTrace();
        }
    	for(String fix : fixed){
    		if(!fix.isEmpty()) {
    			list.add(new NameFilter(fix, locale));
    		}
    	}
      }
    }

    if(list.isEmpty()) {
      return null;
    }
    if(list.size() == 1) {
      return list.get(0);
    }
    return new AndFilter(list.toArray(new ItemFilter[list.size()]));
  }

  static class AndFilter extends ItemFilter {
    final ItemFilter[] list;

    AndFilter(ItemFilter[] list) {
      this.list = list;
    }

    @Override
    public boolean matches(ItemStackData entry) {
    	//NAMES before MOD to allow multi name search
      boolean pass = false;
      for(ItemFilter f : list) {
    	  if(f instanceof ModFilter) continue;
    	  if(f.matches(entry)) {
    		  pass = true;
		  }
      }
      for(ItemFilter f : list) {
    	  if(f instanceof ModFilter){
    		  if(!f.matches(entry)) {
    			  return false;
    		  }
    	  }
      }
      return pass;
    }

    @Override
    public String toString() {
      return Arrays.deepToString(list);
    }
  }

  static class ModFilter extends ItemFilter {
    final String text;
    final Locale locale;

    ModFilter(String text, Locale locale) {
      this.text = text.toLowerCase(locale);
      this.locale = locale;
    }

    @Override
    public boolean matches(ItemStackData entry) {
      return entry.getModId().toLowerCase(locale).contains(text);
    }

    @Override
    public String toString() {
      return "@" + text;
    }
  }
  
  static class OreFilter extends ItemFilter {
    final String text;
    final Locale locale;

    OreFilter(String text, Locale locale) {
      this.text = text.toLowerCase(locale);
      this.locale = locale;
    }

    @Override
    public boolean matches(ItemStackData entry) {
      return entry.getOreDic().toLowerCase(locale).contains(text);
    }

    @Override
    public String toString() {
      return "$" + text;
    }
  }

  static class NameFilter extends ItemFilter {
    final String text;
    final Locale locale;

    NameFilter(String text, Locale locale) {
      this.text = text.toLowerCase(locale);
      this.locale = locale;
    }

    @Override
    public boolean matches(ItemStackData entry) {
      return entry.getLowercaseUnlocName(locale).contains(text);
    }

    @Override
    public String toString() {
      return text;
    }
  }
}
