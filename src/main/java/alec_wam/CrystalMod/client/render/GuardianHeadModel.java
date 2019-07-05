package alec_wam.CrystalMod.client.render;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianHeadModel extends GenericHeadModel {
	private static final float[] field_217136_a = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
	private static final float[] field_217137_b = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
	private static final float[] field_217138_f = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
	private static final float[] field_217139_g = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
	private static final float[] field_217140_h = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
	private static final float[] field_217141_i = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
	   
   private final RendererModel head;
   private final RendererModel eye;
   private final RendererModel[] spikes;

   public GuardianHeadModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.spikes = new RendererModel[12];
      this.head = new RendererModel(this);
      this.head.setTextureOffset(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
      this.head.setTextureOffset(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
      this.head.setTextureOffset(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
      this.head.setTextureOffset(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
      this.head.setTextureOffset(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);
      
      for(int i = 0; i < this.spikes.length; ++i) {
    	  this.spikes[i] = new RendererModel(this, 0, 0);
    	  this.spikes[i].addBox(-1.0F, -4.5F, -1.0F, 2, 9, 2);
    	  this.head.addChild(this.spikes[i]);
      }
      
      this.eye = new RendererModel(this, 8, 0);
      this.eye.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1);
      this.head.addChild(this.eye);
   }

   @Override
   public void func_217104_a(float p_217104_1_, float p_217104_2_, float p_217104_3_, float p_217104_4_, float p_217104_5_, float p_217104_6_) {
	   GlStateManager.pushMatrix();
	   double scale = 0.6;
	   GlStateManager.scaled(scale, scale, scale);
	   float f1 = /*(1.0F - p_212844_1_.getSpikesAnimation(f))*/0.5f * 0.55F;
	   for(int i = 0; i < 12; ++i) {
		   this.spikes[i].rotateAngleX = (float)Math.PI * field_217136_a[i];
		   this.spikes[i].rotateAngleY = (float)Math.PI * field_217137_b[i];
		   this.spikes[i].rotateAngleZ = (float)Math.PI * field_217138_f[i];
		   this.spikes[i].rotationPointX = field_217139_g[i] * (1.0F + MathHelper.cos(p_217104_4_ * 1.5F + (float)i) * 0.01F - f1);
		   this.spikes[i].rotationPointY = 16.0F + field_217140_h[i] * (1.0F + MathHelper.cos(p_217104_4_ * 1.5F + (float)i) * 0.01F - f1);
		   this.spikes[i].rotationPointZ = field_217141_i[i] * (1.0F + MathHelper.cos(p_217104_4_ * 1.5F + (float)i) * 0.01F - f1);
	   }
	   
	   this.eye.rotationPointZ = -8.25F;
	   this.head.rotateAngleY = p_217104_4_ * ((float)Math.PI / 180F);
	   this.head.rotateAngleX = p_217104_5_ * ((float)Math.PI / 180F);
	   this.head.render(p_217104_6_);
	   GlStateManager.popMatrix();	   
   }
}