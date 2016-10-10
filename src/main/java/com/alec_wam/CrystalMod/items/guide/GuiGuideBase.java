package com.alec_wam.CrystalMod.items.guide;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.client.util.comp.BaseComponent;
import com.alec_wam.CrystalMod.client.util.comp.GuiComponentBook;
import com.alec_wam.CrystalMod.client.util.comp.IComponentListener;
import com.alec_wam.CrystalMod.client.util.comp.book.BlankPage;
import com.alec_wam.CrystalMod.client.util.comp.book.TitledPage;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;


public class GuiGuideBase extends GuiScreen implements IComponentListener {
	
	private int centerX;
	private int guiLeft;
	private int guiTop;

	public GuiComponentBook book;
	
	public void updateScreen(){
		if(this.book !=null){
			
			if(CrystalMod.proxy.isShiftKeyDown() && Keyboard.isKeyDown(Keyboard.KEY_I)){
				book.gotoIndex(0);
			}
			
			for(BaseComponent comp : book.components){
				comp.updateComp();
			}
		}
	}
	
	public ItemStack stack;
	
	public GuiGuideBase(){
	}
	public GuiGuideBase(ItemStack hand) {
		this.stack = hand;
	}
	
	public void initBook(){
		book = new GuiComponentBook();
		BlankPage contentsPage = new TitledPage("", "");
		
		addCompsToFront(contentsPage);
		
		book.addPage(new BlankPage());
		book.addPage(contentsPage);
		
		addBookPages();
		
		book.enablePages();
	}
	
	public void addCompsToFront(BlankPage contentsPage){
		
	}
	
	public void addBookPages(){
		
	}
	
	public void initGui(){
		super.initGui();
		initBook();
		if(stack !=null){
			if(ItemNBTHelper.verifyExistance(stack, "LastPage")){
				book.gotoIndex(ItemNBTHelper.getInteger(stack, "LastPage", 0));
			}
		}
	}
	
	public void onGuiClosed(){
		super.onGuiClosed();
		if(stack !=null){
			ItemNBTHelper.setInteger(stack, "LastPage", book.getCurrentIndex());
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		try {
			super.mouseClicked(x, y, button);
		} catch (IOException e) {
			e.printStackTrace();
		}
		book.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
	}

	@Override
	protected void mouseReleased(int x, int y, int button) {
		super.mouseReleased(x, y, button);
		book.mouseMovedOrUp(x - this.guiLeft, y - this.guiTop, button);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		super.mouseClickMove(mouseX, mouseY, button, time);
		book.mouseClickMove(mouseX - this.guiLeft, mouseY - this.guiTop, button, time);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void confirmClicked(boolean result, int action) {
		this.mc.displayGuiScreen(this);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		super.drawScreen(mouseX, mouseY, par3);
		centerX = this.width / 2;
		guiLeft = centerX - 211;
		guiTop = (height - 200) / 2;

		GlStateManager.pushMatrix();
		book.render(this.mc, guiLeft, guiTop, mouseX - this.guiLeft, mouseY - this.guiTop);
		GlStateManager.popMatrix();

		// second pass
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.pushMatrix();
		book.renderOverlay(this.mc, guiLeft, guiTop, mouseX - this.guiLeft, mouseY - this.guiTop);
		GlStateManager.popMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();
	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {

	}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {

	}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {

	}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {

	}

}