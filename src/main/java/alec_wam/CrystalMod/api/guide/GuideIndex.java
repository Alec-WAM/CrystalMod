package alec_wam.CrystalMod.api.guide;

import java.util.Iterator;
import java.util.List;

import alec_wam.CrystalMod.api.CrystalModAPI;

import com.google.common.collect.Lists;

public class GuideIndex {

	private final String id;
	private List<GuideChapter> chapters = Lists.newArrayList();
	
	public GuideIndex(String id){
		this.id = id;
	}
	
	public String getID(){
		return id;
	}

	public void registerChapter(GuideChapter chapter){
		chapters.add(chapter);
		chapter.setIndex(this);
		CrystalModAPI.GUIDE_CHAPTERS.add(chapter);
	}
	
	public List<GuideChapter> getChapters() {
		return chapters;
	}
	
	public GuideChapter getChapter(String id){
		final Iterator<GuideChapter> i = chapters.iterator();
		while(i.hasNext()){
			GuideChapter index = i.next();
			if(index !=null){
				if(index.getID() !=null && index.getID().equals(id)){
					return index;
				}
			}
		}
		return null;
	}
}
