package alec_wam.CrystalMod.tiles.pipes.estorage;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.api.estorage.security.SecurityData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.CountComp;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.ItemFilter;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.ModComp;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.NameComp;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

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
	
	public static enum ViewType{
		BOTH, ITEMS, PATTERNS;
	}
	
	public String lastFilter = null;
	public SortType lastSortType = SortType.NAME;
	public List<ItemStackData> sortedItems = Lists.newArrayList();
	public ViewType lastViewType = ViewType.BOTH;
	public List<ItemStackData> craftingItems = Lists.newArrayList();
	public SecurityData clientSecurityData = null;
	
	public List<ItemStackData> getItemsSorted(SortType sortType, ViewType viewType){
		List<ItemStackData> copy = Lists.newArrayList();
		
		if(viewType == ViewType.BOTH || viewType == ViewType.ITEMS){
			for(ItemStackData data : getItemStorage().getItemList()){
				if(ItemStackTools.isValid(data.stack)){
					copy.add(data);
				}
			}
		}
		if(viewType == ViewType.BOTH || viewType == ViewType.PATTERNS){
			for(ItemStackData data : craftingItems){
				if(!ItemStackTools.isNullStack(data.stack)){
					
					boolean safe = true;
					search : for(ItemStackData item : getItemStorage().getItemList()){
						if(!ItemStackTools.isNullStack(item.stack)  && ItemUtil.canCombine(item.stack, data.stack)){
							if(viewType == ViewType.PATTERNS){
								copy.add(item);
							}
							safe = false;
							break search;
						}
					}
					
					if(safe)copy.add(data);
				}
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
	
	public boolean needsListUpdate;
	private List<ItemStackData> displayItemsCache = Lists.newArrayList();
	
	private List<ItemStackData> getSortedItems(String filterString, SortType sType, ViewType vType, @Nullable EntityPlayer player){
		List<ItemStackData> data = getItemsSorted(sType == null ? SortType.NAME : sType, vType == null ? ViewType.BOTH : vType);
		if(filterString != null){
			ItemFilter filter = Strings.isNullOrEmpty(filterString) ? null : ItemFilter.parse(filterString, LOCALE, player);
			if(filter != null) {
		        Iterator<ItemStackData> iter = data.iterator();
		        while(iter.hasNext()) {
		        	ItemStackData entry = iter.next();
		        	if(!filter.matches(entry)) {
		        		iter.remove();
		        	}
		        }
			}
		}
		return data;
	}
	
	public List<ItemStackData> getDisplayItems(String filterString, SortType sType, ViewType vType, @Nullable EntityPlayer player){
		if(needsListUpdate){
			displayItemsCache = getSortedItems(filterString, sType, vType, player);
			needsListUpdate = false;
		}
		return displayItemsCache;
	}
	
	@Override
	public boolean hasAbility(UUID uuid, NetworkAbility... abilities){
		if(this.clientSecurityData == null){
			return true;
		}
		if(uuid !=null){
			for(NetworkAbility ability : abilities){
				if(!clientSecurityData.hasAbility(ability)){
					return false;
				}
			}
		}
		return true;
	}
	
}
