package alec_wam.CrystalMod.items.guide;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;

public class GuiGuideBase extends GuiScreen {

	private GuiScreen lastScreen;
	
	public GuiGuideBase(GuiScreen lastScreen){
		this.lastScreen = lastScreen;
	}
	
	@Override
    public void onGuiClosed(){
        super.onGuiClosed();
        
        //this.lastScreen = null;

        ExtendedPlayer data = ExtendedPlayerProvider.getExtendedPlayer(CrystalMod.proxy.getClientPlayer());
        data.lastOpenBook = this;
    }
	
	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton == 1){
            this.onBackButtonPressed();
        }
	}
	
	public boolean hasBackButton(){
        return false;
    }
	
	public void onBackButtonPressed(){
		mc.displayGuiScreen(lastScreen);
	}
	
	@Override
	public void initGui(){
		super.initGui();
		guiLeft = (width / 2) - xSize;
		guiTop = (height - ySize) / 2;
	}
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	public int xSize = 211;
    public int ySize = 180;
    public int guiLeft;
    public int guiTop;
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		guiLeft = (width / 2) - xSize;
		guiTop = (height - ySize) / 2;
        drawBackgroundLayer(partialTicks, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawForegroundLayer(partialTicks, mouseX, mouseY);
    }

	public void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
	}

	public void drawForegroundLayer(float partialTicks, int mouseX, int mouseY) {
		
	}
	
}
