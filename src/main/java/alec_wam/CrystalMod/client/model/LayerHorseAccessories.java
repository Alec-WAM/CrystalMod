package alec_wam.CrystalMod.client.model;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.entities.accessories.HorseAccessories;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerHorseAccessories implements LayerRenderer<AbstractHorse> {
	
	private ModelHorseChest chestModel = new ModelHorseChest();
	private ModelHorseShoes horseShoesModel = new ModelHorseShoes(0.1F);
    private static final ResourceLocation enderChestTexture = new ResourceLocation("crystalmod:textures/entities/accessories/donkey_enderchest.png");
    private static final ResourceLocation horseShoesTexture = new ResourceLocation("crystalmod:textures/entities/accessories/horse_horseshoes.png");
	
	public LayerHorseAccessories() {
		
	}

	@Override
	public void doRenderLayer(AbstractHorse entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		boolean invis = entity.isInvisible() || entity.isInvisibleToPlayer(CrystalMod.proxy.getClientPlayer());
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
		ItemStack horseShoes = HorseAccessories.getHorseShoes(entity);
		if(ItemStackTools.isValid(horseShoes)){
			GlStateManager.pushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(horseShoesTexture);
			horseShoesModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
			horseShoesModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			if(horseShoes.hasEffect())LayerArmorBase.renderEnchantedGlint((RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity), entity, horseShoesModel, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}

