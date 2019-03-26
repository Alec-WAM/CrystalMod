package alec_wam.CrystalMod.entities.balloon;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityBalloon extends Render<EntityBalloon>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/entities/balloon.png");

    public RenderEntityBalloon(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    @Override
    public void doRender(EntityBalloon entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
    	super.doRender(entity, x, y, z, entityYaw, partialTicks);
    	//TODO make render "reversed" when riding player
    	if(entity.isRiding()){
    		Entity riding = entity.getRidingEntity();
    		double sX = entity.lastTickPosX - entity.posX;
        	double sZ = entity.lastTickPosZ - entity.posZ;
        	renderAttached(x, y, z, sX * 2.0, sZ * 2.0, entity.getColor(), 3.0F);
    	}
    	else {
    		double sX = entity.lastTickPosX - entity.posX;
        	double sZ = entity.lastTickPosZ - entity.posZ;
    		renderBalloon(x, y, z, sX, sZ, entity.getColor(), 3.0F);    	
    	}
    }
    
    public static void renderBalloon(double x, double y, double z, double sX, double sZ, EnumDyeColor color, float sWidth){
    	GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
    	GlStateManager.translate(x, y, z);
    	float[] afloat = EntitySheep.getDyeRgb(color);
        GlStateManager.color(afloat[0], afloat[1], afloat[2]);
    	Tessellator tessy = Tessellator.getInstance();
		VertexBuffer render = tessy.getBuffer();
		
		double width = 0.5;
		double height = 0.5;
		double rX = 0.0;
		double rY = 1.75;
		double rZ = 0.0;
		
		double texUMin = 0 / 32.0F;
		double texUMax = 8.0F / 32.0F;
		double texVMin = 0 / 32.0F;
		double texVMax = 8.0F / 32.0F;
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
    	render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		render.pos(rX - (width/2), rY + (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		
		render.pos(rX + (width/2), rY - (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		render.pos(rX + (width/2), rY - (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX - (width/2), rY - (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX - (width/2), rY - (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		
		render.pos(rX - (width/2), rY - (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX + (width/2), rY - (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		
		render.pos(rX - (width/2), rY - (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		render.pos(rX + (width/2), rY - (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		
		render.pos(rX + (width/2), rY - (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX + (width/2), rY - (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		
		render.pos(rX - (width/2), rY - (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		render.pos(rX - (width/2), rY - (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		tessy.draw();
		
		texUMin = 0 / 32.0F;
		texUMax = 1.0F / 32.0F;
		texVMin = 10 / 32.0F;
		texVMax = 11.0F / 32.0F;
        
		GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        double diff = 0.02;
        double sY = rY - (height/2);
        double rPos = sY;
        
        double lastOffX = 0;
        double lastOffZ = 0;
        for(int i = 0; i < 5; i ++){
        	float r = 1.0F;
        	float g = 1.0F;
        	float b = 1.0F;
        	if (i % 2 == 0)
            {
                r *= 0.9F;
                g *= 0.9F;
                b *= 0.9F;
            }
        	double MY = rPos; 
        	double mY = rPos - (1.4 / 5);
        	double offsetIntensity = 1.0F - (1.0F / 5.0F * (5 - i));
        	
        	double SX = sX * offsetIntensity;
        	double SZ = sZ * offsetIntensity;
        	double offX = i == 0 ? SX : lastOffX;
        	double offZ = i == 0 ? SZ : lastOffZ;
        	lastOffX = SX;
        	lastOffZ = SZ;
        	
	        render.pos(-diff + offX, MY, 0 + offZ).tex(texUMin, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(diff + offX, MY, 0 + offZ).tex(texUMax, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX + diff, mY, SZ).tex(texUMin, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX - diff, mY, SZ).tex(texUMax, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX - diff, mY, SZ).tex(texUMax, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX + diff, mY, SZ).tex(texUMin, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(diff + offX, MY, 0 + offZ).tex(texUMax, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(-diff + offX, MY, 0 + offZ).tex(texUMin, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(0 + offX, MY, -diff + offZ).tex(texUMin, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(0 + offX, MY, diff + offZ).tex(texUMax, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX, mY, SZ + diff).tex(texUMin, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX, mY, SZ - diff).tex(texUMax, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX, mY, SZ - diff).tex(texUMax, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX, mY, SZ + diff).tex(texUMin, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(0 + offX, MY, diff + offZ).tex(texUMax, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(0 + offX, MY, -diff + offZ).tex(texUMin, texVMin).color(r, g, b, 1.0F).endVertex();
	        rPos -= (1.4 / 5);
        }
        
        tessy.draw();
        GlStateManager.popMatrix();
        

        GlStateManager.enableLighting();
		GlStateManager.popMatrix();
    }
    
    public static void renderAttached(double x, double y, double z, double sX, double sZ, EnumDyeColor color, float sWidth){
    	GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
    	GlStateManager.translate(x, y, z);
    	float[] afloat = EntitySheep.getDyeRgb(color);
        GlStateManager.color(afloat[0], afloat[1], afloat[2]);
    	Tessellator tessy = Tessellator.getInstance();
		VertexBuffer render = tessy.getBuffer();
		
		double width = 0.5;
		double height = 0.5;
		double rX = sX;
		double rY = /*1.75*/ 1.75;
		double rZ = sZ;
		
		double texUMin = 0 / 32.0F;
		double texUMax = 8.0F / 32.0F;
		double texVMin = 0 / 32.0F;
		double texVMax = 8.0F / 32.0F;
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
    	render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		render.pos(rX - (width/2), rY + (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		
		render.pos(rX + (width/2), rY - (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		render.pos(rX + (width/2), rY - (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX - (width/2), rY - (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX - (width/2), rY - (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		
		render.pos(rX - (width/2), rY - (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX + (width/2), rY - (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		
		render.pos(rX - (width/2), rY - (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		render.pos(rX + (width/2), rY - (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		
		render.pos(rX + (width/2), rY - (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ - (width/2)).tex(texUMax, texVMin).endVertex();
		render.pos(rX + (width/2), rY + (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		render.pos(rX + (width/2), rY - (height/2), rZ + (width/2)).tex(texUMax, texVMax).endVertex();
		
		render.pos(rX - (width/2), rY - (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		render.pos(rX - (width/2), rY - (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ + (width/2)).tex(texUMin, texVMax).endVertex();
		render.pos(rX - (width/2), rY + (height/2), rZ - (width/2)).tex(texUMin, texVMin).endVertex();
		tessy.draw();
		
		texUMin = 0 / 32.0F;
		texUMax = 1.0F / 32.0F;
		texVMin = 10 / 32.0F;
		texVMax = 11.0F / 32.0F;
        
		GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        double diff = 0.02;
        double sY = rY - (height/2);
        double rPos = sY;
        
        double lastOffX = 0;
        double lastOffZ = 0;
        for(int i = 0; i < 1; i ++){
        	float r = 1.0F;
        	float g = 1.0F;
        	float b = 1.0F;
        	if (i % 2 == 0)
            {
                r *= 0.9F;
                g *= 0.9F;
                b *= 0.9F;
            }
        	double MY = rPos + (0.5 / 1); 
        	double mY = rPos;
        	double offsetIntensity = 1.0F - (1.0F / 1.0F * (i));
        	
        	double SX = sX * offsetIntensity;
        	double SZ = sZ * offsetIntensity;
        	double offX = lastOffX;
        	double offZ = lastOffZ;
        	lastOffX = SX;
        	lastOffZ = SZ;
        	
	        render.pos(-diff + offX, MY, 0 + offZ).tex(texUMin, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(diff + offX, MY, 0 + offZ).tex(texUMax, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX + diff, mY, SZ).tex(texUMin, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX - diff, mY, SZ).tex(texUMax, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX - diff, mY, SZ).tex(texUMax, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX + diff, mY, SZ).tex(texUMin, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(diff + offX, MY, 0 + offZ).tex(texUMax, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(-diff + offX, MY, 0 + offZ).tex(texUMin, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(0 + offX, MY, -diff + offZ).tex(texUMin, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(0 + offX, MY, diff + offZ).tex(texUMax, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX, mY, SZ + diff).tex(texUMin, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX, mY, SZ - diff).tex(texUMax, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX, mY, SZ - diff).tex(texUMax, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(SX, mY, SZ + diff).tex(texUMin, texVMax).color(r, g, b, 1.0F).endVertex();
	        render.pos(0 + offX, MY, diff + offZ).tex(texUMax, texVMin).color(r, g, b, 1.0F).endVertex();
	        render.pos(0 + offX, MY, -diff + offZ).tex(texUMin, texVMin).color(r, g, b, 1.0F).endVertex();
	        rPos += (0.5 / 1);
        }
        
        tessy.draw();
        GlStateManager.popMatrix();
        

        GlStateManager.enableLighting();
		GlStateManager.popMatrix();
    }
    
    @Override
	protected ResourceLocation getEntityTexture(EntityBalloon entity)
    {
        return TEXTURE;
    }
    
    public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityBalloon> {

        @Override
        public Render<? super EntityBalloon> createRenderFor(RenderManager manager) {
          return new RenderEntityBalloon(manager);
        }
    }
    
    public static class ItemRender implements ICustomItemRenderer {
    	public static final List<ModelResourceLocation> MODELS = Lists.newArrayList();
    	static{
    		for(EnumDyeColor type : EnumDyeColor.values()){
    			MODELS.add(new ModelResourceLocation(ModItems.balloon.getRegistryName(), "color="+type.getUnlocalizedName()));
    		}
    	}
    	
    	
		@Override
		public void render(ItemStack stack) {
			GlStateManager.pushMatrix();
			float sWidth = 3.0F;
			if(lastTransform == TransformType.GROUND || lastTransform == TransformType.HEAD)
			{
				GlStateManager.translate(0, -1, 0);
			}
			else if(lastTransform == TransformType.GUI)
			{
				GlStateManager.translate(0, -1.1, 0);
				GlStateManager.scale(0.9, 0.9, 0.9);
			}
			else if(lastTransform == TransformType.FIRST_PERSON_RIGHT_HAND)
			{
				GlStateManager.translate(0.0, -0.5, 0);
				GlStateManager.scale(1.5, 1.5, 1.5);
				sWidth = 8.0F;
			}
			else if(lastTransform == TransformType.FIRST_PERSON_LEFT_HAND)
			{
				GlStateManager.translate(0.1, -0.5, 0.1);
				GlStateManager.scale(1.5, 1.5, 1.5);
				sWidth = 8.0F;
			}
			else if(lastTransform == TransformType.THIRD_PERSON_RIGHT_HAND)
			{
				GlStateManager.rotate(65, 1, 0, 1);
				GlStateManager.translate(0.2, -0.5, 0);
				GlStateManager.scale(1.2, 1.2, 1.2);
			}
			else if(lastTransform == TransformType.THIRD_PERSON_LEFT_HAND)
			{
				GlStateManager.rotate(65, 0, 0, -1);
				GlStateManager.rotate(45, 1, 0, 0);
				GlStateManager.translate(-0.2, -0.5, 0);
				GlStateManager.scale(1.2, 1.2, 1.2);
			}
			else if(lastTransform == TransformType.FIXED)
			{
				GlStateManager.translate(0, -1.0, 0);
			}
			
			renderBalloon(0, 0, 0, 0, 0, EnumDyeColor.byMetadata(stack.getMetadata()), sWidth);
			
			GlStateManager.popMatrix();
		}

		private TransformType lastTransform;
		
		@Override
		public TRSRTransformation getTransform(TransformType type) {
			lastTransform = type;
			return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
		}
		
		@Override
		public List<ModelResourceLocation> getModels() {
			return MODELS;
		}

	}
	
}
