package alec_wam.CrystalMod.entities.misc;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrystalModPainting extends Render<EntityCrystalModPainting>
{
    public RenderCrystalModPainting(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityCrystalModPainting entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.enableRescaleNormal();
        EntityCrystalModPainting.EnumArt art = entity.art;
        this.bindEntityTexture(entity);
        GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.renderPainting(entity, art.blockWidth * 16, art.blockHeight * 16);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCrystalModPainting entity)
    {
        return CrystalMod.resourceL("textures/entities/painting/"+entity.art.textureName+".png");
    }

    private void renderPainting(EntityCrystalModPainting painting, int realWidth, int realHeight)
    {
    	float f = (float)(-realWidth) / 2.0F;
        float f1 = (float)(-realHeight) / 2.0F;
        
        //Reversed
        float minX = f + (realWidth);
        float maxX = f;
        float minY = f1 + (realHeight);
        float maxY = f1;
        
        this.setLightmap(painting, (minX + maxX) / 2.0F, (minY + maxY) / 2.0F);
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f;
        float maxV = 1.0f;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);                
        vertexbuffer.pos((double)minX, (double)maxY, -0.5D).tex((double)minU, (double)maxV).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)maxY, -0.5D).tex((double)maxU, (double)maxV).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)minY, -0.5D).tex((double)maxU, (double)minV).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexbuffer.pos((double)minX, (double)minY, -0.5D).tex((double)minU, (double)minV).normal(0.0F, 0.0F, -1.0F).endVertex();
        
        //Back
        vertexbuffer.pos((double)minX, (double)minY, 0.5D).tex(0.75D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)minY, 0.5D).tex(0.8125D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)maxY, 0.5D).tex(0.8125D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.pos((double)minX, (double)maxY, 0.5D).tex(0.75D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
        
        //Top
        vertexbuffer.pos((double)minX, (double)minY, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)minY, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)minY, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)minX, (double)minY, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        
        //Bottom
        vertexbuffer.pos((double)minX, (double)maxY, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)maxY, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)maxY, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)minX, (double)maxY, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
        
        //Left
        vertexbuffer.pos((double)minX, (double)minY, 0.5D).tex(0.751953125D, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)minX, (double)maxY, 0.5D).tex(0.751953125D, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)minX, (double)maxY, -0.5D).tex(0.751953125D, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)minX, (double)minY, -0.5D).tex(0.751953125D, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
        
        //Right
        vertexbuffer.pos((double)maxX, (double)minY, -0.5D).tex(0.751953125D, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)maxY, -0.5D).tex(0.751953125D, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)maxY, 0.5D).tex(0.751953125D, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.pos((double)maxX, (double)minY, 0.5D).tex(0.751953125D, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
        
        tessellator.draw();
    }

    private void setLightmap(EntityCrystalModPainting painting, float p_77008_2_, float p_77008_3_)
    {
        int i = MathHelper.floor(painting.posX);
        int j = MathHelper.floor(painting.posY + (double)(p_77008_3_ / 16.0F));
        int k = MathHelper.floor(painting.posZ);
        EnumFacing enumfacing = painting.facingDirection;

        if (enumfacing == EnumFacing.NORTH)
        {
            i = MathHelper.floor(painting.posX + (double)(p_77008_2_ / 16.0F));
        }

        if (enumfacing == EnumFacing.WEST)
        {
            k = MathHelper.floor(painting.posZ - (double)(p_77008_2_ / 16.0F));
        }

        if (enumfacing == EnumFacing.SOUTH)
        {
            i = MathHelper.floor(painting.posX - (double)(p_77008_2_ / 16.0F));
        }

        if (enumfacing == EnumFacing.EAST)
        {
            k = MathHelper.floor(painting.posZ + (double)(p_77008_2_ / 16.0F));
        }

        int l = this.renderManager.world.getCombinedLight(new BlockPos(i, j, k), 0);
        int i1 = l % 65536;
        int j1 = l / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)i1, (float)j1);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
    }
}