package alec_wam.CrystalMod.items.guide;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.guide.GuideChapter;
import alec_wam.CrystalMod.api.guide.GuideIndex;
import alec_wam.CrystalMod.client.util.comp.GuiComponentBook;
import alec_wam.CrystalMod.client.util.comp.GuiComponentSprite;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.VScrollbar;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;

public class GuiGuideIndex extends GuiGuideBase implements IGuiScreen {

	private GuideIndex[] indexes = new GuideIndex[2];
	
	private VScrollbar scrollbarLeft;
	private VScrollbar scrollbarRight;
	protected VScrollbar draggingScrollbar;
	
	public GuiGuideIndex(GuiScreen lastScreen, GuideIndex index){
		super(lastScreen);
		this.indexes = getIndexes(index);
	}
	
	@Override
	public void initGui(){
		super.initGui();
		this.buttonList.clear();
		GuiButton buttonLeft = new GuiButton(0, this.guiLeft+7, this.guiTop+this.ySize-12, 10, 10, "<");
		buttonLeft.visible = indexes[0] !=null && CrystalModAPI.GUIDE_INDEXES.indexOf(indexes[0]) > 0;
    	buttonList.add(buttonLeft);
    	GuiButton buttonRight = new GuiButton(1, this.guiLeft+(xSize*2)-16, this.guiTop+this.ySize-12, 10, 10, ">");
    	buttonRight.visible = indexes[1] != null && CrystalModAPI.GUIDE_INDEXES.indexOf(indexes[1])+1 < CrystalModAPI.GUIDE_INDEXES.size();
		this.buttonList.add(buttonRight);
		
		scrollbarLeft = new VScrollbar(this, 8, 41, 123);
		scrollbarLeft.adjustPosition();
		scrollbarRight = new VScrollbar(this, (xSize*2)-19, 41, 123);
		scrollbarRight.adjustPosition();
	}
	
	public GuideIndex[] getIndexes(GuideIndex index){
		List<GuideIndex> allPages = CrystalModAPI.GUIDE_INDEXES;
        int pageIndex = allPages.indexOf(index);
        GuideIndex index1;
        GuideIndex index2;

        if((pageIndex+1)%2 != 0){
        	index1 = index;
        	index2 = pageIndex >= allPages.size()-1 ? null : allPages.get(pageIndex+1);
        }
        else{
        	index1 = pageIndex <= 0 ? null : allPages.get(pageIndex-1);
        	index2 = index;
        }
        return new GuideIndex[] {index1, index2};
	}
	
	public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        if (i != 0)
        {
        	if(x > width/2)this.scrollbarRight.mouseWheel(x, y, i);
        	if(x < width/2)this.scrollbarLeft.mouseWheel(x, y, i);
        }
    }
	
	public void updateScreen(){
		super.updateScreen();
		pageTimer++;
	}
	
	public void actionPerformed(GuiButton button){
		if(button.id == 0){
			GuideIndex index = this.indexes[0];
	        if(index != null && CrystalModAPI.GUIDE_INDEXES.indexOf(index) > 0){
	        	int pageNumToOpen = CrystalModAPI.GUIDE_INDEXES.indexOf(index)-1;
                if(pageNumToOpen >= 0 && pageNumToOpen < CrystalModAPI.GUIDE_INDEXES.size()){
                	indexes = getIndexes(CrystalModAPI.GUIDE_INDEXES.get(pageNumToOpen));
                }
	        }
	        this.buttonList.get(0).visible = indexes[0] !=null && CrystalModAPI.GUIDE_INDEXES.indexOf(indexes[0]) > 0;
	        this.buttonList.get(1).visible = indexes[1] != null && CrystalModAPI.GUIDE_INDEXES.indexOf(indexes[1])+1 < CrystalModAPI.GUIDE_INDEXES.size();
	        return;
		}
		
		if(button.id == 1){
			GuideIndex index = this.indexes[1];
	        if(index != null && CrystalModAPI.GUIDE_INDEXES.indexOf(index)+1 < CrystalModAPI.GUIDE_INDEXES.size()){
	        	int pageNumToOpen = CrystalModAPI.GUIDE_INDEXES.indexOf(index)+1;
                if(pageNumToOpen >= 0 && pageNumToOpen < CrystalModAPI.GUIDE_INDEXES.size()){
                	indexes = getIndexes(CrystalModAPI.GUIDE_INDEXES.get(pageNumToOpen));
                }
	        }
	        this.buttonList.get(0).visible = indexes[0] !=null && CrystalModAPI.GUIDE_INDEXES.indexOf(indexes[0]) > 0;
	        this.buttonList.get(1).visible = indexes[1] != null && CrystalModAPI.GUIDE_INDEXES.indexOf(indexes[1])+1 < CrystalModAPI.GUIDE_INDEXES.size();
	        return;
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		
		if (draggingScrollbar != null) {
			draggingScrollbar.mouseClicked(mouseX, mouseY, mouseButton);
        	return;
		}
		
		if(this.scrollbarLeft !=null && indexes[0] !=null){
			if (scrollbarLeft.mouseClicked(mouseX, mouseY, mouseButton)) {
				draggingScrollbar = scrollbarLeft;
				return;
			}
		}
		
		if(this.scrollbarRight !=null && indexes[1] !=null){
			if (scrollbarRight.mouseClicked(mouseX, mouseY, mouseButton)) {
				draggingScrollbar = scrollbarRight;
				return;
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(indexes[0] !=null){
			final int xStart = guiLeft+10+12;
			int x = xStart;
			int y = guiTop+40;
			int row = scrollbarLeft.getScrollPos();
			List<GuideChapter> chapters = indexes[0].getChapters();
			for(int s = row; (s < row+6); s++){
				
				if(s < chapters.size()){
					if(mouseX >=x && mouseX <x+182 && mouseY >=y && mouseY <y+20){
						GuideChapter chapter = chapters.get(s);
						mc.displayGuiScreen(new GuiGuideChapter(this, chapter));
						return;
					}
				}
				y+=20;
			}
		}
		if(indexes[1] !=null){
			int middle = width/2;
			final int xStart = middle+10;
			int x = xStart;
			int y = guiTop+40;
			int row = scrollbarRight.getScrollPos();
			List<GuideChapter> chapters = indexes[1].getChapters();
			for(int s = row; (s < row+6); s++){
				
				if(s < chapters.size()){
					if(mouseX >=x && mouseX <x+182 && mouseY >=y && mouseY <y+20){
						GuideChapter chapter = chapters.get(s);
						mc.displayGuiScreen(new GuiGuideChapter(this, chapter));
						return;
					}
				}
				y+=20;
			}
		}
		
		
    }
	
	@Override
	protected void mouseReleased(int x, int y, int button) {
		if (draggingScrollbar != null) {
			draggingScrollbar.mouseMovedOrUp(x, y, button);
			draggingScrollbar = null;
		}
		super.mouseReleased(x, y, button);
	}

	@Override
	protected void mouseClickMove(int x, int y, int button, long time) {
	    if (draggingScrollbar != null) {
	      draggingScrollbar.mouseClickMove(x, y, button, time);
	      return;
	    }
	    super.mouseClickMove(x, y, button, time);
	}
	
    private int pageTimer;
    @Override
	public void drawBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    
		int offsetX = guiLeft;
		int offsetY = guiTop;
		int mouseX = par2 - this.guiLeft;
		int mouseY = par3 - this.guiTop;
		GuiComponentSprite.renderSprite(mc, 0, 0, offsetX, offsetY, mouseX, mouseY, GuiComponentBook.iconPageLeft, GuiComponentBook.texture, 1f, 1f, 1f);
		GuiComponentSprite.renderSprite(mc, (int)GuiComponentBook.iconPageRight.getWidth(), 0, offsetX, offsetY, mouseX, mouseY, GuiComponentBook.iconPageRight, GuiComponentBook.texture, 1f, 1f, 1f);
		
		int middle = xSize;
		GuiScreen.drawRect(guiLeft+middle-2, guiTop, guiLeft+middle+2, guiTop+ySize, Color.RED.getRGB());
		
		if(indexes[0] !=null){
			List<GuideChapter> chapters = indexes[0].getChapters();
		    
		    int max = chapters.size();
			
		    scrollbarLeft.setScrollMax(Math.max(0, max-1));
		    scrollbarLeft.drawScrollbar(par2, par3);
		}
		if(indexes[1] !=null){
			List<GuideChapter> chapters = indexes[1].getChapters();
		    
		    int max = chapters.size();
			
		    scrollbarRight.setScrollMax(Math.max(0, max-1));
		    scrollbarRight.drawScrollbar(par2, par3);
		}
	}
    
    @Override
	public void drawForegroundLayer(float partialTicks, int par2, int par3) {
		
		int middle = width/2;
		
		if(indexes[0] !=null){
			final int xStart = guiLeft+10+12;
			int x = xStart;
			int y = guiTop+40;
			int row = scrollbarLeft.getScrollPos();
			String title = "-"+Lang.localize("guide.index."+indexes[0].getID())+"-";
			this.fontRendererObj.drawString(title, guiLeft+((xSize-8)/2)-this.fontRendererObj.getStringWidth(title)/2, guiTop+20, 0);
			List<GuideChapter> chapters = indexes[0].getChapters();
			for(int s = row; (s < row+6); s++){
				
				if(s < chapters.size()){
					if(par2 >=x && par2 <x+182 && par3 >=y && par3 <y+20){
						GlStateManager.disableLighting();
		                GlStateManager.disableDepth();
		                GlStateManager.colorMask(true, true, true, false);
		                int color = Color.GRAY.getRGB();
		                this.drawGradientRect(x, y, x+182, y+20, color, color);
		                GlStateManager.colorMask(true, true, true, true);
		                GlStateManager.enableLighting();
		                GlStateManager.enableDepth();
					}
					GuideChapter chapter = chapters.get(s);
					chapter.update(pageTimer);
					ItemStack dis = chapter.getDisplayStack();
					if(!ItemStackTools.isNullStack(dis)){
						GlStateManager.pushMatrix();
			            RenderHelper.enableGUIStandardItemLighting();
			            GlStateManager.disableLighting();
			            GlStateManager.enableRescaleNormal();
			            GlStateManager.enableColorMaterial();
			            GlStateManager.enableLighting();
			    		GlStateManager.pushMatrix();
			    		this.itemRender.zLevel = 100.0F;
			            this.itemRender.renderItemAndEffectIntoGUI(dis, x, y+2);
			            GlStateManager.popMatrix();
			            this.itemRender.zLevel = 0.0F;
			            GlStateManager.disableLighting();
		
			            GlStateManager.popMatrix();
			            GlStateManager.enableLighting();
			            GlStateManager.enableDepth();
					}
					
					this.fontRendererObj.drawString(chapter.getLocalizedTitle(), x+20, y+6, 0);
				}
				y+=20;
			}
		}
		
		if(indexes[1] !=null){
			final int xStart = middle+7;
			int x = xStart;
			int y = guiTop+40;
			int row = scrollbarRight.getScrollPos();
			String title = "-"+Lang.localize("guide.index."+indexes[1].getID())+"-";
			this.fontRendererObj.drawString(title, middle+((xSize-8)/2)-this.fontRendererObj.getStringWidth(title)/2, guiTop+20, 0);
			List<GuideChapter> chapters = indexes[1].getChapters();
			for(int s = row; (s < row+6); s++){
				
				if(s < chapters.size()){
					if(par2 >=x && par2 <x+182 && par3 >=y && par3 <y+20){
						GlStateManager.disableLighting();
		                GlStateManager.disableDepth();
		                GlStateManager.colorMask(true, true, true, false);
		                int color = Color.GRAY.getRGB();
		                this.drawGradientRect(x, y, x+182, y+20, color, color);
		                GlStateManager.colorMask(true, true, true, true);
		                GlStateManager.enableLighting();
		                GlStateManager.enableDepth();
					}
					GuideChapter chapter = chapters.get(s);
					chapter.update(pageTimer);
					ItemStack dis = chapter.getDisplayStack();
					if(dis !=null){
						GlStateManager.pushMatrix();
			            RenderHelper.enableGUIStandardItemLighting();
			            GlStateManager.disableLighting();
			            GlStateManager.enableRescaleNormal();
			            GlStateManager.enableColorMaterial();
			            GlStateManager.enableLighting();
			    		GlStateManager.pushMatrix();
			    		this.itemRender.zLevel = 100.0F;
			            this.itemRender.renderItemAndEffectIntoGUI(dis, x, y+2);
			            GlStateManager.popMatrix();
			            this.itemRender.zLevel = 0.0F;
			            GlStateManager.disableLighting();
		
			            GlStateManager.popMatrix();
			            GlStateManager.enableLighting();
			            GlStateManager.enableDepth();
					}
					
					this.fontRendererObj.drawString(chapter.getLocalizedTitle(), x+20, y+6, 0);
				}
				y+=20;
			}
		}
	}
	
	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}

}
