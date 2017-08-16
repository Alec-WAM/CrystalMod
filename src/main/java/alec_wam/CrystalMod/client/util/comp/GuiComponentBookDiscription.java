package alec_wam.CrystalMod.client.util.comp;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiComponentBookDiscription extends BaseComponent {

	public String text;
	private int maxHeight, maxWidth;

	private static FontRenderer getFontRenderer() {
		return Minecraft.getMinecraft().fontRendererObj;
	}

	public GuiComponentBookDiscription(int x, int y, String text) {
		this(x, y, getFontRenderer().getStringWidth(text), getFontRenderer().FONT_HEIGHT, text);
	}

	public GuiComponentBookDiscription(int x, int y, int width, int height, String text) {
		super(x, y);
		this.text = text;
		setMaxHeight(height);
		setMaxWidth(width);
	}
	
	public GuiComponentBookDiscription setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}
	
	public int getMaxHeight(){
		return this.maxHeight;
	}
	
	public GuiComponentBookDiscription setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}
	
	public int getMaxWidth(){
		return this.maxWidth;
	}
	
	public void setText(String text){
		this.text = text;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glPushMatrix();
		GlStateManager.translate(offsetX+x, offsetY+y, 0);
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		float scale = 0.5f;
		float additionalScale = sr.getScaleFactor() % 2 == 1 && scale < 1f? 0.667f : 1f;
		GL11.glScalef(scale * additionalScale, scale * additionalScale, scale * additionalScale);
		renderText(0, 0, this.maxWidth, this.maxHeight, this.text);
		GL11.glPopMatrix();
	}
	
	public static void renderText(int x, int y, int width, int height, String unlocalizedText) {
		renderText(x, y, width, height, 10, false, 0, unlocalizedText);
	}

	@SideOnly(Side.CLIENT)
	public static void renderText(int x, int y, int width, int height, int paragraphSize, boolean useUnicode, int color, String unlocalizedText) {
		x += 2;
		y += 10;
		width -= 4;

		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		boolean unicode = font.getUnicodeFlag();
		if(useUnicode)
			font.setUnicodeFlag(true);
		String text = I18n.format(unlocalizedText).replaceAll("&", "\u00a7");
		String[] textEntries = text.split("<br>");

		List<List<String>> lines = new ArrayList<List<String>>();

		String controlCodes;
		for(String s : textEntries) {
			List<String> words = new ArrayList<String>();
			String lineStr = "";
			String[] tokens = s.split(" ");
			for(String token : tokens) {
				String prev = lineStr;
				String spaced = token + " ";
				lineStr += spaced;

				controlCodes = toControlCodes(getControlCodes(prev));
				if(font.getStringWidth(lineStr) > width) {
					lines.add(words);
					lineStr = controlCodes + spaced;
					words = new ArrayList<String>();
				}

				words.add(controlCodes + token);
			}

			if(!lineStr.isEmpty())
				lines.add(words);
			lines.add(new ArrayList<String>());
		}

		for(List<String> words : lines) {
			words.size();
			int xi = x;
			int spacing = 4;
			words.size();
			int compensationSpaces = 0;
			/*boolean justify = ConfigHandler.lexiconJustifiedText && wcount > 0 && lines.size() > i && !lines.get(i + 1).isEmpty();

			if(justify) {
				String s = Joiner.on("").join(words);
				int swidth = font.getStringWidth(s);
				int space = width - swidth;

				spacing = wcount == 1 ? 0 : space / (wcount - 1);
				compensationSpaces = wcount == 1 ? 0 : space % (wcount - 1);
			}*/

			for(String s : words) {
				int extra = 0;
				if(compensationSpaces > 0) {
					compensationSpaces--;
					extra++;
				}
				font.drawString(s, xi, y, color);
				xi += font.getStringWidth(s) + spacing + extra;
			}

			y += words.isEmpty() ? paragraphSize : 10;
		}

		font.setUnicodeFlag(unicode);
	}

	private static String getControlCodes(String s) {
		String controls = s.replaceAll("(?<!\u00a7)(.)", "");
		return controls.replaceAll(".*r", "r");
	}

	private static String toControlCodes(String s) {
		return s.replaceAll(".", "\u00a7$0");
	}

	@Override
	public int getWidth() {
		return this.getMaxWidth();
	}

	@Override
	public int getHeight() {
		return this.getMaxHeight();
	}

	public String getText() {
		return text;
	}
}