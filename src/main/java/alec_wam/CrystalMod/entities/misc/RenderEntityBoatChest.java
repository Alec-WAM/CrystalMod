package alec_wam.CrystalMod.entities.misc;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.IMultipassModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityBoatChest extends Render<EntityBoatChest>
{
    private static final ResourceLocation[] BOAT_TEXTURES = new ResourceLocation[] {new ResourceLocation("textures/entity/boat/boat_oak.png"), new ResourceLocation("textures/entity/boat/boat_spruce.png"), new ResourceLocation("textures/entity/boat/boat_birch.png"), new ResourceLocation("textures/entity/boat/boat_jungle.png"), new ResourceLocation("textures/entity/boat/boat_acacia.png"), new ResourceLocation("textures/entity/boat/boat_darkoak.png")};
    /** instance of ModelBoat for rendering */
    protected ModelBase modelBoat = new ModelBoat();

    public RenderEntityBoatChest(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityBoatChest entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        this.setupTranslation(x, y, z);
        this.setupRotation(entity, entityYaw, partialTicks);
        this.bindEntityTexture(entity);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        IBlockState iblockstate = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);

        if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE)
        {
            GlStateManager.pushMatrix();
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            float scale = 0.85f;
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.rotate(180, 1, 0, 0);
            GlStateManager.rotate(90, 0, 1, 0);
            GlStateManager.translate(0.1F, -0.2f, 0.5F);
            
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(iblockstate, entity.getBrightness(partialTicks));
            GlStateManager.popMatrix();
            
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindEntityTexture(entity);
        }
        
        this.modelBoat.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void setupRotation(EntityBoatChest p_188311_1_, float p_188311_2_, float p_188311_3_)
    {
        GlStateManager.rotate(180.0F - p_188311_2_, 0.0F, 1.0F, 0.0F);
        float f = (float)p_188311_1_.getTimeSinceHit() - p_188311_3_;
        float f1 = p_188311_1_.getDamageTaken() - p_188311_3_;

        if (f1 < 0.0F)
        {
            f1 = 0.0F;
        }

        if (f > 0.0F)
        {
            GlStateManager.rotate(MathHelper.sin(f) * f * f1 / 10.0F * (float)p_188311_1_.getForwardDirection(), 1.0F, 0.0F, 0.0F);
        }

        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
    }

    public void setupTranslation(double p_188309_1_, double p_188309_3_, double p_188309_5_)
    {
        GlStateManager.translate((float)p_188309_1_, (float)p_188309_3_ + 0.375F, (float)p_188309_5_);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityBoatChest entity)
    {
        return BOAT_TEXTURES[entity.getBoatType().ordinal()];
    }

    public boolean isMultipass()
    {
        return true;
    }

    public void renderMultipass(EntityBoatChest p_188300_1_, double p_188300_2_, double p_188300_4_, double p_188300_6_, float p_188300_8_, float p_188300_9_)
    {
        GlStateManager.pushMatrix();
        this.setupTranslation(p_188300_2_, p_188300_4_, p_188300_6_);
        this.setupRotation(p_188300_1_, p_188300_8_, p_188300_9_);
        this.bindEntityTexture(p_188300_1_);
        ((IMultipassModel)this.modelBoat).renderMultipass(p_188300_1_, p_188300_9_, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }
}