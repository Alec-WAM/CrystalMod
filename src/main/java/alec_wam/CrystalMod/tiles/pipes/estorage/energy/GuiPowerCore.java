package alec_wam.CrystalMod.tiles.pipes.estorage.energy;

import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiPowerCore extends GuiContainer {

	private TileNetworkPowerCore core;
	public GuiPowerCore(EntityPlayer player, TileNetworkPowerCore core) {
		super(new ContainerPowerCore(player, core));
		xSize = this.width = 176;
        ySize = this.height = 230;
		this.core = core;
	}
	ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/eStorage_powercore.png");
	@Override
    protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    	if(core !=null){
    		if(core.info !=null){
    			this.fontRendererObj.drawString("Power: "+core.info.storedEnergy+"/"+core.info.maxEnergy+" "+Lang.localize("power.cu"), 10, 115, 0);
    			this.fontRendererObj.drawString("Usage: "+core.info.energyUsage+" "+Lang.localize("power.cu"), 10, 115+this.fontRendererObj.FONT_HEIGHT+5, 0);
    		}
    	}
    }

}
