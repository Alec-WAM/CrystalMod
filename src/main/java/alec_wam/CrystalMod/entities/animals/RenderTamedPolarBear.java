package alec_wam.CrystalMod.entities.animals;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTamedPolarBear extends RenderLiving<EntityTamedPolarBear>
{
    private static final ResourceLocation POLAR_BEAR_TEXTURE = new ResourceLocation("textures/entity/bear/polarbear.png");

    public RenderTamedPolarBear(RenderManager p_i47197_1_)
    {
        super(p_i47197_1_, new ModelTamedPolarBear(), 0.7F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityTamedPolarBear entity)
    {
        return POLAR_BEAR_TEXTURE;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityTamedPolarBear entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityTamedPolarBear entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.scale(1.2F, 1.2F, 1.2F);
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }
}