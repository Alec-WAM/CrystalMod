package alec_wam.CrystalMod.api.guide;

import net.minecraft.item.ItemStack;

public interface IGuideChapter {

    IGuidePage[] getAllPages();

    String getLocalizedName();

    String getLocalizedNameWithFormatting();

    IGuideEntry getEntry();

    ItemStack getDisplayItemStack();

    String getIdentifier();

    int getPageIndex(IGuidePage page);
}
