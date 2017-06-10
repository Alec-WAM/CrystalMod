package alec_wam.CrystalMod.entities.mob.devil;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDevil extends ModelBiped
{
    protected ModelRenderer leftWing;
    protected ModelRenderer rightWing;
    protected ModelRenderer leftHorn;
    protected ModelRenderer rightHorn;

    public ModelDevil()
    {
        this(0.0F);
    }

    public ModelDevil(float p_i47224_1_)
    {
        super(p_i47224_1_, 0.0F, 64, 64);
        this.bipedLeftLeg.showModel = false;
        this.bipedHeadwear.showModel = false;
        this.bipedRightLeg = new ModelRenderer(this, 32, 0);
        this.bipedRightLeg.addBox(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.rightWing = new ModelRenderer(this, 0, 32);
        this.rightWing.addBox(-20.0F, 0.0F, 0.0F, 20, 12, 1);
        this.leftWing = new ModelRenderer(this, 0, 32);
        this.leftWing.mirror = true;
        this.leftWing.addBox(0.0F, 0.0F, 0.0F, 20, 12, 1);
        
        this.rightHorn = new ModelRenderer(this, 0, 9).setTextureSize(128, 128);
        this.rightHorn.addBox(0.0F, -16.0F, 0.0F, 8, 7, 0);
        this.leftHorn = new ModelRenderer(this, 0, 9).setTextureSize(128, 128);
        this.leftHorn.mirror = true;
        this.leftHorn.addBox(-8.0F, -16.0F, 0.0F, 8, 7, 0);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.rightWing.render(scale);
        this.leftWing.render(scale);
        
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.8, 0.8, 0.8);
        this.rightHorn.render(scale);
        this.leftHorn.render(scale);
        GlStateManager.popMatrix();        
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        EntityDevil entityvex = (EntityDevil)entityIn;

        /*if (entityvex.isCharging())
        {
            if (entityvex.getPrimaryHand() == EnumHandSide.RIGHT)
            {
                this.bipedRightArm.rotateAngleX = 3.7699115F;
            }
            else
            {
                this.bipedLeftArm.rotateAngleX = 3.7699115F;
            }
        }*/

        this.bipedRightLeg.rotateAngleX += ((float)Math.PI / 5F);
        this.rightWing.rotationPointZ = 2.0F;
        this.leftWing.rotationPointZ = 2.0F;
        this.rightWing.rotationPointY = 1.0F;
        this.leftWing.rotationPointY = 1.0F;
        this.rightWing.rotateAngleY = 0.47123894F + MathHelper.cos(ageInTicks * 0.8F) * (float)Math.PI * 0.05F;
        this.leftWing.rotateAngleY = -this.rightWing.rotateAngleY;
        this.leftWing.rotateAngleZ = -0.47123894F;
        this.leftWing.rotateAngleX = 0.47123894F;
        this.rightWing.rotateAngleX = 0.47123894F;
        this.rightWing.rotateAngleZ = 0.47123894F;
        
        copyModelAngles(this.bipedHead, this.rightHorn);
        copyModelAngles(this.bipedHead, this.leftHorn);
    }

    public int getModelVersion()
    {
        return 23;
    }
}