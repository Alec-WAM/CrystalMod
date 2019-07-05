package alec_wam.CrystalMod.client.render;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseHeadModel extends GenericHeadModel {
	protected final RendererModel head;


   public HorseHeadModel(float p_i51065_1_) {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.head = new RendererModel(this, 0, 35);
      this.head.addBox(-2.05F, -6.0F, -2.0F, 4, 6, 7);
      this.head.rotateAngleX = ((float)Math.PI / 6F);
      RendererModel renderermodel = new RendererModel(this, 0, 13);
      renderermodel.addBox(-3.0F, -11.0F, -2.0F, 6, 5, 7, p_i51065_1_);
      RendererModel renderermodel1 = new RendererModel(this, 56, 36);
      renderermodel1.addBox(-1.0F, -11.0F, 5.01F, 2, 11, 2, p_i51065_1_);
      RendererModel renderermodel2 = new RendererModel(this, 0, 25);
      renderermodel2.addBox(-2.0F, -11.0F, -7.0F, 4, 5, 5, p_i51065_1_);
      this.head.addChild(renderermodel);
      this.head.addChild(renderermodel1);
      this.head.addChild(renderermodel2);
      
      RendererModel renderermodel_2 = new RendererModel(this, 19, 16);
      renderermodel_2.addBox(0.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
      RendererModel renderermodel1_2 = new RendererModel(this, 19, 16);
      renderermodel1_2.addBox(-2.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
      head.addChild(renderermodel_2);
      head.addChild(renderermodel1_2);
   }

   @Override
   public void func_217104_a(float p_217104_1_, float p_217104_2_, float p_217104_3_, float p_217104_4_, float p_217104_5_, float p_217104_6_) {
	   GlStateManager.pushMatrix();
	   double scale = 0.8;
	   GlStateManager.scaled(scale, scale, scale);
	   this.head.rotateAngleY = p_217104_4_ * ((float)Math.PI / 180F);
	   this.head.rotateAngleX = p_217104_5_ * ((float)Math.PI / 180F);
	   head.render(p_217104_6_);
	   GlStateManager.popMatrix();
   }
}