package alec_wam.CrystalMod.items.guide;

import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.Language;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuidePageLoader implements IResourceManagerReloadListener {

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		Language language = FMLClientHandler.instance().getClient().getLanguageManager().getCurrentLanguage();
		String lang = language.getJavaLocale().getLanguage();
		ModLogger.info("Loading guide text... ("+lang+")");
		GuidePages.loadGuideText(lang);
		ModLogger.info("("+GuidePages.CHAPTERTEXT.size()+") chapters loaded.");
	}

}
