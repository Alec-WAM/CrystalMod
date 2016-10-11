package alec_wam.CrystalMod.client.util.comp.book;

import alec_wam.CrystalMod.client.util.comp.GuiComponentLabel;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.Minecraft;


public class TitledPage extends BlankPage {

	private GuiComponentLabel lblTitle;
	private GuiComponentLabel lblContent;

	public TitledPage(String title, String content) {

		String translatedTitle = Lang.translateToLocal(title);
		String translatedContent = String.format(Lang.translateToLocal(content));

		int x = (getWidth() - Minecraft.getMinecraft().fontRendererObj.getStringWidth(translatedTitle)) / 2;
		lblTitle = new GuiComponentLabel(x, 12, translatedTitle);

		lblContent = new GuiComponentLabel(27, 40, 300, 300, translatedContent);
		lblContent.setScale(0.5f);
		lblContent.setAdditionalLineHeight(2);

		addComponent(lblTitle);
		addComponent(lblContent);
	}

}
