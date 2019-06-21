package alec_wam.CrystalMod.compatibility.materials;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

public enum DustModelLoader implements ICustomModelLoader {
	INSTANCE;
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		System.out.println("DustModel Loader Reload");
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		CrystalMod.LOGGER.info("Loading Model Pre2: "+modelLocation);
		if(modelLocation.getNamespace().equals(CrystalMod.MODID)){
			System.out.println("Loading Model Pre: "+modelLocation);
			if(modelLocation.getPath().startsWith("dust_")){
				System.out.println("Loading Model Dust: "+modelLocation);
				
				
				String name = modelLocation.getPath().substring(5);
				if(!name.isEmpty()){
					if(MaterialLoader.DUST_ITEMS.containsKey(name)){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
		return ModelDust.MODEL;
	}

}
