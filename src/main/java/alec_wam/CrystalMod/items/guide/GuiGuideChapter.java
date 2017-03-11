package alec_wam.CrystalMod.items.guide;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import alec_wam.CrystalMod.api.guide.GuideChapter;
import alec_wam.CrystalMod.api.guide.GuidePage;
import alec_wam.CrystalMod.client.util.comp.GuiComponentBook;
import alec_wam.CrystalMod.client.util.comp.GuiComponentSprite;

public class GuiGuideChapter extends GuiGuideBase {
	private GuideChapter chapter;
	private GuidePage[] currentPages = new GuidePage[2];
	private int pageTimer;
	
	private GuiButton buttonLeft;
	private GuiButton buttonRight;
	
	public GuiGuideChapter(GuiScreen lastScreen, GuideChapter chapter){
		super(lastScreen);
		this.chapter = chapter;
		this.currentPages = getPages(chapter.getPages()[0]);
	}
	
	public GuiGuideChapter(GuiScreen lastScreen, GuideChapter chapter, GuidePage page){
		this(lastScreen, chapter);
		this.currentPages = getPages(page);
	}
	
	public FontRenderer getFont(){
		return this.fontRendererObj;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		this.buttonList.clear();
		buttonLeft = new GuiButton(0, this.guiLeft+7, this.guiTop+this.ySize-12, 10, 10, "<");
		buttonLeft.visible = currentPages[0] !=null && chapter.getIndex(currentPages[0]) > 0;
    	buttonList.add(buttonLeft);
    	buttonRight = new GuiButton(1, this.guiLeft+(xSize*2)-16, this.guiTop+this.ySize-12, 10, 10, ">");
    	buttonRight.visible = currentPages[1] != null && chapter.getIndex(currentPages[1])+1 < chapter.getPages().length;
		this.buttonList.add(buttonRight);
		for(int i = 0; i < this.currentPages.length; i++){
            GuidePage page = this.currentPages[i];
            if(page != null){
                page.initGui(this, this.guiLeft+7+(xSize*(2*i)), this.guiTop+7);
            }
        }
	}
	
	@Override
    public void updateScreen(){
        super.updateScreen();

        for(int i = 0; i < this.currentPages.length; i++){
        	GuidePage page = this.currentPages[i];
            if(page != null){
                page.updateScreen(this, this.guiLeft+7+(xSize*(2*i)), this.guiTop+7, this.pageTimer);
            }
        }

        this.pageTimer++;
    }
	
	public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
    }
	
	public GuidePage[] getPages(GuidePage page){
		GuidePage[] allPages = chapter.getPages();
        int pageIndex = chapter.getIndex(page);
        GuidePage page1;
        GuidePage page2;

        if(page.isOnLeft()){
            page1 = page;
            page2 = pageIndex >= allPages.length-1 ? null : allPages[pageIndex+1];
        }
        else{
            page1 = pageIndex <= 0 ? null : allPages[pageIndex-1];
            page2 = page;
        }
        return new GuidePage[] {page1, page2};
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for(GuidePage page : currentPages){
			if(page != null){
                page.mouseClicked(this, mouseX, mouseY, mouseButton);
			}
		}
    }
	
	@Override
	protected void mouseReleased(int x, int y, int button) {
		super.mouseReleased(x, y, button);
		for(GuidePage page : currentPages){
			if(page != null){
                page.mouseReleased(this, x, y, button);
			}
		}
	}

	@Override
	protected void mouseClickMove(int x, int y, int button, long time) {
	    super.mouseClickMove(x, y, button, time);
	    for(GuidePage page : currentPages){
			if(page != null){
                page.mouseClickMove(this, x, y, button, time);
			}
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		if(button.id == 0){
			GuidePage page = this.currentPages[0];
	        if(page != null && chapter.getIndex(page) > 0){
	        	GuidePage[] pages = chapter.getPages();

                int pageNumToOpen = chapter.getIndex(page)-1;
                if(pageNumToOpen >= 0 && pageNumToOpen < pages.length){
                	currentPages = getPages(pages[pageNumToOpen]);
                }
	        }
	        buttonLeft.visible = currentPages[0] !=null && chapter.getIndex(currentPages[0]) > 0;
	        buttonRight.visible = currentPages[1] != null && chapter.getIndex(currentPages[1])+1 < chapter.getPages().length;
	        return;
		}
		
		if(button.id == 1){
			GuidePage page = this.currentPages[1];
	        if(page != null && chapter.getIndex(page)+1 < chapter.getPages().length){
	        	GuidePage[] pages = chapter.getPages();

                int pageNumToOpen = chapter.getIndex(page)+1;
                if(pageNumToOpen >= 0 && pageNumToOpen < pages.length){
                	currentPages = getPages(pages[pageNumToOpen]);
                }
	        }
	        buttonLeft.visible = currentPages[0] !=null && chapter.getIndex(currentPages[0]) > 0;
	        buttonRight.visible = currentPages[1] != null && chapter.getIndex(currentPages[1])+1 < chapter.getPages().length;
	        return;
		}
		
		for(GuidePage page : currentPages){
			if(page != null){
                page.actionPerformed(this, button);;
			}
		}
	}
	
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
		//GuiScreen.drawRect(guiLeft+middle-2, guiTop, guiLeft+middle+2, guiTop+ySize, Color.RED.getRGB());
		for(int i = 0; i < this.currentPages.length; i++){
			GuidePage page = this.currentPages[i];
            if(page != null){
                page.drawBackground(this, this.guiLeft+7+i*xSize, this.guiTop+7, mouseX, mouseY, par1);
            }
        }
	}
	
	@Override
	public void drawForegroundLayer(float par1, int par2, int par3) {
		for(int i = 0; i < this.currentPages.length; i++){
			GuidePage page = this.currentPages[i];
            if(page != null){
                page.drawForeground(this, this.guiLeft+7+i*xSize, this.guiTop+7, par2, par3, par1);
            }
        }
		
	}

}
