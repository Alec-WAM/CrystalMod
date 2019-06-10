package alec_wam.CrystalMod.client.gui;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiContainerBase<C extends Container> extends ContainerScreen<C> {
	private final ResourceLocation texture;
	public GuiContainerBase(C inventorySlotsIn, PlayerInventory inv, ITextComponent title, ResourceLocation texture) {
		super(inventorySlotsIn, inv, title);
		this.texture = texture;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(texture !=null){
			Minecraft.getInstance().getTextureManager().bindTexture(texture);
			blit(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
		for(Widget button : this.buttons){
			if(button instanceof ITooltipProvider){
				if(isPointInRegion(button.x - guiLeft, button.y - guiTop, button.getWidth(), button.getHeight(), mouseX, mouseY)){
					List<String> info = ((ITooltipProvider)button).getInfo();
					if(!info.isEmpty()){
						renderTooltip(info, mouseX, mouseY);
					}
				}
			}
		}
	}
	
	public static interface ITooltipProvider {
		public List<String> getInfo();
	}

}
