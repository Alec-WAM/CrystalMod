package alec_wam.CrystalMod.client.render;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.items.ItemMobSkull;
import alec_wam.CrystalMod.items.ItemMobSkull.EnumSkullType;
import alec_wam.CrystalMod.items.ItemMobSkull.HorseType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MobSkullRenderHelper {

	private static final GenericHeadModel HEAD_MODEL_BASIC = new GenericHeadModel(0, 0, 64, 32);
	private static final GuardianHeadModel HEAD_MODEL_GUARDIAN = new GuardianHeadModel();
	private static final HorseHeadModel HEAD_MODEL_HORSE = new HorseHeadModel(0.0f);
    
	private static final ResourceLocation ENDERMAN_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman.png");
	private static final ResourceLocation ENDERMAN_EYES_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
	private static final Map<HorseType, ResourceLocation> HORSE_TEXTURES = Maps.newHashMap();
	static {
		HORSE_TEXTURES.put(HorseType.WHITE, new ResourceLocation("textures/entity/horse/horse_white.png"));
		HORSE_TEXTURES.put(HorseType.CREAMY, new ResourceLocation("textures/entity/horse/horse_creamy.png"));
		HORSE_TEXTURES.put(HorseType.CHESTNUT, new ResourceLocation("textures/entity/horse/horse_chestnut.png"));
		HORSE_TEXTURES.put(HorseType.BROWN, new ResourceLocation("textures/entity/horse/horse_brown.png"));
		HORSE_TEXTURES.put(HorseType.BLACK, new ResourceLocation("textures/entity/horse/horse_black.png"));
		HORSE_TEXTURES.put(HorseType.GRAY, new ResourceLocation("textures/entity/horse/horse_gray.png"));
		HORSE_TEXTURES.put(HorseType.DARKBROWN, new ResourceLocation("textures/entity/horse/horse_darkbrown.png"));
		HORSE_TEXTURES.put(HorseType.ZOMBIE, new ResourceLocation("textures/entity/horse/horse_zombie.png"));
		HORSE_TEXTURES.put(HorseType.SKELETON, new ResourceLocation("textures/entity/horse/horse_skeleton.png"));
		HORSE_TEXTURES.put(HorseType.DONKEY, new ResourceLocation("textures/entity/horse/donkey.png"));
		HORSE_TEXTURES.put(HorseType.MULE, new ResourceLocation("textures/entity/horse/mule.png"));
	}
	
	public static void renderSkull(ItemStack stack) {
		if(stack.getItem() instanceof ItemMobSkull){
			ItemMobSkull skull = (ItemMobSkull)stack.getItem();
			EnumSkullType type = skull.type;
			float rotation = 180.0f;
			TextureManager texture = Minecraft.getInstance().getTextureManager();
			GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
			
            GlStateManager.enableRescaleNormal();
            GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
            GlStateManager.enableAlphaTest();
            if(type == EnumSkullType.ENDERMAN){
            	GlStateManager.translated(-0.5, 0.0, 0.5);
            	texture.bindTexture(ENDERMAN_TEXTURE);
            	HEAD_MODEL_BASIC.func_217104_a(0.0f, 0.0F, 0.0F, rotation, 0.0F, 0.0625F);
            	texture.bindTexture(ENDERMAN_EYES_TEXTURE);
            	HEAD_MODEL_BASIC.func_217104_a(0.0f, 0.0F, 0.0F, rotation, 0.0F, 0.0625F);
            }
            if(type == EnumSkullType.GUARDIAN){
            	GlStateManager.translated(-0.5, -0.9, 0.5);
            	texture.bindTexture(GUARDIAN_TEXTURE);
            	HEAD_MODEL_GUARDIAN.func_217104_a(0.0f, 0.0F, 0.0F, rotation, 0.0F, 0.0625F);
            }
            if(type == EnumSkullType.HORSE){
            	HorseType horse = ItemMobSkull.loadHorseType(stack);
            	GlStateManager.translated(-0.5, 0.1, 0.5);
            	texture.bindTexture(horse == null ? HORSE_TEXTURES.get(HorseType.CREAMY) : HORSE_TEXTURES.get(horse));
            	HEAD_MODEL_HORSE.func_217104_a(0.0f, 0.0F, 0.0F, rotation, 0.0F, 0.0625F);
            }
            GlStateManager.popMatrix();
			
			GlStateManager.enableCull();
            GlStateManager.popMatrix();
		}
	}

}
