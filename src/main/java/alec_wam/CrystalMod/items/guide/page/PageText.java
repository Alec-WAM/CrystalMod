package alec_wam.CrystalMod.items.guide.page;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.items.guide.GuiGuideChapter;
import alec_wam.CrystalMod.items.guide.GuidePages;
import alec_wam.CrystalMod.items.guide.GuidePages.ManualChapter;
import alec_wam.CrystalMod.items.guide.GuidePages.PageData;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

public class PageText extends GuidePage {

	public PageText(String id) {
		super(id);
	}
	
	 @Override
	 @SideOnly(Side.CLIENT)
	 public void drawBackground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){
		 super.drawBackground(gui, startX, startY, mouseX, mouseY, partialTicks);
		 String lang = Lang.prefix+"guide.chapter."+getChapter().getID()+".text."+getId();
		 String text = "";
		 String title = "";
		 if(I18n.canTranslate(lang))text = Lang.translateToLocal(lang);
		 else {
			 ManualChapter chapter = GuidePages.CHAPTERTEXT.get(getChapter().getID());
			 if(chapter !=null){
				 PageData data = chapter.pages.get(getId());
				 if(data !=null){
					 text = data.text;
					 title = data.title;
				 }
			 }
		 }
		 text = text.replaceAll("<n>", "\n");
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

}
