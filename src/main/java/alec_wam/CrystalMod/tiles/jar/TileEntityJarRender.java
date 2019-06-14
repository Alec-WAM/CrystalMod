package alec_wam.CrystalMod.tiles.jar;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;

public class TileEntityJarRender extends TileEntityRenderer<TileEntityJar> {

	public static ShulkerBulletEntity bullet;
	public static IBakedModel bakedJar = null;
	
	@Override
	public void render(TileEntityJar tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		int pass = 0;//MinecraftForgeClient.getRenderPass();
		if(tile == null)return;
		//System.out.println("" + MinecraftForgeClient.getRenderLayer());
		if(MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT){
			pass = 1;
		}
		renderInternalJar(x, y, z, tile.getPotion(), tile.getPotionCount(), tile.isShulkerLamp(), tile.getLabelMap(), pass);
	}
	
	public static void renderInternalJar(double x, double y, double z, Potion potion, int potionCount, boolean shulker, EnumMap<Direction, Boolean> label, int pass){
		if(shulker){
			//if(pass == 0)return;
			GlStateManager.pushMatrix();
			GlStateManager.translated(x + 0.5, y + 0.25, z + 0.5);
			if(bullet == null)bullet = new ShulkerBulletEntity(EntityType.SHULKER_BULLET, Minecraft.getInstance().world);
			Minecraft.getInstance().getRenderManager().renderEntity(bullet, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.popMatrix();
		}
		else {
			if(potion != Potions.field_185229_a && potionCount > 0){
				Vector3f color = getColorFromPotion(potion);
				if(color !=null){
					Tessellator tessy = Tessellator.getInstance();
					BufferBuilder render = tessy.getBuffer();
					GlStateManager.pushMatrix();
	
					GlStateManager.translated(x, y, z);
					GlStateManager.disableTexture();
					GlStateManager.enableBlend();
					GlStateManager.disableLighting();
					GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					float red = color.x / 255f, green = color.y / 255f, blue = color.z / 255f;
					float alpha = 0.65f;
					GlStateManager.color4f(red, green, blue, alpha);
					render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					float height = (0.8f/(float)BlockJar.MAX_POTIONS_STORED)*potionCount;

					render.pos(0.2, height, 0.2).tex(0, 0).endVertex();
					render.pos(0.2, height, 0.8).tex(0, 0).endVertex();
					render.pos(0.8, height, 0.8).tex(0, 0).endVertex();
					render.pos(0.8, height, 0.2).tex(0, 0).endVertex();

					render.pos(0.8, 0.01, 0.2).tex(0, 0).endVertex();
					render.pos(0.8, 0.01, 0.8).tex(0, 0).endVertex();
					render.pos(0.2, 0.01, 0.8).tex(0, 0).endVertex();
					render.pos(0.2, 0.01, 0.2).tex(0, 0).endVertex();

					render.pos(0.2, 0.01, 0.8).tex(0, 0).endVertex();
					render.pos(0.8, 0.01, 0.8).tex(0, 0).endVertex();
					render.pos(0.8, height, 0.8).tex(0, 0).endVertex();
					render.pos(0.2, height, 0.8).tex(0, 0).endVertex();

					render.pos(0.2, 0.01, 0.2).tex(0, 0).endVertex();
					render.pos(0.2, height, 0.2).tex(0, 0).endVertex();
					render.pos(0.8, height, 0.2).tex(0, 0).endVertex();
					render.pos(0.8, 0.01, 0.2).tex(0, 0).endVertex();

					render.pos(0.8, 0.01, 0.2).tex(0, 0).endVertex();
					render.pos(0.8, height, 0.2).tex(0, 0).endVertex();
					render.pos(0.8, height, 0.8).tex(0, 0).endVertex();
					render.pos(0.8, 0.01, 0.8).tex(0, 0).endVertex();

					render.pos(0.2, 0.01, 0.2).tex(0, 0).endVertex();
					render.pos(0.2, 0.01, 0.8).tex(0, 0).endVertex();
					render.pos(0.2, height, 0.8).tex(0, 0).endVertex();
					render.pos(0.2, height, 0.2).tex(0, 0).endVertex();
					tessy.draw();
					GlStateManager.enableLighting();
					GlStateManager.enableTexture();
					GlStateManager.popMatrix();
				}
				
				EffectInstance effect = potion.getEffects().isEmpty() ? null : potion.getEffects().get(0);
				if(effect !=null && pass == 0){
					for(Direction face : Direction.Plane.HORIZONTAL){
						if(label.getOrDefault(face, false)){
							renderPotionLabel(effect, x, y, z, face);
						}
					}
				}
			} else {
				if(pass == 0){
					for(Direction face : Direction.Plane.HORIZONTAL){
						if(label.getOrDefault(face, false)){
							renderPotionLabel(null, x, y, z, face);
						}
					}
				}
			}
		}
	}
	
	public static Vector3f getColorFromPotion(Potion type){
		if (type.getEffects().isEmpty())
		{
			return null;
		}
		else
		{
			float f = 0.0F;
			float f1 = 0.0F;
			float f2 = 0.0F;
			int j = 0;

			for (EffectInstance potioneffect : type.getEffects())
			{
				if (potioneffect.doesShowParticles())
				{
					int k = potioneffect.getPotion().getLiquidColor();
					int l = potioneffect.getAmplifier() + 1;
					f += l * (k >> 16 & 255) / 255.0F;
					f1 += l * (k >> 8 & 255) / 255.0F;
					f2 += l * (k >> 0 & 255) / 255.0F;
					j += l;
				}
			}

			if (j == 0)
			{
				return new Vector3f(0, 0, 0);
			}
			else
			{
				f = f / j * 255.0F;
				f1 = f1 / j * 255.0F;
				f2 = f2 / j * 255.0F;
				return new Vector3f(f, f1, f2);
			}
		}
	}
	
	public static void renderPotionLabel(EffectInstance effect, double x, double y, double z, Direction side){
		GlStateManager.pushMatrix();
		GlStateManager.translated(x, y, z);
		if(side == Direction.NORTH){
			GlStateManager.translated(1, 0, 1);
			GlStateManager.rotatef(180, 0, 1, 0);
		}
		if(side == Direction.EAST){
			GlStateManager.translated(0, 0, 1);
			GlStateManager.rotatef(90, 0, 1, 0);
		}
		if(side == Direction.WEST){
			GlStateManager.translated(1, 0, 0);
			GlStateManager.rotatef(270, 0, 1, 0);
		}
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		double potionZ = 0.819;
		Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		Tessellator tessy = Tessellator.getInstance();
		BufferBuilder render = tessy.getBuffer();
		render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		TextureAtlasSprite itemframe = RenderUtil.getSprite("minecraft:block/item_frame");
		render.pos(0.3, 0.2, potionZ).tex(itemframe.getMinU(), itemframe.getMaxV()).endVertex();
		render.pos(0.7, 0.2, potionZ).tex(itemframe.getMaxU(), itemframe.getMaxV()).endVertex();
		render.pos(0.7, 0.6, potionZ).tex(itemframe.getMaxU(), itemframe.getMinV()).endVertex();
		render.pos(0.3, 0.6, potionZ).tex(itemframe.getMinU(), itemframe.getMinV()).endVertex();

		render.pos(0.7, 0.2, potionZ).tex(itemframe.getMaxU(), itemframe.getMaxV()).endVertex();
		render.pos(0.3, 0.2, potionZ).tex(itemframe.getMinU(), itemframe.getMaxV()).endVertex();
		render.pos(0.3, 0.6, potionZ).tex(itemframe.getMinU(), itemframe.getMinV()).endVertex();
		render.pos(0.7, 0.6, potionZ).tex(itemframe.getMaxU(), itemframe.getMinV()).endVertex();
		tessy.draw();
		
		if(effect !=null){
			Effect potion = effect.getPotion();
			
	        if (effect.isShowIcon())
	        {
	            PotionSpriteUploader potionspriteuploader = Minecraft.getInstance().func_213248_ap();
	        	TextureAtlasSprite textureatlassprite = potionspriteuploader.func_215288_a(potion);
	            double shrink = 0.05;
				double ofsetY = 0.045;
				Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.field_215264_i);
				render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				render.pos(0.32 + shrink, 0.22 + shrink + ofsetY, potionZ+0.001).tex(textureatlassprite.getMinU(), textureatlassprite.getMaxV()).endVertex();
				render.pos(0.68 - shrink, 0.22 + shrink + ofsetY, potionZ+0.001).tex(textureatlassprite.getMaxU(), textureatlassprite.getMaxV()).endVertex();
				render.pos(0.68 - shrink, 0.58 - shrink + ofsetY, potionZ+0.001).tex(textureatlassprite.getMaxU(), textureatlassprite.getMinV()).endVertex();
				render.pos(0.32 + shrink, 0.58 - shrink + ofsetY, potionZ+0.001).tex(textureatlassprite.getMinU(), textureatlassprite.getMinV()).endVertex();
				tessy.draw();
				
				String name = I18n.format(potion.getName(), new Object[0]);
				
				int amp = effect.getAmplifier();
				if(amp > 0){
					name += " " + Lang.translateToLocal("potion.potency."+amp);
				}
				
				int width = Minecraft.getInstance().fontRenderer.getStringWidth(name);
				GlStateManager.pushMatrix();
				GlStateManager.translated(0.5, 0.31, potionZ+0.002);
				GlStateManager.rotatef(180, 1, 0, 0);
				double scale = 1.0/250;
                GlStateManager.scaled(scale, scale, 1);
                GlStateManager.translated(-width/2, 0, 0);
                Minecraft.getInstance().fontRenderer.drawString(name, 0, 0, 0);
                GlStateManager.popMatrix();
				 
	        } else {
	        	GlStateManager.pushMatrix();
	        	GlStateManager.translated(0.3+0.2, 0.6, potionZ+0.001);
	        	GlStateManager.rotatef(180, 1, 0, 0);
	        	String s1 = I18n.format(potion.getName(), new Object[0]);
	        	int startY = 5;
	        	List<String> lines = Lists.newArrayList();
	        	List<String> nameList = Arrays.<String>asList(s1.split(" "));
	        	lines.addAll(nameList);
	        	double scale = 1.0/90;
	            if(lines.size() > 3){
	            	scale = 1.0/100;
	            	startY = 3;
	            } else {
	            	startY+=(3-lines.size())*5;
	            }
	        	GlStateManager.scaled(scale, scale, 1);
	        	
	        	int index = 0;
	        	for(String line : lines){
	        		GlStateManager.pushMatrix();
	        		int stringWidth = Minecraft.getInstance().fontRenderer.getStringWidth(line);
	                scale = Math.min(30F / (stringWidth+10), 0.8F);
	                double renderY = startY+(index*(Minecraft.getInstance().fontRenderer.FONT_HEIGHT*(1.0-scale)));
	                GlStateManager.translated(0, renderY, 0);
	                GlStateManager.scaled(scale, scale, 1);
	                GlStateManager.translated(-stringWidth/2, 0, 0);
	                Minecraft.getInstance().fontRenderer.drawString(line, 0, index*Minecraft.getInstance().fontRenderer.FONT_HEIGHT, 0);
	                GlStateManager.popMatrix();
	                index++;
	        	}
	        	GlStateManager.popMatrix();
	        	
	        	int amp = effect.getAmplifier();
				if(amp > 0){
					String numeral = Lang.translateToLocal("potion.potency."+amp);
					GlStateManager.pushMatrix();
					GlStateManager.translated(0.63, 0.27, potionZ+0.002);
					GlStateManager.rotatef(180, 1, 0, 0);
					scale = 1.0/180;
	                GlStateManager.scaled(scale, scale, 1);
	                Minecraft.getInstance().fontRenderer.drawString(numeral, 0, 0, 0xFFF700);
	                GlStateManager.popMatrix();
				}
	        }
			
			if(!potion.isInstant()){
        		String time = EffectUtils.getPotionDurationString(effect, 1.0F);
        		GlStateManager.pushMatrix();
				GlStateManager.translated(0.34, 0.27, potionZ+0.002);
				GlStateManager.rotatef(180, 1, 0, 0);
				double scale = 1.0/200;
                GlStateManager.scaled(scale, scale, 1);
                Minecraft.getInstance().fontRenderer.drawString(time, 0, 0, 0);
                GlStateManager.popMatrix();
        	}
			
			GlStateManager.pushMatrix();
			GlStateManager.translated(0.6, 0.3, potionZ+0.001);
			double scale = 1.0/90;
	        GlStateManager.scaled(scale, scale, 1);
	        //potion.renderInventoryEffect(0, 0, effect, Minecraft.getInstance());
	        GlStateManager.popMatrix();
		}
        
		GlStateManager.popMatrix();
	}

	/*@Override
	public void render(ItemStack stack) {
		PotionType type = PotionTypes.EMPTY;
		int count = 0;
		boolean lamp = false;
		EnumMap<Direction, Boolean> labels = Maps.newEnumMap(Direction.class);
		
		if(ItemNBTHelper.verifyExistance(stack, BlockJar.TILE_NBT_STACK)){
			CompoundNBT tileNBT = ItemNBTHelper.getCompound(stack).getCompoundTag(BlockJar.TILE_NBT_STACK);
			type = PotionUtils.getPotionTypeFromNBT(tileNBT);
			count = tileNBT.getInteger("Count");
			lamp = tileNBT.getBoolean("IsShulker");
			for(Direction facing : Direction.HORIZONTALS){
				labels.put(facing, tileNBT.getBoolean("Label."+facing.getName().toUpperCase()));
			}
		}
		
		GlStateManager.pushMatrix();
		
		if(lastTransform == TransformType.GUI){
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(-1, -1, 0);
		}
		if(lastTransform == TransformType.GROUND){
			GlStateManager.translate(-0.5, 0, -0.5);
		}
		if(lastTransform == TransformType.FIRST_PERSON_RIGHT_HAND){
			GlStateManager.translate(1.2, 0, -0.5);
			GlStateManager.rotate(270, 0, 1, 0);
		}
		if(lastTransform == TransformType.THIRD_PERSON_RIGHT_HAND){
			GlStateManager.rotate(45, 1, 0, 1);
			GlStateManager.rotate(90, 0, -1, 0);
			GlStateManager.translate(-0.5, -0.5, -0.5);
			GlStateManager.scale(1.2, 1.2, 1.2);
		}
		if(lastTransform == TransformType.FIRST_PERSON_LEFT_HAND){
			GlStateManager.translate(1.2, 0, -0.5);
			GlStateManager.rotate(270, 0, 1, 0);
		}
		if(lastTransform == TransformType.THIRD_PERSON_LEFT_HAND){
			GlStateManager.rotate(-45, -1, 0, 1);
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.translate(-0.5, -0.5, -0.5);
			GlStateManager.scale(1.2, 1.2, 1.2);
		}
		if(lastTransform == TransformType.HEAD){
			GlStateManager.translate(0.8, -0.5, 0.8);
			GlStateManager.scale(1.6, 1.6, 1.6);
			GlStateManager.rotate(180, 0, 1, 0);
		}
		if(lastTransform == TransformType.FIXED){
			GlStateManager.translate(0.75, -0.7, 0.8);
			GlStateManager.scale(1.5, 1.5, 1.5);
			GlStateManager.rotate(180, 0, 1, 0);
		}
		
		List<BakedQuad> listQuads = getBakedJar(WoodType.byMetadata(stack.getItemDamage())).getQuads(null, (Direction)null, 0L);
        
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        int i = 0;

        for (int j = listQuads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = listQuads.get(i);
            vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
            vertexbuffer.addVertexData(bakedquad.getVertexData());

            //vertexbuffer.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);

            Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            vertexbuffer.putNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            tessellator.draw();
        }

		//GlStateManager.scale(10, 10, 10);
		for(int pass = 0; pass < 2; pass++){
			renderInternalJar(0, 0, 0, type, count, lamp, labels, pass);
		}
		GlStateManager.popMatrix();
	}

	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}*/
}