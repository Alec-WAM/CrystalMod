package alec_wam.CrystalMod.client.util.comp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class GuiComponentItemStackBasic extends BaseComponent {

	public ItemStack stack;
	private static ItemRenderer itemRenderer;

	public GuiComponentItemStackBasic(int x, int y, ItemStack stack) {
		super(x, y);
		if (itemRenderer == null) {
			itemRenderer = new ItemRenderer(Minecraft.getMinecraft());
		}
		this.stack = stack;
	}

	@Override
	public int getWidth() {
		return 64;
	}

	@Override
	public int getHeight() {
		return 64;
	}

	@Override
	public void renderOverlay(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderOverlay(minecraft, offsetX, offsetY, mouseX, mouseY);
		GlStateManager.pushMatrix();
		float scale = 3.0f;
		GlStateManager.translate(offsetX + x - (scale), offsetY + y - (scale), 0);
		GlStateManager.scale(scale, scale, 1);
		renderItem(stack);
		GlStateManager.popMatrix();
	}

	public static void renderItem(ItemStack itemStack) {
		if(itemStack == null)return;
		GlStateManager.pushMatrix();
		Minecraft mc = Minecraft.getMinecraft();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().zLevel = 100.0F;
		mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, 0, 0);
		mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.disableDepth();
		GlStateManager.popMatrix();
	}
}