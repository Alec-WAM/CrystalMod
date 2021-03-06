package alec_wam.CrystalMod.api.guide;

import alec_wam.CrystalMod.items.guide.GuiGuideChapter;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GuidePage {

	private String id;
	private GuideChapter chapter;
	protected TranslationHandler translator;
	protected ITextEditor textEditor;

	public GuidePage(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public GuidePage setTranslator(TranslationHandler translator) {
		this.translator = translator;
		return this;
	}
	
	public TranslationHandler getTranslator(){
		return translator;
	}
	
	public GuidePage setTextEditor(ITextEditor editor) {
		this.textEditor = editor;
		return this;
	}
	
	public ITextEditor getTextEditor(){
		return textEditor;
	}

	public GuideChapter getChapter() {
		return chapter;
	}

	public void setChapter(GuideChapter chapter) {
		this.chapter = chapter;
	}
	
	@SideOnly(Side.CLIENT)
    public void handleMouseInput(GuiGuideChapter gui){

    }
	
	@SideOnly(Side.CLIENT)
	public void keyTyped(GuiGuideChapter gui, char typedChar, int keyCode){
		
	}
	
	@SideOnly(Side.CLIENT)
    public void mouseClicked(GuiGuideChapter gui, int mouseX, int mouseY, int mouseButton){

    }
	
	@SideOnly(Side.CLIENT)
    public void mouseReleased(GuiGuideChapter gui, int mouseX, int mouseY, int state){

    }
	
	@SideOnly(Side.CLIENT)
    public void mouseClickMove(GuiGuideChapter gui, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick){

    }
	
	@SideOnly(Side.CLIENT)
    public void actionPerformed(GuiGuideChapter gui, GuiButton button){

    }
	
	@SideOnly(Side.CLIENT)
    public void initGui(GuiGuideChapter gui, int startX, int startY){

    }
	
	@SideOnly(Side.CLIENT)
    public void updateScreen(GuiGuideChapter gui, int startX, int startY, int timer){

    }
	
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){

	}
	
	@SideOnly(Side.CLIENT)
    public void drawForeground(GuiGuideChapter gui, int startX, int startY, int mouseX, int mouseY, float partialTicks){

    }
	
	public boolean isOnLeft(){
        return (this.chapter.getIndex(this)+1)%2 != 0;
	}

	public abstract boolean matchesFilter(String filter);
	
}
