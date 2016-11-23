package alec_wam.CrystalMod.api.guide;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface IGuideEntry{

    List<IGuideChapter> getAllChapters();

    String getIdentifier();

    String getLocalizedName();

    String getLocalizedNameWithFormatting();

    void addChapter(IGuideChapter chapter);

    @SideOnly(Side.CLIENT)
    List<IGuideChapter> getChaptersForDisplay(String searchBarText);
}
