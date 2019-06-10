package alec_wam.CrystalMod.client;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;

public enum BakedModelLoader implements ICustomModelLoader
{
    INSTANCE;
	public IResourceManager resourceManager;
	@Override
    public boolean accepts(ResourceLocation modelLocation) {
		System.out.println("OBJ MODEL TEST");
		
		if(modelLocation.getNamespace().equals(CrystalMod.MODID.toLowerCase())){
			if(modelLocation.getPath().equals("models/block/obj/crystalshard_1.json")){
				System.out.println("OBJ MODEL!!!!");
				return true;
			}
		}
        return false;
    }

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
		return OBJLoader.INSTANCE.loadModel(modelLocation);
	}
	
	/*protected ModelBlock loadModelCustom(ResourceLocation location) throws IOException{
		Reader reader = null;
		IResource iresource = null;

		ModelBlock lvt_5_2_;
		try {
			iresource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".obj"));
			reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
			
			lvt_5_2_ = ModelBlock.deserialize(reader);
			lvt_5_2_.name = location.toString();
			ModelBlock modelblock1 = lvt_5_2_;
			return modelblock1;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly((Closeable)iresource);
		}
	}*/
}
