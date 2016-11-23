package alec_wam.CrystalMod.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import alec_wam.CrystalMod.api.guide.GuideChapter;
import alec_wam.CrystalMod.api.guide.GuideIndex;
import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.api.guide.IGuideChapter;
import alec_wam.CrystalMod.api.guide.IGuideEntry;
import alec_wam.CrystalMod.api.guide.IGuidePage;

public class CrystalModAPI {

	public static final List<GuideIndex> GUIDE_INDEXES = new ArrayList<GuideIndex>();
	public static final List<GuideChapter> GUIDE_CHAPTERS = new ArrayList<GuideChapter>();
	public static final List<GuidePage> GUIDE_PAGES = new ArrayList<GuidePage>();

	public static GuideIndex BLOCKS = null;
	public static GuideIndex ITEMS = null;
	public static GuideIndex ENTITES = null;
	public static GuideIndex WORKBENCH = null;
	public static GuideIndex MISC = null;
	
	public static GuideIndex regiterGuideIndex(GuideIndex index){
		GUIDE_INDEXES.add(index);
		return index;
	}
	
	public static GuideIndex getIndex(String id){
		final Iterator<GuideIndex> i = GUIDE_INDEXES.iterator();
		while(i.hasNext()){
			GuideIndex index = i.next();
			if(index !=null){
				if(index.getID() !=null && index.getID().equals(id)){
					return index;
				}
			}
		}
		return null;
	}
}
