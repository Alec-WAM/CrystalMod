package alec_wam.CrystalMod.tiles.machine.worksite.gui.elements;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.tiles.machine.worksite.gui.GuiContainerWorksiteBase.ActivationEvent;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;


public class Button extends GuiElement {

	private boolean pressed = false;
	protected FontRenderer fr;
	protected String text;
	protected int textX;
	protected int textY;

	public Button(int topLeftX, int topLeftY, int width, int height, String text) {
		super(topLeftX, topLeftY, width, height);
		fr = Minecraft.getMinecraft().fontRendererObj;
		this.setText(text);
		this.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (pressed && enabled && visible
						&& isMouseOverElement(evt.mx, evt.my)) {
					Minecraft
							.getMinecraft()
							.getSoundHandler()
							.playSound(
									PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					onPressed();
				}
				pressed = false;
				return true;
			}
		});
		this.addNewListener(new Listener(Listener.MOUSE_DOWN) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (enabled && visible && isMouseOverElement(evt.mx, evt.my)) {
					pressed = true;
				}
				return true;
			}
		});
	}

	public final void setText(String text) {
		this.text = Lang.translateToLocal(text);
		int tw = fr.getStringWidth(this.text);
		textX = (width - tw) / 2;
		textY = (height - 8) / 2;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (visible) {
			Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture1);
			int textureSize = 256;
			int startX = 0;
			int startY = enabled ? isMouseOverElement(mouseX, mouseY) ? 80 : 40
					: 0;
			int textColor = startY == 80 ? 0xa0a0a0ff : 0xffffffff;// grey or
																	// white
			int usedWidth = 256;
			int usedHeight = 40;
			RenderUtil.renderQuarteredTexture(textureSize, textureSize,
					startX, startY, usedWidth, usedHeight, renderX, renderY,
					width, height);
			fr.drawStringWithShadow(text, renderX + textX, renderY + textY,
					textColor);
			GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
		}
	}

	/**
	 * sub-classes may override this as an on-pressed callback method is called
	 * whenever the 'pressed' sound is played. uses built-in click listener for
	 * sound to trigger method
	 */
	protected void onPressed() {

	}

}
