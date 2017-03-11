package alec_wam.CrystalMod.items.guide;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.guide.GuideIndex;
import alec_wam.CrystalMod.client.util.comp.GuiComponentBook;
import alec_wam.CrystalMod.client.util.comp.GuiComponentSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiGuideMainPage extends GuiGuideBase {

	public GuiGuideMainPage(){
		super(null);
	}
	
	private GuiButton buttonIndex;
	
	@Override
	public void initGui(){
		super.initGui();
		buttonList.add(buttonIndex = new GuiButton(0, guiLeft + xSize + ((xSize/2)-25), guiTop+60, 50, 20, "Chapters"));
	}
	
	public static final ResourceLocation LOGO = CrystalMod.resourceL("logo.png");
	
	@Override
	public void drawBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    
		int offsetX = guiLeft;
		int offsetY = guiTop;
		int mouseX = par2 - this.guiLeft;
		int mouseY = par3 - this.guiTop;
		GuiComponentSprite.renderSprite(mc, 0, 0, offsetX, offsetY, mouseX, mouseY, GuiComponentBook.iconPageLeft, GuiComponentBook.texture, 1f, 1f, 1f);
		GuiComponentSprite.renderSprite(mc, (int)GuiComponentBook.iconPageRight.getWidth(), 0, offsetX, offsetY, mouseX, mouseY, GuiComponentBook.iconPageRight, GuiComponentBook.texture, 1f, 1f, 1f);
	
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft + (xSize/2) + 107, guiTop + 10, 0);
		GlStateManager.scale(0.79, 0.79, 0);		
		Minecraft.getMinecraft().renderEngine.bindTexture(LOGO);
		drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 256, 43, 256, 43);
		GlStateManager.popMatrix();
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		if(button == buttonIndex){
			if(CrystalModAPI.GUIDE_INDEXES.isEmpty()){
				return;
			}
			openIndex(CrystalModAPI.GUIDE_INDEXES.get(0));
		} 
	}
	
	public void openIndex(GuideIndex index){
		mc.displayGuiScreen(new GuiGuideIndex(this, index));
	}
}
