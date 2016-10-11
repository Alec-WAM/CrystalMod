package alec_wam.CrystalMod.client.util.comp.book;

import alec_wam.CrystalMod.client.util.comp.GuiComponentLabel;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.Minecraft;

public class SectionPage extends BlankPage {

	private GuiComponentLabel title;

	public SectionPage(String name) {
		String txt = Lang.translateToLocal(name);
		int strWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(txt) * 2;
		int x = (getWidth() - strWidth) / 2;
		title = new GuiComponentLabel(x - 10, 70, txt);
		title.setScale(2f);
		addComponent(title);
	}
}
