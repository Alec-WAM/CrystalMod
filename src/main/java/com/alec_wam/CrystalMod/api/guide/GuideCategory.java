package com.alec_wam.CrystalMod.api.guide;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuideCategory implements Comparable<GuideCategory> {

	private static int count = 0;

	public final String unlocalizedName;
	public final List<GuideEntry> entries = new ArrayList<GuideEntry>();
	private final int sortingId;
	private ResourceLocation icon;
	private int priority = 5;

	/**
	 * @param unlocalizedName The unlocalized name of this category. This will be localized by the client display.
	 */
	public GuideCategory(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		sortingId = count;
		count++;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public GuideCategory setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	public int getSortingPriority() {
		return priority;
	}

	public final int getSortingId() {
		return sortingId;
	}

	public GuideCategory setIcon(ResourceLocation icon) {
		this.icon = icon;
		return this;
	}

	public ResourceLocation getIcon() {
		return icon;
	}

	@Override
	public int compareTo(@Nonnull GuideCategory category) {
		return priority == category.priority ? sortingId - category.sortingId : category.priority - priority;
	}
}
