package alec_wam.CrystalMod.capability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.ModelBackpack;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.client.RenderUtil;

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
    	if(!ItemStackTools.isNullStack(backpack)){
    		if(backpack.getItem() instanceof ItemBackpackBase){
    			IBackpack type = ((ItemBackpackBase)backpack.getItem()).getBackpack();
    			ResourceLocation res = type.getTexture(backpack, 1);
    			if(res == null){
    				res = TextureMap.LOCATION_MISSING_TEXTURE;
    			}
    			GlStateManager.pushMatrix();
    			playerRenderer.bindTexture(res);
    			this.backpackModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
                this.backpackModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                boolean enchanted = false;
                if (enchanted) {
                    LayerArmorBase.renderEnchantedGlint(this.playerRenderer, entity, this.backpackModel, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
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

