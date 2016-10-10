package com.alec_wam.CrystalMod.api.guide;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class GuidePage {

	public String unlocalizedName;
	public boolean skipRegistry;

	public GuidePage(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}

	/**
	 * Does the rendering for this page.
	 * @param gui The active GuiScreen
	 * @param mx The mouse's relative X position.
	 * @param my The mouse's relative Y position.
	 */
	@SideOnly(Side.CLIENT)
	public abstract void renderScreen(IGuiGuideEntry gui, int mx, int my);

	/**
	 * Called per update tick. Non gui-sensitive version, kept for backwards compatibility only.
	 */
	@SideOnly(Side.CLIENT)
	public void updateScreen() {}

	/**
	 * Called per update tick. Feel free to override fully, the
	 * call to updateScreen() is for backwards compatibility.
	 */
	@SideOnly(Side.CLIENT)
	public void updateScreen(IGuiGuideEntry gui) {
		updateScreen();
	}

	/**
	 * Called when this page is opened, be it via initGui() or when the player changes page.
	 * You can add buttons and whatever you'd do on initGui() here.
	 */
	@SideOnly(Side.CLIENT)
	public void onOpened(IGuiGuideEntry gui) {}

	/**
	 * Called when this page is opened, be it via closing the gui or when the player changes page.
	 * Make sure to dispose of anything you don't use any more, such as buttons in the gui's buttonList.
	 */
	@SideOnly(Side.CLIENT)
	public void onClosed(IGuiGuideEntry gui) {}

	/**
	 * Called when a button is pressed, equivalent to GuiScreen.actionPerformed.
	 */
	@SideOnly(Side.CLIENT)
	public void onActionPerformed(IGuiGuideEntry gui, GuiButton button) {}

	/**
	 * Called when a key is pressed.
	 */
	@SideOnly(Side.CLIENT)
	public void onKeyPressed(char c, int key) {}

	/**
	 * Called when {@link GuideEntry#setGuidePages(GuidePage...)} is called.
	 */
	public void onPageAdded(GuideEntry entry, int index) {}

	/**
	 * Shows the list of recipes present in this page for display in the category
	 * page. Can return null for an entry with no recipes.
	 */
	public List<ItemStack> getDisplayedRecipes() {
		return null;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public GuidePage setSkipRegistry() {
		skipRegistry = true;
		return this;
	}
}
