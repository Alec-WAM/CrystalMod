package alec_wam.CrystalMod.api.guide;

import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGuideEntry{

    List<IGuideChapter> getAllChapters();

    String getIdentifier();

    String getLocalizedName();

    String getLocalizedNameWithFormatting();

    void addChapter(IGuideChapter chapter);

    @SideOnly(Side.CLIENT)
    List<IGuideChapter> getChaptersForDisplay(String searchBarText);
}
