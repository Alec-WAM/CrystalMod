package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import alec_wam.CrystalMod.tiles.pipes.estorage.client.CountComp;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.ItemFilter;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.ModComp;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.NameComp;
import alec_wam.CrystalMod.util.ItemUtil;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;

public class EStorageNetworkClient extends EStorageNetwork {

	private Locale LOCALE;
	private Collator collator;
	public Locale getLocale(){
		String languageCode = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		if(LOCALE == null || LOCALE.getISO3Language() != languageCode){
			int idx = languageCode.indexOf('_');
		    if(idx > 0) {
		      String lang = languageCode.substring(0, idx);
		      String country = languageCode.substring(idx+1);
		      LOCALE = new Locale(lang, country);
		      collator = Collator.getInstance(LOCALE);
		    } else {
		      LOCALE = new Locale(languageCode);
		      collator = Collator.getInstance(LOCALE);
		    }
		}
		return LOCALE;
	}
	
	public static enum SortType{
		NAME, NAME_REVERSE, MOD, MOD_REVERSE, COUNT, COUNT_REVERSE;
	}
	
	public String lastFilter = null;
	public SortType lastSortType = SortType.NAME;
	public List<ItemStackData> sortedItems = Lists.newArrayList();
	
	public List<ItemStackData> craftingItems = Lists.newArrayList();
	
	public List<ItemStackData> getItemsSorted(SortType sortType){
		List<ItemStackData> copy = Lists.newArrayList();
		for(ItemStackData data : items){
			if(data.stack !=null)
			copy.add(data);
		}
		
		for(ItemStackData data : craftingItems){
			if(data.stack !=null){
				
				boolean safe = true;
				search : for(ItemStackData item : items){
					if(item.stack !=null && ItemUtil.canCombine(item.stack, data.stack)){
						safe = false;
						break search;
					}
				}
				
				if(safe)copy.add(data);
			}
		}
		if(collator == null){
			getLocale();
			if(collator == null){
				collator = Collator.getInstance();
			}
		}
		Comparator<ItemStackData> cmp = new NameComp(collator);
		if(sortType == SortType.MOD || sortType == SortType.MOD_REVERSE){
			cmp = new ModComp(collator);
		}
		if(sortType == SortType.COUNT || sortType == SortType.COUNT_REVERSE){
			cmp = new CountComp(collator);
		}
		Collections.sort(copy, cmp);
		if(sortType == SortType.NAME_REVERSE || sortType == SortType.MOD_REVERSE || sortType == SortType.COUNT_REVERSE){
			Collections.reverse(copy);
		}
		return copy;
	}
	
	public List<ItemStackData> getDisplayItems(String filterString, SortType type){
		if(filterString != null){
			List<ItemStackData> data = getItemsSorted(type == null ? SortType.NAME : type);
			ItemFilter filter = Strings.isNullOrEmpty(filterString) ? null : ItemFilter.parse(filterString, LOCALE);
			if(filter != null) {
		        Iterator<ItemStackData> iter = data.iterator();
		        while(iter.hasNext()) {
		        	ItemStackData entry = iter.next();
		        	if(!filter.matches(entry)) {
		        		iter.remove();
		        	}
		        }
			}
			return data;
		}
		return getItemsSorted(type);
	}
	
}
