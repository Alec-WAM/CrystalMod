package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemRenderBackpack implements ICustomItemRenderer {
	private final ModelBackpack backpackModel = new ModelBackpack(1.0F);
	
	@Override
	public void render(ItemStack stack) {
		GlStateManager.pushMatrix();
		IBackpack type = BackpackUtil.getType(stack);
		if(type !=null){
			ResourceLocation res = type.getTexture(stack, 0);
			if(res == null){
				res = TextureMap.LOCATION_MISSING_TEXTURE;
			}
			GlStateManager.pushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(res);
			GlStateManager.pushMatrix();
			GlStateManager.scale(1, 1, 1);
			GlStateManager.translate(0, 0.05, 0);
			if(lastTransform == TransformType.FIRST_PERSON_LEFT_HAND || lastTransform == TransformType.FIRST_PERSON_RIGHT_HAND){
				GlStateManager.translate(0.0, -0.8, 0.0);
				GlStateManager.rotate(-90, 0, 1, 0);
			}
			if(lastTransform == TransformType.THIRD_PERSON_LEFT_HAND){
				GlStateManager.translate(0, -0.4, 0);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-0.5, 0, -0.2);
			}
			if(lastTransform == TransformType.THIRD_PERSON_RIGHT_HAND){
				GlStateManager.translate(0, -0.4, 0);
				GlStateManager.rotate(-90, 0, 1, 0);
				GlStateManager.translate(0.5, 0, -0.2);
			}
			
			if(lastTransform == TransformType.GUI)GlStateManager.translate(0.5, -0.8, 0);
			if(lastTransform == TransformType.FIXED){
				GlStateManager.translate(0, -0.8, -0.3);
				GlStateManager.scale(1.7, 1.7, 1.7);
			}
			if(lastTransform == TransformType.GROUND){
				GlStateManager.translate(0, -0.8, -0.5);
				GlStateManager.scale(1.7, 1.7, 1.7);
			}
			
			this.backpackModel.Strap_Long.isHidden = this.backpackModel.Strap_Long2.isHidden = true;
            this.backpackModel.render(null, 0, 0, 0, 0, 0, 0.0625F);
            /*boolean enchanted = false;
            if (enchanted) {
                LayerArmorBase.renderEnchantedGlint(this.playerRenderer, entity, this.backpackModel, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }*/
            GlStateManager.popMatrix();
			if(type instanceof IBackpackInventory){
				IBackpackInventory inv = (IBackpackInventory)type;
				InventoryBackpack inventory = inv.getInventory(stack);
				InventoryBackpackUpgrades upgradeInv = type.getUpgradeInventory(stack);
				if(inventory !=null && upgradeInv !=null){
					if(upgradeInv.hasUpgrade(BackpackUpgrade.POCKETS)){
						if(inventory instanceof NormalInventoryBackpack){
							NormalInventoryBackpack normalInv = (NormalInventoryBackpack)inventory;
							ItemStack weapon = normalInv.getToolStack(0);
							ItemStack tool = normalInv.getToolStack(1);
							if(ItemStackTools.isValid(weapon)){
								GlStateManager.pushMatrix();
								GlStateManager.translate(0.40, 0.3, 0.3);
								GlStateManager.rotate(90, 0, 1, 0);
								GlStateManager.rotate(45, 0, 0, 1);
								RenderUtil.renderItem(weapon, TransformType.GROUND);
								GlStateManager.popMatrix();
							}
							if(ItemStackTools.isValid(tool)){
								GlStateManager.pushMatrix();
								GlStateManager.translate(-0.4, 0.5, 0.4);
								GlStateManager.rotate(90, 0, 1, 0);
								GlStateManager.rotate(225, 0, 0, 1);
								RenderUtil.renderItem(tool, TransformType.GROUND);
								GlStateManager.popMatrix();
							}
						}
					}
				}
			}
			GlStateManager.popMatrix();
		}
    	GlStateManager.popMatrix();
	}
	
	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}

}

