package alec_wam.CrystalMod.api.guide;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.util.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuideEntry implements Comparable<GuideEntry> {

	public final String unlocalizedName;
	public final GuideCategory category;

	public final List<GuidePage> pages = new ArrayList<GuidePage>();
	private boolean priority = false;
	private ItemStack icon = null;
	
	private final List<ItemStack> extraDisplayedRecipes = new ArrayList<ItemStack>();

	/**
	 * @param unlocalizedName The unlocalized name of this entry. This will be localized by the client display.
	 */
	public GuideEntry(String unlocalizedName, GuideCategory category) {
		this.unlocalizedName = unlocalizedName;
		this.category = category;
	}

	/**
	 * Sets this page as prioritized, as in, will appear before others in the guide.
	 */
	public GuideEntry setPriority() {
		priority = true;
		return this;
	}

	/**
	 * Sets the display icon for this entry. Overriding the one already there. When adding recipe pages to the
	 * entry, this will be called once for the result of the first found recipe.
	 */
	public void setIcon(ItemStack stack) {
		icon = stack;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public boolean isPriority() {
		return priority;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public String getTagline() {
		return null; // Override this if you want a tagline. You probably do
	}

	@SideOnly(Side.CLIENT)
	public boolean isVisible() {
		return true;
	}

	/**
	 * Sets what pages you want this entry to have.
	 */
	public GuideEntry setGuidePages(GuidePage... pages) {
		this.pages.addAll(Arrays.asList(pages));

		for(int i = 0; i < this.pages.size(); i++) {
			GuidePage page = this.pages.get(i);
			if(!page.skipRegistry)
				page.onPageAdded(this, i);
		}

		return this;
	}

	/**
	 * Adds a page to the list of pages.
	 */
	public void addPage(GuidePage page) {
		pages.add(page);
	}

	public final String getNameForSorting() {
		return (priority ? 0 : 1) + Lang.translateToLocal(getUnlocalizedName());
	}

	public List<ItemStack> getDisplayedRecipes() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for(GuidePage page : pages) {
			List<ItemStack> l = page.getDisplayedRecipes();

			if(l != null) {
				ArrayList<ItemStack> itemsAddedThisPage = new ArrayList<ItemStack>();

				for(ItemStack s : l) {
					addItem: {
					for(ItemStack s1 : itemsAddedThisPage)
						if(s1.getItem() == s.getItem())
							break addItem;
					for(ItemStack s1 : list)
						if(s1.isItemEqual(s) && ItemStack.areItemStackTagsEqual(s1, s))
							break addItem;

					itemsAddedThisPage.add(s);
					list.add(s);
				}
				}
			}
		}
		
		list.addAll(extraDisplayedRecipes);

		return list;
	}
	
	public void addExtraDisplayedRecipe(ItemStack stack) {
		extraDisplayedRecipes.add(stack);
	}

	@Override
	public int compareTo(@Nonnull GuideEntry o) {
		return getNameForSorting().compareTo(o.getNameForSorting());
	}

}
