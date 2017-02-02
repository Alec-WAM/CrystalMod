package alec_wam.CrystalMod.client.model;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.entities.accessories.ModelWolfArmor;
import alec_wam.CrystalMod.entities.accessories.WolfAccessories;
import alec_wam.CrystalMod.entities.accessories.WolfAccessories.WolfArmor;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerWolfAccessories implements LayerRenderer<EntityWolf> {
	
	private static Map<WolfArmor, ResourceLocation> TEXTURE_MAP = Maps.newHashMap();
	private static final ResourceLocation DEFAULT_TEXTURE_0 = CrystalMod.resourceL("textures/entities/accessories/armor/default_0.png");
	private static final ResourceLocation DEFAULT_TEXTURE_1 = CrystalMod.resourceL("textures/entities/accessories/armor/default_1.png");
	ModelWolfArmor modelWolfArmor0 = new ModelWolfArmor(0.2F);
    ModelWolfArmor modelWolfArmor1 = new ModelWolfArmor(0.1F);

    protected RenderLivingBase<?> renderer;
    
	public LayerWolfAccessories(@Nonnull RenderLivingBase<?> renderer) {
		this.renderer = renderer;
	}

	@Override
	public void doRenderLayer(EntityWolf entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		boolean invis = entity.isInvisible() || entity.isInvisibleToPlayer(CrystalMod.proxy.getClientPlayer());
		if(invis)return;
		if(ItemStackTools.isValid(WolfAccessories.getWolfArmorStack(entity))){
			ItemStack armorStack = WolfAccessories.getWolfArmorStack(entity);
			GlStateManager.pushMatrix();
			for(int i = 0; i < 2; i++){
				ModelWolfArmor model = null;
				if(i == 0)model = modelWolfArmor0;
				if(i == 1)model = modelWolfArmor1;
				if(model !=null){
					model.setModelAttributes(renderer.getMainModel());
					model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
					
					//TODO Add leather armor dying support
					renderer.bindTexture(getTexture(WolfAccessories.getWolfArmor(armorStack), i));
					GlStateManager.color(1, 1, 1, 1);
					model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
				}
			}
			GlStateManager.popMatrix();
		}
	}

	private ResourceLocation getTexture(WolfArmor wolfArmor, int layer) {
		if(wolfArmor != WolfArmor.NONE){
			TEXTURE_MAP.put(wolfArmor, CrystalMod.resourceL("textures/entities/accessories/armor/"+wolfArmor.name().toLowerCase()+"_"+layer+".png"));
		}
		return TEXTURE_MAP.getOrDefault(wolfArmor, layer == 0 ? DEFAULT_TEXTURE_0 : DEFAULT_TEXTURE_1);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}

