package alec_wam.CrystalMod.tiles.jar;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.model.TRSRTransformation;

public class RenderTileJar<T extends TileJar> extends TileEntitySpecialRenderer<T> implements ICustomItemRenderer {

	public static EntityShulkerBullet bullet;
	public static IBakedModel bakedJar = null;
	
	@Override
	public void renderTileEntityAt(TileJar tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		int pass = MinecraftForgeClient.getRenderPass();
		if(tile == null)return;
		renderInternalJar(x, y, z, tile.getPotion(), tile.getPotionCount(), tile.isShulkerLamp(), tile.getLabelMap(), pass);
	}
	
	public static void renderInternalJar(double x, double y, double z, PotionType potion, int potionCount, boolean shulker, EnumMap<EnumFacing, Boolean> label, int pass){
		if(shulker){
			if(pass == 0)return;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.25, z + 0.5);
			if(bullet == null)bullet = new EntityShulkerBullet(CrystalMod.proxy.getClientWorld());
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(bullet, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.popMatrix();
		}
		else {
			if(potion !=PotionTypes.EMPTY && potionCount > 0){
				Vector3f color = getColorFromPotion(potion);
				if(color !=null && pass == 1){
					Tessellator tessy = Tessellator.getInstance();
					VertexBuffer render = tessy.getBuffer();
					GlStateManager.pushMatrix();
	
					GlStateManager.translate(x, y, z);
					GlStateManager.enableCull();
					GlStateManager.enableBlend();
					GlStateManager.disableLighting();
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					
					GlStateManager.disableTexture2D();
					float red = color.x / 255f, green = color.y / 255f, blue = color.z / 255f;
					float alpha = 0.65f;
					GlStateManager.color(red, green, blue, alpha);
					render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					float height = (0.8f/3.0f)*potionCount;
					
					render.pos(0.2, height, 0.2).tex(0, 0)/*.lightmap(MAX_LIGHT_X, MAX_LIGHT_Y)*/.endVertex();
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
	
					GlStateManager.enableTexture2D();
					GlStateManager.popMatrix();
				}
				
				PotionEffect effect = potion.getEffects().isEmpty() ? null : potion.getEffects().get(0);
				if(effect !=null && pass == 0){
					for(EnumFacing face : EnumFacing.HORIZONTALS){
						if(label.getOrDefault(face, false)){
							renderPotionLabel(effect, x, y, z, face);
						}
					}
				}
			} else {
				if(pass == 0){
					for(EnumFacing face : EnumFacing.HORIZONTALS){
						if(label.getOrDefault(face, false)){
							renderPotionLabel(null, x, y, z, face);
						}
					}
				}
			}
		}
	}
	
	public static IBakedModel getBakedJar(WoodType type){
		return Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(ModBlocks.jar.getDefaultState().withProperty(WoodenBlockProperies.WOOD, type));
	}
	
	public static Vector3f getColorFromPotion(PotionType type){
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

			for (PotionEffect potioneffect : type.getEffects())
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
	
	public static void renderPotionLabel(PotionEffect effect, double x, double y, double z, EnumFacing side){
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		if(side == EnumFacing.NORTH){
			GlStateManager.translate(1, 0, 1);
			GlStateManager.rotate(180, 0, 1, 0);
		}
		if(side == EnumFacing.EAST){
			GlStateManager.translate(0, 0, 1);
			GlStateManager.rotate(90, 0, 1, 0);
		}
		if(side == EnumFacing.WEST){
			GlStateManager.translate(1, 0, 0);
			GlStateManager.rotate(270, 0, 1, 0);
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		double potionZ = 0.819;
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Tessellator tessy = Tessellator.getInstance();
		VertexBuffer render = tessy.getBuffer();
		render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		TextureAtlasSprite itemframe = RenderUtil.getSprite("minecraft:blocks/itemframe_background");
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
			Potion potion = effect.getPotion();
			
	        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
	        boolean useIcons = true;
			if (useIcons && potion.hasStatusIcon())
	        {
	            int i1 = potion.getStatusIconIndex();
	            double textureX = 0 + i1 % 8 * 18;
				double textureY = 198 + i1 / 8 * 18;
				Minecraft.getMinecraft().getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
				render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				render.pos(0.32, 0.22, potionZ+0.001).tex((float)(textureX + 0) * 0.00390625F, (float)(textureY + 18) * 0.00390625F).endVertex();
				render.pos(0.68, 0.22, potionZ+0.001).tex((float)(textureX + 18) * 0.00390625F, (float)(textureY + 18) * 0.00390625F).endVertex();
				render.pos(0.68, 0.58, potionZ+0.001).tex((float)(textureX + 18) * 0.00390625F, (float)(textureY + 0) * 0.00390625F).endVertex();
				render.pos(0.32, 0.58, potionZ+0.001).tex((float)(textureX + 0) * 0.00390625F, (float)(textureY + 0) * 0.00390625F).endVertex();
				tessy.draw();
				
				String numeral = "";
				if (effect.getAmplifier() == 1)
	            {
					numeral = I18n.format("enchantment.level.2", new Object[0]);
	            }
	            else if (effect.getAmplifier() == 2)
	            {
	            	numeral = I18n.format("enchantment.level.3", new Object[0]);
	            }
	            else if (effect.getAmplifier() == 3)
	            {
	            	numeral = I18n.format("enchantment.level.4", new Object[0]);
	            }
				if(!numeral.isEmpty()){
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.6, 0.3, potionZ+0.002);
					GlStateManager.rotate(180, 1, 0, 0);
					double scale = 1.0/90;
	                GlStateManager.scale(scale, scale, 1);
	                Minecraft.getMinecraft().fontRendererObj.drawString(numeral, 0, 0, 0xFFF700);
	                GlStateManager.popMatrix();
				}
	        } else {
	        	GlStateManager.pushMatrix();
	        	GlStateManager.translate(0.3+0.2, 0.6, potionZ+0.001);
	        	GlStateManager.rotate(180, 1, 0, 0);
	        	String s1 = I18n.format(potion.getName(), new Object[0]);
	        	int startY = 5;
	        	List<String> lines = Lists.newArrayList();
	        	List<String> nameList = Arrays.<String>asList(s1.split(" "));
	        	lines.addAll(nameList);
	        	if(!potion.isInstant()){
	        		lines.add(Potion.getPotionDurationString(effect, 1.0F));
	        	} 
	        	double scale = 1.0/90;
	            if(lines.size() > 3){
	            	scale = 1.0/100;
	            	startY = 3;
	            } else {
	            	startY+=(3-lines.size())*5;
	            }
	        	GlStateManager.scale(scale, scale, 1);
	        	
	        	int index = 0;
	        	for(String line : lines){
	        		GlStateManager.pushMatrix();
	        		int stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(line);
	                scale = Math.min(30F / (stringWidth+10), 0.8F);
	                double renderY = startY+(index*(Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT*(1.0-scale)));
	                GlStateManager.translate(0, renderY, 0);
	                GlStateManager.scale(scale, scale, 1);
	                GlStateManager.translate(-stringWidth/2, 0, 0);
	                Minecraft.getMinecraft().fontRendererObj.drawString(line, 0, index*Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, 0);
	                GlStateManager.popMatrix();
	                index++;
	        	}
	        	
	        	//this.getFontRenderer().drawSplitString(s1, 3, 3, 40, 0);
	        	GlStateManager.popMatrix();
	        	
	        	String numeral = "";
				if (effect.getAmplifier() == 1)
	            {
					numeral = I18n.format("enchantment.level.2", new Object[0]);
	            }
	            else if (effect.getAmplifier() == 2)
	            {
	            	numeral = I18n.format("enchantment.level.3", new Object[0]);
	            }
	            else if (effect.getAmplifier() == 3)
	            {
	            	numeral = I18n.format("enchantment.level.4", new Object[0]);
	            }
				if(!numeral.isEmpty()){
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.6, 0.3, potionZ+0.002);
					GlStateManager.rotate(180, 1, 0, 0);
					scale = 1.0/90;
	                GlStateManager.scale(scale, scale, 1);
	                Minecraft.getMinecraft().fontRendererObj.drawString(numeral, 0, 0, 0xFFF700);
	                GlStateManager.popMatrix();
				}
	        }
	
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.6, 0.3, potionZ+0.001);
			double scale = 1.0/90;
	        GlStateManager.scale(scale, scale, 1);
	        potion.renderInventoryEffect(0, 0, effect, Minecraft.getMinecraft());
	        GlStateManager.popMatrix();
		}
        
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack) {
		PotionType type = PotionTypes.EMPTY;
		int count = 0;
		boolean lamp = false;
		EnumMap<EnumFacing, Boolean> labels = Maps.newEnumMap(EnumFacing.class);
		
		if(ItemNBTHelper.verifyExistance(stack, BlockJar.TILE_NBT_STACK)){
			NBTTagCompound tileNBT = ItemNBTHelper.getCompound(stack).getCompoundTag(BlockJar.TILE_NBT_STACK);
			type = PotionUtils.getPotionTypeFromNBT(tileNBT);
			count = tileNBT.getInteger("Count");
			lamp = tileNBT.getBoolean("IsShulker");
			for(EnumFacing facing : EnumFacing.HORIZONTALS){
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
			GlStateManager.translate(0.9, -0.5, 0.9);
			GlStateManager.scale(1.8, 1.8, 1.8);
			GlStateManager.rotate(180, 0, 1, 0);
		}
		if(lastTransform == TransformType.FIXED){
			GlStateManager.translate(0.75, -0.7, 0.8);
			GlStateManager.scale(1.5, 1.5, 1.5);
			GlStateManager.rotate(180, 0, 1, 0);
		}
		
		List<BakedQuad> listQuads = getBakedJar(WoodType.byMetadata(stack.getItemDamage())).getQuads(null, (EnumFacing)null, 0L);
        
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
	}
}