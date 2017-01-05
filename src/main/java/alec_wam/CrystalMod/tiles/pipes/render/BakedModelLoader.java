package alec_wam.CrystalMod.tiles.pipes.render;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.pipes.ModelPipe;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class BakedModelLoader implements ICustomModelLoader {

    public static final ModelPipe PIPE_MODEL = new ModelPipe();

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getResourceDomain().equals(CrystalMod.MODID.toLowerCase()) && "crystalpipe".equals(modelLocation.getResourcePath());
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return PIPE_MODEL;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}
