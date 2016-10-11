package alec_wam.CrystalMod.client.util.comp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

public class GuiComponentItemStackSpinner extends BaseComponent {

	public ItemStack stack;
	private float rotationY = 0f;
	private static ItemRenderer itemRenderer;

	public GuiComponentItemStackSpinner(int x, int y, ItemStack stack) {
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
		GlStateManager.enableDepth();
		float scale = 90.0f;
		GlStateManager.translate(offsetX + x + (scale / 2), offsetY + y + (scale / 2), scale);
		GlStateManager.scale(scale, -scale, scale);
		rotationY += 0.6f;
		GlStateManager.rotate(20, 1, 0, 0);
		GlStateManager.rotate(rotationY, 0, 1, 0);
		renderItem(stack);
		GlStateManager.disableDepth();
		GlStateManager.popMatrix();
	}

	public static void renderItem(ItemStack itemStack) {
		if(itemStack == null)return;
		GlStateManager.pushMatrix();
		Minecraft mc = Minecraft.getMinecraft();
		

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		RenderHelper.enableStandardItemLighting();
		mc.getRenderItem().renderItem(itemStack, TransformType.GROUND);	
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}