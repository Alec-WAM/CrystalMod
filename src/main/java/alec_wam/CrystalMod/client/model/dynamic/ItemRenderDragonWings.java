package alec_wam.CrystalMod.client.model.dynamic;

import alec_wam.CrystalMod.client.model.ModelDragonWings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemRenderDragonWings implements ICustomItemRenderer {
	private ModelDragonWings dragonModel = new ModelDragonWings(0.0F);
    private static final ResourceLocation enderDragonTextures = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    
	@Override
	public void render(ItemStack stack) {
		GlStateManager.pushMatrix();
    	Minecraft.getMinecraft().getTextureManager().bindTexture(enderDragonTextures);
    	GlStateManager.pushMatrix();
    	
    	float yaw = 90;
    	
    	GlStateManager.rotate(-yaw, 0, 1, 0);
    	GlStateManager.rotate(90, 1, 0, 0);
    	GlStateManager.translate(0, 0.3f, -0.6F);
    	
    	if(lastTransform == TransformType.FIXED){
    		GlStateManager.rotate(90, 0, 0, 1);    
        	GlStateManager.translate(-0.3f, 0f, 0.15f);    
        	GlStateManager.scale(2f, 2f, 2f);   	
		}
    	
    	if(lastTransform == TransformType.FIRST_PERSON_RIGHT_HAND){
    		GlStateManager.rotate(180, 0, 0, 1);    
        	GlStateManager.translate(0.3f, 0.5f, 0.0f);   
		}
    	
    	if(lastTransform == TransformType.FIRST_PERSON_LEFT_HAND){
    		GlStateManager.translate(0.3f, -0.5f, 0.0f);   
		}
    	
    	if(lastTransform == TransformType.GROUND){
    		GlStateManager.translate(0f, 0f, 0.3f);  
		}
    	
    	double scale = 0.18;
    	GlStateManager.scale(scale, scale, scale);
    	float f = 0.5f;
    	for (int j = 0; j < 2; ++j)
        {
    		GlStateManager.enableCull();
            float f11 = f * (float)Math.PI * 2.0F;
            dragonModel.wing.rotateAngleX = 0.125F - (float)Math.cos(f11) * 0.2F;
            dragonModel.wing.rotateAngleY = 0.25F;
            
            dragonModel.wing.rotateAngleZ = 0.25f+(float)(Math.sin(f11) + 0.125D) * 0.4F;
            
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
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    	GlStateManager.popMatrix();
	}
	
	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}

}
