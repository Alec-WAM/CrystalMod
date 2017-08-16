package alec_wam.CrystalMod.api.guide;

import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.items.guide.GuidePages;
import alec_wam.CrystalMod.items.guide.GuidePages.ManualChapter;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class GuideChapter {

	private final String unlocalizedName;
	private final GuidePage[] pages;
	private Map<String, List<?>> lookUpData = Maps.newHashMap();
	private GuideIndex index;
	private TranslationHandler translator;
	
	public GuideChapter(String unlocalizedName, GuidePage... pages){
		this.unlocalizedName = unlocalizedName;
		this.pages = pages;
		for(GuidePage page : pages){
			page.setChapter(this);
			CrystalModAPI.GUIDE_PAGES.add(page);
		}
	}
	
	public GuideChapter setTranslator(TranslationHandler handler){
		this.translator = handler;
		return this;
	}
	
	public void setIndex(GuideIndex index){
		this.index = index;
	}
	
	public GuideIndex getIndex(){
		return index;
	}
	
	public Map<String, List<?>> getLookUpData(){
		return lookUpData;
	}
	
	public GuideChapter setLookUpData(Map<String, List<?>> data){
		lookUpData = data;
		return this;
	}
	
	public String getLocalizedTitle() {
		if(translator == null){
			ManualChapter chapter = GuidePages.CHAPTERTEXT.get(getID());
			if(chapter !=null && !Strings.isNullOrEmpty(chapter.title))return chapter.title;
			return Lang.localize("guide.chapter."+unlocalizedName);
		}
		return translator.getTranslatedText();
	}
	
	public String getID(){
		return unlocalizedName;
	}

	public GuideChapter setDisplayObject(Object displayObject) {
		if(displayObject instanceof ItemStack){
			stacks = new ItemStack[]{ItemStackTools.getEmptyStack()};
			stacks[0] = ItemStackTools.safeCopy(((ItemStack)displayObject));
			if(stacks[0].getItemDamage() == OreDictionary.WILDCARD_VALUE)stacks[0].setItemDamage(0);
		}
		else if(displayObject instanceof String){
			String id = (String)displayObject;
			List<ItemStack> oreList = OreDictionary.getOres(id);
			if(!oreList.isEmpty()){
				stacks = oreList.toArray(new ItemStack[oreList.size()]);
			}
		}
		else if(displayObject instanceof List<?>){
			@SuppressWarnings("unchecked")
			List<ItemStack> list = (List<ItemStack>)displayObject;
			if(!list.isEmpty()){
				stacks = new ItemStack[list.size()];
				for(int i = 0; i < list.size(); i++){
					stacks[i] = list.get(i);
				}
			}
		}
		
		if(stacks == null){
			stacks = new ItemStack[]{ItemStackTools.getEmptyStack()};
		}
		stackIndex = 0;
		currentDisplay = stacks[0];
		
		return this;
	}
	
	private ItemStack currentDisplay;
	private ItemStack[] stacks;
	private int stackIndex = 0;

	public void update(int pageTimer) {
		if(stacks !=null && Util.isMultipleOf(pageTimer, 40)){
			stackIndex++;
			if(stackIndex >= stacks.length){
				stackIndex = 0;
			}
			currentDisplay = stacks[stackIndex];
		}
	}
	
	public ItemStack getDisplayStack() {
		return currentDisplay;
	}

	public boolean doesFilterMatch(String filter) {
		for(GuidePage page : getPages()){
			if(page.matchesFilter(filter)){
				return true;
			}
		}
		return false;
	}
	
	public GuidePage[] getPages() {
		return pages;
	}

	public int getIndex(GuidePage guidePage) {
		if(guidePage == null)return -1;
		for(int i = 0; i < this.pages.length; i++){
            if(this.pages[i] == guidePage){
                return i;
            }
        }
        return -1;
	}
	
	public GuidePage getPage(String id){
		for(int i = 0; i < this.pages.length; i++){
			GuidePage page = pages[i];
            if(page.getId() !=null && page.getId().equals(id)){
                return page;
            }
        }
		return null;
	}

}
