package alec_wam.CrystalMod.tiles.pipes.estorage.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ItemFilter {

  public abstract boolean matches(ItemStackData entry);

  public static ItemFilter parse(String filter, Locale locale, @Nullable EntityPlayer player) {
    ArrayList<ItemFilter> list = new ArrayList<ItemFilter>();

    String[] parts = filter.split("\\|");
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
      else if(part.startsWith("*")) {
    	  part = part.substring(1);
    	  if(!part.isEmpty()) {
    		  list.add(new DeepNameFilter(part, locale, player));
    	  }
      }
      else if(!part.isEmpty()) {
    	list.add(new NameFilter(part, locale));
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
  
  static class DeepNameFilter extends ItemFilter {
	  final String text;
	  final Locale locale;
	  final EntityPlayer player;

	  DeepNameFilter(String text, Locale locale, @Nullable EntityPlayer player) {
		  this.text = text.toLowerCase(locale);
		  this.locale = locale;
		  this.player = player;
	  }

	  @Override
	  public boolean matches(ItemStackData entry) {
		  List<String> infoList = Lists.newArrayList();
		  if(player !=null){
			  entry.stack.getItem().addInformation(entry.stack, player, infoList, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
		  }
		  if(entry.getLowercaseUnlocName(locale).contains(text))return true;
		  for(String line : infoList){
			  if(line.toLowerCase(locale).contains(text)){
				  return true;
			  }
		  }
		  return false;
	  }

	  @Override
	  public String toString() {
		  return text;
	  }
  }
}
