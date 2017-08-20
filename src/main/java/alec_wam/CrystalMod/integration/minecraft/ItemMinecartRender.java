package alec_wam.CrystalMod.integration.minecraft;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemMinecartRender implements ICustomItemRenderer {

	private Map<String, EntityMinecart> minecarts = Maps.newHashMap();
	
	public EntityMinecart getMinecart(String id, Class<? extends EntityMinecart> clazz) {
		if(!minecarts.containsKey(id)){
			try {
				minecarts.put(id, clazz.getConstructor(World.class).newInstance(CrystalMod.proxy.getClientWorld()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return minecarts.get(id);
	}
	
	@Override
	public void render(ItemStack stack) {
		String id = "empty";
		Class<? extends EntityMinecart> clazz = EntityMinecartEmpty.class;
		
		if(stack.getItem() == Items.CHEST_MINECART){
			id = "chest";
			clazz = EntityMinecartChest.class;
		}
		
		if(stack.getItem() == Items.FURNACE_MINECART){
			id = "furnace";
			clazz = EntityMinecartFurnace.class;
		}
		
		if(stack.getItem() == Items.TNT_MINECART){
			id = "tnt";
			clazz = EntityMinecartTNT.class;
		}
		
		if(stack.getItem() == Items.HOPPER_MINECART){
			id = "hopper";
			clazz = EntityMinecartHopper.class;
		}
		
		if(stack.getItem() == Items.COMMAND_BLOCK_MINECART){
			id = "commandblock";
			clazz = EntityMinecartCommandBlock.class;
		}
		
		EntityMinecart minecart = getMinecart(id, clazz);
		if(minecart == null){
			return;
		}
		renderMinecart(minecart, lastTransform, true);
	}
	
	public static void renderMinecart(EntityMinecart minecart, TransformType type, boolean isVanilla){

		boolean atrib = false;
		GlStateManager.pushMatrix();
		if(atrib)GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		if (type == TransformType.GUI)
		{
			GlStateManager.pushMatrix();
			GlStateManager.color(1, 1, 1, 1);
			float scale = 1.8f;
			//Vec3d offset = essence.getRenderOffset();
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.5, 0);
			
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.pushAttrib();
			if(isVanilla)customMinecartRender(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, 1.0F);
			else Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
			GlStateManager.enableBlend();
	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	        GlStateManager.disableTexture2D();
	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
		else if (type == TransformType.FIRST_PERSON_RIGHT_HAND || type == TransformType.FIRST_PERSON_LEFT_HAND)
		{
			GlStateManager.pushMatrix();
			float scale = 1.5f;
			GlStateManager.scale(0.8F*scale, 0.8F*scale, 0.8F*scale);
			GlStateManager.translate(2, 0.5, 0);
			if(type == TransformType.FIRST_PERSON_RIGHT_HAND){
				GlStateManager.rotate(120+60, 0F, 1F, 0F);
			}
			if(type == TransformType.FIRST_PERSON_LEFT_HAND){
				GlStateManager.rotate(120+60, 0F, 1F, 0F);
			}
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);			
			if(isVanilla)customMinecartRender(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, 1.0F);
			else Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.enableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}
		else if (type == TransformType.THIRD_PERSON_RIGHT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND)
		{
			GlStateManager.pushMatrix();
			float scale = 2.0f;
			GlStateManager.scale(1.5F*scale, 1.5F*scale, 1.5F*scale);
			if(type == TransformType.THIRD_PERSON_RIGHT_HAND){
				GlStateManager.translate(2.7, -1.3, -2.7);
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.scale(0.8, 0.8, 0.8);
			}else{
				GlStateManager.translate(0.3, -0.1, 0.3);
				GlStateManager.scale(0.8, 0.8, 0.8);
			}
			if(isVanilla)customMinecartRender(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, 1.0F);
			else Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.popMatrix();
		}
		else if(type == TransformType.GROUND){
			GlStateManager.pushMatrix();
			float scale = 3.0f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.4, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			if(isVanilla)customMinecartRender(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, 1.0F);
			else Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		} else if(type == TransformType.HEAD){
			GlStateManager.pushMatrix();
			float scale = 3f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.4, 0);
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			if(isVanilla)customMinecartRender(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, 1.0F);
			else Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}else if(type == TransformType.FIXED || type == null){
			GlStateManager.pushMatrix();
			float scale = 3.0f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.6, 0);
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			if(isVanilla)customMinecartRender(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, 1.0F);
			else Minecraft.getMinecraft().getRenderManager().doRenderEntity(minecart, 0.0D, 0.0D, 0.0D, 0.0F, 0.0f, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}

		if(atrib)GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	private static final ResourceLocation MINECART_TEXTURES = new ResourceLocation("textures/entity/minecart.png");
    /** instance of ModelMinecart for rendering */
    protected static ModelBase modelMinecart = new ModelMinecart();
    public static void customMinecartRender(EntityMinecart entity, double x, double y, double z, float entityYaw, float partialTicks, float brightness){
		GlStateManager.pushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(MINECART_TEXTURES);
        long i = (long)entity.getEntityId() * 493286711L;
        i = i * i * 4392167121L + i * 98761L;
        float f = (((float)(i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f1 = (((float)(i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f2 = (((float)(i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GlStateManager.translate(f, f1, f2);
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        Vec3d vec3d = entity.getPos(d0, d1, d2);
        float f3 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        if (vec3d != null)
        {
            Vec3d vec3d1 = entity.getPosOffset(d0, d1, d2, 0.30000001192092896D);
            Vec3d vec3d2 = entity.getPosOffset(d0, d1, d2, -0.30000001192092896D);

            if (vec3d1 == null)
            {
                vec3d1 = vec3d;
            }

            if (vec3d2 == null)
            {
                vec3d2 = vec3d;
            }

            x += vec3d.xCoord - d0;
            y += (vec3d1.yCoord + vec3d2.yCoord) / 2.0D - d1;
            z += vec3d.zCoord - d2;
            Vec3d vec3d3 = vec3d2.addVector(-vec3d1.xCoord, -vec3d1.yCoord, -vec3d1.zCoord);

            if (vec3d3.lengthVector() != 0.0D)
            {
                vec3d3 = vec3d3.normalize();
                entityYaw = (float)(Math.atan2(vec3d3.zCoord, vec3d3.xCoord) * 180.0D / Math.PI);
                f3 = (float)(Math.atan(vec3d3.yCoord) * 73.0D);
            }
        }

        GlStateManager.translate((float)x, (float)y + 0.375F, (float)z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-f3, 0.0F, 0.0F, 1.0F);
        float f5 = (float)entity.getRollingAmplitude() - partialTicks;
        float f6 = entity.getDamage() - partialTicks;

        if (f6 < 0.0F)
        {
            f6 = 0.0F;
        }

        if (f5 > 0.0F)
        {
            GlStateManager.rotate(MathHelper.sin(f5) * f5 * f6 / 10.0F * (float)entity.getRollingDirection(), 1.0F, 0.0F, 0.0F);
        }

        int j = entity.getDisplayTileOffset();

        IBlockState iblockstate = entity.getDisplayTile();

        if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE)
        {
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(-0.5F, (float)(j - 8) / 16.0F, 0.5F);
            renderCartContentsCustom(entity, partialTicks, iblockstate, brightness);
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().renderEngine.bindTexture(MINECART_TEXTURES);            
        }

        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        modelMinecart.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
	}
    
    public static void renderCartContentsCustom(EntityMinecart p_188319_1_, float partialTicks, IBlockState p_188319_3_, float brightness)
    {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(p_188319_3_, brightness);
        GlStateManager.popMatrix();
    }
	
	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}

}
