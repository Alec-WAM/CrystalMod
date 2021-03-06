package alec_wam.CrystalMod.client.model;

import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.handler.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerDragonWings implements LayerRenderer<AbstractClientPlayer> {
	
	private ModelDragonWings dragonModel = new ModelDragonWings(0.0F);
    private static final ResourceLocation enderDragonTextures = new ResourceLocation("textures/entity/enderdragon/dragon.png");
	
	public LayerDragonWings() {
		
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if(!EventHandler.hasDragonWings(entity))return;
    	
    	ExtendedPlayer extPlayer = ExtendedPlayerProvider.getExtendedPlayer(entity);
    	if(extPlayer == null)return;
    	
    	GlStateManager.pushMatrix();
    	Minecraft.getMinecraft().getTextureManager().bindTexture(enderDragonTextures);
    	GlStateManager.pushMatrix();
    	
    	GlStateManager.rotate(-90, 1, 0, 0);
    	
    	if (entity.isSneaking())
        {
    		GlStateManager.rotate(25.0F, 1.0F, 0.0F, 0.0F);
    		GlStateManager.translate(0, 0.0f, 0.3F);
        }
    	
    	GlStateManager.translate(0, -0.3f, 0.1f);
    	double scale2 = 0.2;
    	GlStateManager.scale(scale2, scale2, scale2);
    	
    	float f = extPlayer.prevWingAnimTime + (extPlayer.wingAnimTime - extPlayer.prevWingAnimTime) * partialTicks;
    	for (int j = 0; j < 2; ++j)
        {
    		GlStateManager.enableCull();
            float f11 = f * (float)Math.PI * 2.0F;
            dragonModel.wing.rotateAngleX = 0.125F - (float)Math.cos(f11) * 0.2f;
            dragonModel.wing.rotateAngleY = 0.25F;
            if(!entity.onGround)dragonModel.wing.rotateAngleZ = 0.45f+(float)(Math.sin(f11) + 0.125D) * 0.8F;
            else dragonModel.wing.rotateAngleZ = 0.25f+(float)(Math.sin(f11) + 0.125D) * 0.4F;
            dragonModel.wingTip.rotateAngleZ = -((float)(Math.sin(f11 + 2.0F) + 0.5D)) * 0.75F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.4, 0, 0);
            dragonModel.wing.render(0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.scale(-1.0F, 1.0F, 1.0F);

            if (j == 0)
            {
                GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
            }
        }
    	
    	GlStateManager.popMatrix();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableCull();
    	GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}

