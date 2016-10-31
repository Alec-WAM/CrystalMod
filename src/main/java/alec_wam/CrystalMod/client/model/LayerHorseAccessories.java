package alec_wam.CrystalMod.client.model;

import alec_wam.CrystalMod.entities.accessories.HorseAccessories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerHorseAccessories implements LayerRenderer<EntityHorse> {
	
	private ModelHorseChest chestModel = new ModelHorseChest();
    private static final ResourceLocation enderChestTexture = new ResourceLocation("crystalmod:textures/entities/accessories/donkey_enderchest.png");
	
	public LayerHorseAccessories() {
		
	}

	@Override
	public void doRenderLayer(EntityHorse entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		boolean invis = entity.isInvisible() || entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
		if(invis)return;
		if(HorseAccessories.hasEnderChest(entity)){
			GlStateManager.pushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(enderChestTexture);
			float f5 = 0.0F;
            float f6 = 0.0F;

            if (!entity.isRiding())
            {
                f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
                f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

                if (entity.isChild())
                {
                    f6 *= 3.0F;
                }

                if (f5 > 1.0F)
                {
                    f5 = 1.0F;
                }
            }
			chestModel.setLivingAnimations(entity, f6, f5, partialTicks);
			chestModel.rightChest.render(0.0625F);
			chestModel.leftChest.render(0.0625F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}

