package alec_wam.CrystalMod.entities.mob.devil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDevil extends RenderBiped<EntityDevil>
{
    private static final ResourceLocation DEVIL_TEXTURE = new ResourceLocation("crystalmod:textures/entities/devil.png");
    private int modelVersion;

    public RenderDevil(RenderManager p_i47190_1_)
    {
        super(p_i47190_1_, new ModelDevil(), 0.4F);
        this.modelVersion = ((ModelDevil)this.mainModel).getModelVersion();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(EntityDevil entity)
    {
        return DEVIL_TEXTURE;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    @Override
	public void doRender(EntityDevil entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        int i = ((ModelDevil)this.mainModel).getModelVersion();

        if (i != this.modelVersion)
        {
            this.mainModel = new ModelDevil();
            this.modelVersion = i;
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    @Override
	protected void preRenderCallback(EntityDevil entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.scale(0.8F, 0.8F, 0.8F);
    }
}