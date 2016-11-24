package alec_wam.CrystalMod.api.guide;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.Util;

public class GuideChapter {

	private final String unlocalizedName;
	private Object displayObject;
	private final GuidePage[] pages;
	private Map<String, List<?>> lookUpData = Maps.newHashMap();
	private GuideIndex index;
	
	public GuideChapter(String unlocalizedName, GuidePage... pages){
		this.unlocalizedName = unlocalizedName;
		this.pages = pages;
		for(GuidePage page : pages){
			page.setChapter(this);
			CrystalModAPI.GUIDE_PAGES.add(page);
		}
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
		return Lang.localize("guide.chapter."+unlocalizedName);
	}
	
	public String getID(){
		return unlocalizedName;
	}

	public GuideChapter setDisplayObject(Object displayObject) {
		this.displayObject = displayObject;
		
		if(displayObject instanceof ItemStack){
			stacks = new ItemStack[1];
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
				stacks = list.toArray(new ItemStack[list.size()]);
			}
		}
		
		if(stacks == null){
			stacks = new ItemStack[1];
		}
		stackIndex = 0;
		currentDisplay = stacks[0];
		
		return this;
	}
	
	private ItemStack currentDisplay;
	private ItemStack[] stacks;
	private int stackIndex = 0;

	public void update(int pageTimer) {
		if(stacks !=null && Util.isMultipleOf(pageTimer, 20)){
			if(stackIndex+1 >=stacks.length){
				stackIndex = 0;
			} else {
				stackIndex++;
			}
			currentDisplay = stacks[stackIndex];
		}
	}
	
	public ItemStack getDisplayStack() {
		return currentDisplay;
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