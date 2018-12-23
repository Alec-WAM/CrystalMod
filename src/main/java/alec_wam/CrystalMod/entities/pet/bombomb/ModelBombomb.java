package alec_wam.CrystalMod.entities.pet.bombomb;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelBombomb extends ModelBase
{
	ModelRenderer LeftFoot;
    ModelRenderer RightFoot;
    ModelRenderer Head;
    ModelRenderer Key;
    ModelRenderer FuseHolder;
    ModelRenderer Fuse;
  
    public ModelBombomb()
    {
    	textureWidth = 64;
    	textureHeight = 32;
    
    	LeftFoot = new ModelRenderer(this, 46, 11);
    	LeftFoot.addBox(-2F, -1F, -3F, 3, 2, 6);
    	LeftFoot.setRotationPoint(-2F, 23F, 0F);
    	LeftFoot.setTextureSize(64, 32);
    	LeftFoot.mirror = true;
    	setRotation(LeftFoot, 0F, 0F, 0F);
	      RightFoot = new ModelRenderer(this, 46, 0);
	      RightFoot.addBox(-2F, -1F, -3F, 3, 2, 6);
	      RightFoot.setRotationPoint(3F, 23F, 0F);
	      RightFoot.setTextureSize(64, 32);
	      RightFoot.mirror = true;
	      setRotation(RightFoot, 0F, 0F, 0F);
	      Head = new ModelRenderer(this, 0, 11);
	      Head.addBox(-4F, -4F, -4F, 8, 8, 8);
	      Head.setRotationPoint(0F, 18F, 0F);
	      Head.setTextureSize(64, 32);
	      Head.mirror = true;
	      setRotation(Head, 0F, 0F, 0F);
	      Key = new ModelRenderer(this, 34, 0);
	      Key.addBox(0F, -3F, 0F, 0, 6, 3);
	      Key.setRotationPoint(0F, 18F, 4F);
	      Key.setTextureSize(64, 32);
	      Key.mirror = true;
	      setRotation(Key, 0F, 0F, 0F);
	      FuseHolder = new ModelRenderer(this, 43, 22);
	      FuseHolder.addBox(0F, 0F, 0F, 4, 1, 4);
	      FuseHolder.setRotationPoint(-2F, 13F, -2F);
	      FuseHolder.setTextureSize(64, 32);
	      FuseHolder.mirror = true;
	      setRotation(FuseHolder, 0F, 0F, 0F);
	      Fuse = new ModelRenderer(this, 0, 0);
	      Fuse.addBox(0F, 0F, 0F, 1, 3, 1);
	      Fuse.setRotationPoint(1F, 10F, 0F);
	      Fuse.setTextureSize(64, 32);
	      Fuse.mirror = true;
	      setRotation(Fuse, 0F, 0F, 0.5205006F);
    }
  
    @Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
	    super.render(entity, f, f1, f2, f3, f4, f5);
	    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	    LeftFoot.render(f5);
	    RightFoot.render(f5);
	    Head.render(f5);
	    FuseHolder.render(f5);
	    Fuse.render(f5);
	    GlStateManager.pushMatrix();
	    //GlStateManager.translate(0.5, 0.5, 0.5);
	    //GlStateManager.rotate(((EntityBombomb)entity).keyRotation, 0, 0, 1);
	    //GlStateManager.translate(-0.5, -0.5, -0.5);
	    Key.render(f5);
	    GlStateManager.popMatrix();
    }
  
    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
    	model.rotateAngleX = x;
    	model.rotateAngleY = y;
    	model.rotateAngleZ = z;
    }
  
    @Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
	  this.RightFoot.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
	  this.LeftFoot.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2;
    }
  
    public void renderAll()
    {
	      this.LeftFoot.render(0.0625F);
	      this.RightFoot.render(0.0625F);
	      this.Head.render(0.0625F);
	      this.Key.render(0.0625F);
	      this.FuseHolder.render(0.0625F);
	      this.Fuse.render(0.0625F);
    }

}
