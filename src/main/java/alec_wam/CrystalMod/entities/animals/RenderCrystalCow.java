package alec_wam.CrystalMod.entities.animals;

import net.minecraft.client.model.ModelCow;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrystalCow extends RenderLiving<EntityCrystalCow>
{
    private static final ResourceLocation[] COW_TEXTURE = new ResourceLocation[]{new ResourceLocation("crystalmod:textures/entities/cow/blue_cow.png"), new ResourceLocation("crystalmod:textures/entities/cow/red_cow.png"), new ResourceLocation("crystalmod:textures/entities/cow/green_cow.png"), new ResourceLocation("crystalmod:textures/entities/cow/dark_cow.png"), new ResourceLocation("crystalmod:textures/entities/cow/pure_cow.png")};

    public RenderCrystalCow(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelCow(), 0.7F);
        this.addLayer(new LayerCrystalCowCrystals(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(EntityCrystalCow entity)
    {
        return COW_TEXTURE[entity.getColor()];
    }
    
    public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityCrystalCow> {

        @Override
        public Render<? super EntityCrystalCow> createRenderFor(RenderManager manager) {
          return new RenderCrystalCow(manager);
        }
    }
}
