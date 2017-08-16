package alec_wam.CrystalMod.items.tools.backpack;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ModelBackpack extends ModelBiped
{
	public ModelRenderer Bottom_Half;
    public ModelRenderer TopHalf;
    public ModelRenderer Strap_Long;
    public ModelRenderer Strap_Long2;
  
    public ModelBackpack(float scale)
    {
    	super(scale, 0, 64, 32);
    
    	Bottom_Half = new ModelRenderer(this, 0, 14);
    	Bottom_Half.addBox(-6F, -14F, -10F, 12, 8, 10);
    	Bottom_Half.setRotationPoint(0F, 20F, 2F);
    	Bottom_Half.setTextureSize(64, 32);
    	setRotation(Bottom_Half, 0F, this.bipedBody.rotateAngleY, 0F);
    	TopHalf = new ModelRenderer(this, 0, 0);
    	TopHalf.addBox(-6F, -13F, -8F, 12, 6, 8);
    	TopHalf.setRotationPoint(0F, 13F, 2F);
    	TopHalf.setTextureSize(64, 32);
    	setRotation(TopHalf, 0F, this.bipedBody.rotateAngleY, 0F);
    	Strap_Long = new ModelRenderer(this, 57, 3);
    	Strap_Long.addBox(4F, -12F, 11F, 4, 12, 0);
    	Strap_Long.setRotationPoint(3F, 11F, 8F);
    	Strap_Long.setTextureSize(64, 32);
    	setRotation(Strap_Long, 0F, 0F, 0F);
    	Strap_Long2= new ModelRenderer(this, 57, 3);
    	Strap_Long2.addBox(-2F, -12F, 11F, 4, 12, 0);
    	Strap_Long2.setRotationPoint(3F, 11F, 8F);
    	Strap_Long2.setTextureSize(64, 32);
    	setRotation(Strap_Long2, 0F, 0F, 0F);
    }
  
    @Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
    	GlStateManager.pushMatrix();
    
    	this.TopHalf.render(f5);
    	this.Bottom_Half.render(f5);
    	this.Strap_Long.render(f5);
    	this.Strap_Long2.render(f5);
    	
    	GlStateManager.popMatrix();
    }
  
    public void setOffset(ModelRenderer modelRenderer, float x, float y, float z)
    {
    	modelRenderer.offsetX = x;
    	modelRenderer.offsetY = y;
    	modelRenderer.offsetZ = z;
    }
  
    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
    	model.rotateAngleX = x;
    	model.rotateAngleY = y+180F-19.8F;
    	model.rotateAngleZ = z;
    }
  
    @Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity par7Entity)
    {
    	super.setRotationAngles(f, f1, f2, f3, f4, f5, par7Entity);

    	this.bipedBody.postRender(f5);
    	
    	if(par7Entity !=null && par7Entity instanceof EntityLivingBase){
    		EntityLivingBase liv = (EntityLivingBase)par7Entity;
    		if (liv.isSneaking()){
    			GlStateManager.rotate(25.0F, 1.0F, 0.0F, 0.0F);
        		GlStateManager.translate(0, 0.2f, -0.1F);
            }
    	}
    }
    
  	public void renderAll(boolean sleepRoll)
  	{
  		GlStateManager.pushMatrix();
      	this.Bottom_Half.render(0.0625F);
      	this.TopHalf.render(0.0625F);
      
      	this.Strap_Long.render(0.0625F);
      	this.Strap_Long2.render(0.0625F);
      	GlStateManager.popMatrix();
  	}
}
