package alec_wam.CrystalMod.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHorseChest extends ModelBase
{
    /** The left chest box on the mule model. */
    public final ModelRenderer leftChest;
    /** The right chest box on the mule model. */
    public final ModelRenderer rightChest;

    public ModelHorseChest()
    {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.leftChest = new ModelRenderer(this, 0, 34);
        this.leftChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
        this.leftChest.setRotationPoint(-7.5F, 3.0F, 10.0F);
        this.setBoxRotation(this.leftChest, 0.0F, ((float)Math.PI / 2F), 0.0F);
        this.rightChest = new ModelRenderer(this, 0, 47);
        this.rightChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
        this.rightChest.setRotationPoint(4.5F, 3.0F, 10.0F);
        this.setBoxRotation(this.rightChest, 0.0F, ((float)Math.PI / 2F), 0.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        EntityHorse entityhorse = (EntityHorse)entityIn;
    }

    /**
     * Sets the rotations for a ModelRenderer in the ModelHorse class.
     */
    private void setBoxRotation(ModelRenderer renderer, float rotateAngleX, float rotateAngleY, float rotateAngleZ)
    {
        renderer.rotateAngleX = rotateAngleX;
        renderer.rotateAngleY = rotateAngleY;
        renderer.rotateAngleZ = rotateAngleZ;
    }

    /**
     * Fixes and offsets a rotation in the ModelHorse class.
     */
    private float updateHorseRotation(float p_110683_1_, float p_110683_2_, float p_110683_3_)
    {
        float f;

        for (f = p_110683_2_ - p_110683_1_; f < -180.0F; f += 360.0F)
        {
            ;
        }

        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return p_110683_1_ + p_110683_3_ * f;
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime)
    {
        super.setLivingAnimations(entitylivingbaseIn, p_78086_2_, p_78086_3_, partialTickTime);
        float f = this.updateHorseRotation(entitylivingbaseIn.prevRenderYawOffset, entitylivingbaseIn.renderYawOffset, partialTickTime);
        float f1 = this.updateHorseRotation(entitylivingbaseIn.prevRotationYawHead, entitylivingbaseIn.rotationYawHead, partialTickTime);
        float f3 = f1 - f;

        if (f3 > 20.0F)
        {
            f3 = 20.0F;
        }

        if (f3 < -20.0F)
        {
            f3 = -20.0F;
        }

        EntityHorse entityhorse = (EntityHorse)entitylivingbaseIn;
        float f6 = entityhorse.getRearingAmount(partialTickTime);
        float f7 = 1.0F - f6;
        float f10 = MathHelper.cos(p_78086_2_ * 0.6662F + (float)Math.PI);
        float f11 = f10 * 0.8F * p_78086_3_;
        this.rightChest.rotationPointY = 3.0F;
        this.rightChest.rotationPointZ = 10.0F;
        this.rightChest.rotationPointY = f6 * 5.5F + f7 * this.rightChest.rotationPointY;
        this.rightChest.rotationPointZ = f6 * 15.0F + f7 * this.rightChest.rotationPointZ;
        this.leftChest.rotateAngleX = f11 / 5.0F;
        this.rightChest.rotateAngleX = -f11 / 5.0F;
        this.leftChest.rotationPointY = this.rightChest.rotationPointY;
        this.leftChest.rotationPointZ = this.rightChest.rotationPointZ;
    }
}