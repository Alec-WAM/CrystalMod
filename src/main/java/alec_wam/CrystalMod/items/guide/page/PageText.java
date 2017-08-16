package alec_wam.CrystalMod.items.guide.page;

import java.util.List;

import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.items.guide.GuiGuideChapter;
import alec_wam.CrystalMod.items.guide.GuiGuideIndex;
import alec_wam.CrystalMod.items.guide.GuidePages;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageText extends GuidePage {

	public PageText(String id) {
		super(id);
	}
	
	 @Override
	 @SideOnly(Side.CLIENT)
	 public void drawBackground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		 super.drawBackground(gui, startX, startY, mouseX, mouseY, partialTicks);
		 String text = GuidePages.getText(getChapter(), this);
		 String title = GuidePages.getTitle(getChapter(), this);
		 int x = startX+6;
		 int yOffset = 0;
		 if(title != null && !title.isEmpty()){
			 yOffset = 12;
			 title = title.replaceAll("<n>", "\n");
			 GlStateManager.pushMatrix();
			 boolean oldUnicode = gui.getFont().getUnicodeFlag();
			 gui.getFont().setUnicodeFlag(false);

			 gui.getFont().drawString(title, x, startY+10, 0, false);

			 gui.getFont().setUnicodeFlag(oldUnicode);
			 GlStateManager.popMatrix();
		 }
		 
		 if(text != null && !text.isEmpty()){
			 float scale = 0.75f;
			 List<String> lines = gui.getFont().listFormattedStringToWidth(text, (int)(189/scale));
			 for(int i = 0; i < lines.size(); i++){
				 int y = startY+10+yOffset+(i*(int)(gui.getFont().FONT_HEIGHT*scale+3));
				 GlStateManager.pushMatrix();
				 GlStateManager.scale(scale, scale, scale);
				 boolean oldUnicode = gui.getFont().getUnicodeFlag();
				 gui.getFont().setUnicodeFlag(false);
	
				 gui.getFont().drawString(lines.get(i), x/scale, y/scale, 0, false);
	
				 gui.getFont().setUnicodeFlag(oldUnicode);
				 GlStateManager.popMatrix();
			 }
		 }
	 }
	 
	 @Override
	 public boolean matchesFilter(String filter) {
		 if(GuidePages.getTitle(getChapter(), this).toLowerCase(GuiGuideIndex.getLocale()).contains(filter)){
			 return true;
		 }
		 return false;
	 }

}
