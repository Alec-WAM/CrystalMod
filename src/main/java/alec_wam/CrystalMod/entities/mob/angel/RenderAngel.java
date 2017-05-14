package alec_wam.CrystalMod.entities.mob.angel;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAngel extends RenderBiped<EntityAngel>
{
    private static final ResourceLocation ANGEL_TEXTURE = new ResourceLocation("crystalmod:textures/entities/angel.png");
    private int modelVersion;

    public RenderAngel(RenderManager p_i47190_1_)
    {
        super(p_i47190_1_, new ModelAngel(), 0.4F);
        this.modelVersion = ((ModelAngel)this.mainModel).getModelVersion();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityAngel entity)
    {
        return ANGEL_TEXTURE;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityAngel entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        int i = ((ModelAngel)this.mainModel).getModelVersion();

        if (i != this.modelVersion)
        {
            this.mainModel = new ModelAngel();
            this.modelVersion = i;
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityAngel entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.scale(0.8F, 0.8F, 0.8F);
    }
}