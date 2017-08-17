package alec_wam.CrystalMod.tiles.machine.specialengines;

import java.awt.Color;

import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiFiniteEngine extends GuiContainer {

	public TileFiniteEngine engine;
	public GuiFiniteEngine(EntityPlayer player, TileFiniteEngine engine) {
		super(new ContainerFiniteEngine(player.inventory, engine));
		this.engine = engine;
		this.ySize = 176;
	}

	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/specialengine.png");
	private final Color BAR_COLOR = new Color(0x9700B5);
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		int rf = engine.energyStorage.getEnergyStored();
		int maxRF = engine.energyStorage.getMaxEnergyStored();
		RenderUtil.renderPowerBar(82, 6, (int)zLevel, 12, 78, rf, maxRF, BAR_COLOR.getRGB(), BAR_COLOR.darker().getRGB());
		
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
		if(xAxis > 79 && xAxis < (82)+12 && yAxis >= 6 && yAxis <= 86)
		{
			drawCreativeTabHoveringText(rf > 0 ? "Power: "+ rf +" / "+ maxRF : Lang.localize("gui.empty"), xAxis, yAxis);
			RenderHelper.enableGUIStandardItemLighting();
		}
		
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

}
