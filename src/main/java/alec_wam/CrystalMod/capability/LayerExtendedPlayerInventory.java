package alec_wam.CrystalMod.capability;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackInventory;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.ModelBackpack;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerExtendedPlayerInventory implements LayerRenderer<AbstractClientPlayer> {
	
	public LayerExtendedPlayerInventory(RenderPlayer playerRenderer) {
		this.playerRenderer = playerRenderer;
	}
	private final RenderPlayer playerRenderer;
	private final ModelBackpack backpackModel = new ModelBackpack(1.0F);
	
	@Override
	public void doRenderLayer(AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if(entity.isInvisible())return;
    	ItemStack backpack = BackpackUtil.getBackpackOnBack(entity);
    	if(ItemStackTools.isValid(backpack)){
    		IBackpack type = BackpackUtil.getType(backpack);
    		if(type !=null){
    			ResourceLocation res = type.getTexture(backpack, 1);
    			if(res == null){
    				res = TextureMap.LOCATION_MISSING_TEXTURE;
    			}
    			GlStateManager.pushMatrix();
    			playerRenderer.bindTexture(res);
    			this.backpackModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
    			GlStateManager.pushMatrix();
    			GlStateManager.scale(0.7, 0.7, 0.7);
    			GlStateManager.translate(0, 0.05, 0);
                this.backpackModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                boolean enchanted = false;
                if (enchanted) {
                    LayerArmorBase.renderEnchantedGlint(this.playerRenderer, entity, this.backpackModel, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                }
                GlStateManager.popMatrix();
    			if(type instanceof IBackpackInventory){
    				IBackpackInventory inv = (IBackpackInventory)type;
    				InventoryBackpack inventory = inv.getInventory(backpack);
    				InventoryBackpackUpgrades upgradeInv = type.getUpgradeInventory(backpack);
    				if(inventory !=null && upgradeInv !=null){
    					if(upgradeInv.hasUpgrade(BackpackUpgrade.POCKETS)){
    						if(inventory instanceof NormalInventoryBackpack){
    							NormalInventoryBackpack normalInv = (NormalInventoryBackpack)inventory;
    							ItemStack weapon = normalInv.getToolStack(0);
    							ItemStack tool = normalInv.getToolStack(1);
    							if(ItemStackTools.isValid(weapon)){
    								GlStateManager.pushMatrix();
    								GlStateManager.translate(0.3, 0.3, 0.3);
    								GlStateManager.rotate(90, 0, 1, 0);
    								GlStateManager.rotate(-45, 0, 0, 1);
    								GlStateManager.scale(0.5, 0.5, 0.5);
    								RenderUtil.renderItem(weapon, TransformType.FIXED);
    								GlStateManager.popMatrix();
    							}
    							if(ItemStackTools.isValid(tool)){
    								GlStateManager.pushMatrix();
    								GlStateManager.translate(-0.3, 0.3, 0.3);
    								GlStateManager.rotate(90, 0, 1, 0);
    								GlStateManager.rotate(135, 0, 0, 1);
    								GlStateManager.scale(0.5, 0.5, 0.5);
    								RenderUtil.renderItem(tool, TransformType.FIXED);
    								GlStateManager.popMatrix();
    							}
    						}
    					}
    				}
    			}
    			GlStateManager.popMatrix();
    		}
    	}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}

