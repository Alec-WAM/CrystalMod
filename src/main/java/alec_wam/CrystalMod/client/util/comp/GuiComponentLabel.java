package alec_wam.CrystalMod.client.util.comp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;


import org.lwjgl.opengl.GL11;

public class GuiComponentLabel extends BaseComponent {

	public String text;
	private String textObj;
	private float scale = 1f;
	private String textDelta;
	private String[] formattedText;
	private int maxHeight, maxWidth;
	private float additionalScale = 1.0f;
	private int additionalLineHeight = 0;

	private static FontRenderer getFontRenderer() {
		return Minecraft.getMinecraft().fontRendererObj;
	}

	/*public GuiComponentLabel(int x, int y, int width, int height, String txt) {
		this(x, y, width, height, txt.getValue());
		textObj = txt;
	}*/

	public GuiComponentLabel(int x, int y, String text) {
		this(x, y, getFontRenderer().getStringWidth(text), getFontRenderer().FONT_HEIGHT, text);
	}

	/*public GuiComponentLabel(int x, int y, String txt) {
		this(x, y, txt.getValue());
		textObj = txt;
	}*/

	public GuiComponentLabel(int x, int y, int width, int height, String text) {
		super(x, y);
		this.text = text;
		this.formattedText = new String[10];
		setMaxHeight(height);
		setMaxWidth(width);
	}
	
	public void setText(String text){
		this.text = text;
		this.formattedText = new String[10];
	}

	@SuppressWarnings("unchecked")
	private void compileFormattedText(FontRenderer fr) {
		// if (textDelta != null && textDelta.equals(getText())) return;
		textDelta = getText();
		if (textDelta == null) return;
		formattedText = (String[])fr.listFormattedStringToWidth(textDelta, getMaxWidth()).toArray(formattedText);
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc);
		additionalScale = sr.getScaleFactor() % 2 == 1 && scale < 1f? 0.667f : 1f;
		if (getMaxHeight() < minecraft.fontRendererObj.FONT_HEIGHT) return;
		if (getMaxWidth() < minecraft.fontRendererObj.getCharWidth('m')) return;
		GL11.glPushMatrix();
		GL11.glTranslated(offsetX + x, offsetY + y, 1);
		GL11.glScalef(scale * additionalScale, scale * additionalScale, scale * additionalScale);
		compileFormattedText(minecraft.fontRendererObj);
		int offset = 0;
		int lineCount = 0;
		for (String s : formattedText) {
			if (s == null) break;
			minecraft.fontRendererObj.drawString(s, 0, offset, 4210752);
			offset += getFontHeight();
			if (++lineCount >= getMaxLines()) break;
		}
		GL11.glPopMatrix();
	}

	private int calculateHeight() {
		FontRenderer fr = getFontRenderer();
		compileFormattedText(fr);
		int offset = 0;
		int lineCount = 0;
		for (String s : formattedText) {
			if (s == null) break;
			offset += getFontHeight();
			if (++lineCount >= getMaxLines()) break;
		}
		return offset;
	}

	private int calculateWidth() {
		FontRenderer fr = getFontRenderer();
		compileFormattedText(fr);
		float maxWidth = 0;
		for (String s : formattedText) {
			if (s == null) break;
			float width = fr.getStringWidth(s);
			if (width > maxWidth) maxWidth = width;
		}
		return (int)maxWidth;
	}

	public GuiComponentLabel setScale(float scale) {
		this.scale = scale;
		return this;
	}

	public float getScale() {
		return scale;
	}

	public GuiComponentLabel setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public void setAdditionalLineHeight(int lh) {
		this.additionalLineHeight = lh;
	}

	public int getFontHeight() {
		return getFontRenderer().FONT_HEIGHT + additionalLineHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public GuiComponentLabel setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public int getMaxLines() {
		return (int)Math.floor(getMaxHeight() / (scale / additionalScale)
				/ getFontHeight());
	}

	public int getMaxWidth() {
		return (int)(this.maxWidth / additionalScale);
	}

	@Override
	public int getHeight() {
		return (int)(Math.min(getMaxHeight(), calculateHeight()) + 0.5);
	}

	@Override
	public int getWidth() {
		return (int)(Math.min(getMaxWidth(), calculateWidth()) + 0.5);
	}

	public String getText() {
		String pre = (textObj != null? textObj.toString(): text);
		return pre == null? "" : pre;
	}
}