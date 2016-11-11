package alec_wam.CrystalMod.entities.pet;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.util.PlayerUtil;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderEntityBombomb extends RenderLiving<EntityBombomb>{

	private static final ResourceLocation defaultTextures = new ResourceLocation("crystalmod:textures/entities/bombomb/untamed.png");
    private static final ResourceLocation tamedTextures = new ResourceLocation("crystalmod:textures/entities/bombomb/tamed.png");
    
	public RenderEntityBombomb(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelBombomb(), 0.5f);
		this.addLayer(new LayerBombombColor(this));
	}

	/**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityBombomb entitylivingbaseIn, float partialTickTime)
    {
        float f = entitylivingbaseIn.getFlashIntensity(partialTickTime);
        float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        f = f * f;
        f = f * f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        GlStateManager.scale(f2, f3, f2);
    }

    /**
     * Gets an RGBA int color multiplier to apply.
     */
    protected int getColorMultiplier(EntityBombomb entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        float f = entitylivingbaseIn.getFlashIntensity(partialTickTime);

        if ((int)(f * 10.0F) % 2 == 0)
        {
            return 0;
        }
        else
        {
            int i = (int)(f * 0.2F * 255.0F);
            i = MathHelper.clamp_int(i, 0, 255);
            return i << 24 | 822083583;
        }
    }
	
    public void doRender(EntityBombomb entity, double x, double y, double z, float entityYaw, float partialTicks){
    	super.doRender(entity, x, y, z, entityYaw, partialTicks);
    	if (!this.renderOutlines)
        {
    		if(!entity.hasCustomName() && entity.getOwnerId() !=null){
    			String ownerName = ProfileUtil.getUsername(entity.getOwnerId());
    			if(!Strings.isNullOrEmpty(ownerName)){
		        	Team team = entity.getTeam();
		        	
		        	String name = team != null ? team.formatString(ownerName): ownerName;
	    			renderLivingLabel(entity, name, x, y, z, 64);
    			}
    		}
        }
    }
    
	@Override
	protected ResourceLocation getEntityTexture(EntityBombomb entity) {
		return entity.isTamed() ? tamedTextures : defaultTextures;
	}
	
    public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityBombomb> {

        @Override
        public Render<? super EntityBombomb> createRenderFor(RenderManager manager) {
          return new RenderEntityBombomb(manager);
        }
    }

}
