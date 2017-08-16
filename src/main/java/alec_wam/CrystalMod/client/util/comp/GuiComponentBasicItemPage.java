package alec_wam.CrystalMod.client.util.comp;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;


public class GuiComponentBasicItemPage extends BaseComponent {

	private GuiComponentBookDiscription lblDescription;
	private GuiComponentLabel lblTitle;
	private GuiComponentItemStackBasic output;
	private List<ItemStack> stacks;

	public GuiComponentBasicItemPage(String title, String description, List<ItemStack> resultingItem) {
		super(0, 0);
		stacks = resultingItem;
		String translatedTitle = Lang.translateToLocal(title);
		
		lblTitle = new GuiComponentLabel((getWidth() - Minecraft.getMinecraft().fontRendererObj.getStringWidth(translatedTitle)) / 2, 12, translatedTitle);
		lblDescription = new GuiComponentBookDiscription(27, 95, 340, 51, "");
		setDescription(description, false);

		output = new GuiComponentItemStackBasic(90, 40, stacks.get(0));

		addComponent(lblDescription);
		addComponent(lblTitle);
		addComponent(output);
	}
	
	public void setDescription(String desc, boolean isTranslated){
		String translatedDescription = isTranslated ? desc.replaceAll("\\\\n", "\n") : Lang.translateToLocal(desc).replaceAll("\\\\n", "\n");
		this.lblDescription.setText(translatedDescription);
	}
	
	public String getDescription(){
		return lblDescription.getText();
	}
	
	
	public int tick = 0;
	public int listIndex = 0;
	@Override
	public void updateComp(){
		super.updateComp();
		tick++;
		if(!CrystalMod.proxy.isShiftKeyDown() && Util.isMultipleOf(tick, 40)){
			listIndex++;
			listIndex%=stacks.size();
			output.stack = stacks.get(listIndex);
		}
	}
	
	@Override
	public int getWidth() {
		return 220;
	}

	@Override
	public int getHeight() {
		return 200;
	}

}