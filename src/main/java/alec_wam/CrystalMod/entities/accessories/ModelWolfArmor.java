package alec_wam.CrystalMod.entities.accessories;

import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWolf;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWolfArmor extends ModelWolf {
	
    public ModelWolfArmor(float scale) {
        this.wolfHeadMain = new ModelRenderer(this, 0, 0);
        this.wolfHeadMain.addBox(-2, -3, -2, 6, 6, 4, scale);
        this.wolfHeadMain.setRotationPoint(-1, 13.5F, -7);
        this.wolfBody = new ModelRenderer(this, 18, 14);
        this.wolfBody.addBox(-3, -2, -3, 6, 9, 6, scale);
        this.wolfBody.setRotationPoint(0, 14, 2);
        this.wolfLeg1 = new ModelRenderer(this, 0, 18);
        this.wolfLeg1.addBox(0, 0, -1, 2, 8, 2, scale);
        this.wolfLeg1.setRotationPoint(-2.5F, 16, 7);
        this.wolfLeg2 = new ModelRenderer(this, 0, 18);
        this.wolfLeg2.addBox(0, 0, -1, 2, 8, 2, scale);
        this.wolfLeg2.setRotationPoint(0.5F, 16, 7);
        this.wolfLeg3 = new ModelRenderer(this, 0, 18);
        this.wolfLeg3.addBox(0, 0, -1, 2, 8, 2, scale);
        this.wolfLeg3.setRotationPoint(-2.5F, 16, -4);
        this.wolfLeg4 = new ModelRenderer(this, 0, 18);
        this.wolfLeg4.addBox(0, 0, -1, 2, 8, 2, scale);
        this.wolfLeg4.setRotationPoint(0.5F, 16, -4);
        this.wolfHeadMain.setTextureOffset(16, 14).addBox(-2, -5, 0, 2, 2, 1, scale);
        this.wolfHeadMain.setTextureOffset(16, 14).addBox(2, -5, 0, 2, 2, 1, scale);
        this.wolfHeadMain.setTextureOffset(0, 10).addBox(-0.5F, 0, -5, 3, 3, 4, scale);

        ModelRenderer wolfMane = new ModelRenderer(this, 21, 0);
        wolfMane.addBox(-3, -3, -3, 8, 6, 7, scale);
        wolfMane.setRotationPoint(-1, 14, 2);
        ModelRenderer wolfTail = new ModelRenderer(this, 9, 18);
        wolfTail.addBox(0, 0, -1, 2, 8, 2, scale);
        wolfTail.setRotationPoint(-1, 12, 8);

        try {
        	ObfuscationReflectionHelper.setPrivateValue(ModelWolf.class, this, wolfTail, 6);
        	ObfuscationReflectionHelper.setPrivateValue(ModelWolf.class, this, wolfMane, 7);
        } catch(Exception e){
        	ModLogger.error("Error while trying to access the needed fields in the ModelWolf class");
        	e.printStackTrace();
        }
    }
}
