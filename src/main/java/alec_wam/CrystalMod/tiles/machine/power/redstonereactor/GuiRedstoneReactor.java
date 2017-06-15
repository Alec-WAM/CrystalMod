package alec_wam.CrystalMod.tiles.machine.power.redstonereactor;

import java.awt.Color;

import alec_wam.CrystalMod.handler.ClientEventHandler;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiRedstoneReactor extends GuiContainer {

	public TileRedstoneReactor reactor;
	public int animationIndex;
	public GuiRedstoneReactor(EntityPlayer player, TileRedstoneReactor reactor) {
		super(new ContainerRedstoneReactor(player.inventory, reactor));
		this.reactor = reactor;
		this.ySize = 190;
	}

	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/reactor/reactor_gui.png");
	public static final ResourceLocation ANIMATION_TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/reactor/gui_animation.png");
	public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/reactor/slot_animation.png");

	@Override
	public void updateScreen(){
		super.updateScreen();
		if(reactor.remainingFuel.getValue() > 0){
			if(ClientEventHandler.elapsedTicks % 5 == 0){
				animationIndex++;
				if(animationIndex == 12){
					animationIndex = 0;
				}
			}
		} else {
			animationIndex = 0;
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ANIMATION_TEXTURE);
		drawModalRectWithCustomSizedTexture(48, 10, 0, animationIndex * 80, 80, 80, 80, 960);
		
		int rf = reactor.energyStorage.getEnergyStored();
		int maxRF = reactor.energyStorage.getMaxEnergyStored();
		RenderUtil.renderPowerBar(145, 10, (int)zLevel, 12, 80, rf, maxRF, Color.RED.getRGB(), Color.RED.darker().getRGB());
		
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
		if(xAxis > 142 && xAxis < (145)+12 && yAxis >= 10 && yAxis <= 90)
		{
			drawCreativeTabHoveringText(rf > 0 ? "RF: "+ rf +" / "+ maxRF +"RF": Lang.localize("gui.empty"), xAxis, yAxis);
			RenderHelper.enableGUIStandardItemLighting();
		}
		
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(reactor.remainingFuel.getValue() > 0){
			Minecraft.getMinecraft().getTextureManager().bindTexture(SLOT_TEXTURE);
			int y = 0;
			if(reactor.remainingFuel.getValue() < 31){
				y = 47;
			}
			if(reactor.remainingFuel.getValue() < 21){
				y = 94;
			}
			if(reactor.remainingFuel.getValue() < 11){
				y = 141;
			}
			drawModalRectWithCustomSizedTexture(guiLeft + 2, guiTop + 12, 0, y, 44, 44, 44, 188);
		}
	}

}
