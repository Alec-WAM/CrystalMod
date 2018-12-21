package alec_wam.CrystalMod.tiles.machine.worksite.gui.elements;

import java.util.EnumSet;
import java.util.List;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

public class Upgrades extends GuiElement {

	EnumSet<WorksiteUpgrade> upgrades;
	public Upgrades(int x, int y, EnumSet<WorksiteUpgrade> upgrades) {
		super(x, y);
		this.upgrades = upgrades;
	}

	public void setUpgrades(EnumSet<WorksiteUpgrade> upgrades){
		this.upgrades = upgrades;
	}
	
	final ItemStack renderStack = new ItemStack(ModItems.worksiteUpgrade);
	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		GlStateManager.translate(renderX, renderY, 20);
		int index = 0;
		for(WorksiteUpgrade u : upgrades){
			GlStateManager.translate(index * 18, 0, 0);
			renderStack.setItemDamage(u.ordinal());
			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(renderStack, 0, 0);;
			index++;
		}
	}

}