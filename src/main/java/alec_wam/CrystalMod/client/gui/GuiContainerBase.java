package alec_wam.CrystalMod.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiContainerBase extends GuiContainer {
	private final ResourceLocation texture;
	public GuiContainerBase(Container inventorySlotsIn, ResourceLocation texture) {
		super(inventorySlotsIn);
		this.texture = texture;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(texture !=null){
			Minecraft.getInstance().getTextureManager().bindTexture(texture);
			drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
		for(GuiButton button : this.buttons){
			if(button instanceof ITooltipProvider){
				if(isPointInRegion(button.x - guiLeft, button.y - guiTop, button.width, button.height, mouseX, mouseY)){
					List<String> info = ((ITooltipProvider)button).getInfo();
					if(!info.isEmpty()){
						drawHoveringText(info, mouseX, mouseY, fontRenderer);
					}
				}
			}
		}
	}
	
	public static interface ITooltipProvider {
		public List<String> getInfo();
	}

}
