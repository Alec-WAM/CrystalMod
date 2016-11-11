package alec_wam.CrystalMod.entities.pet.bombomb;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerBombombColor implements LayerRenderer<EntityBombomb>
{
    private static final ResourceLocation COLLAR = new ResourceLocation("crystalmod:textures/entities/bombomb/color.png");
    private final RenderEntityBombomb bombombRenderer;

    public LayerBombombColor(RenderEntityBombomb bombombRenderer)
    {
        this.bombombRenderer = bombombRenderer;
    }

    public void doRenderLayer(EntityBombomb entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (!entitylivingbaseIn.isInvisible())
        {
        	bombombRenderer.bindTexture(COLLAR);
            EnumDyeColor enumdyecolor = EnumDyeColor.byMetadata(entitylivingbaseIn.getColor().getMetadata());
            float[] afloat = EntitySheep.getDyeRgb(enumdyecolor);
            GlStateManager.color(afloat[0], afloat[1], afloat[2]);
            this.bombombRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
        }
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }
}