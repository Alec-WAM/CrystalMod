package alec_wam.CrystalMod.entities.minions;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerBackItem implements LayerRenderer<EntityMinionBase>
{
    private final RenderLivingBase<?> livingEntityRenderer;

    public LayerBackItem(RenderLivingBase<?> livingEntityRendererIn)
    {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    @Override
	public void doRenderLayer(EntityMinionBase entitylivingbaseIn1, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
    	ItemStack itemstack = entitylivingbaseIn1.getBackItem();

        if (ItemStackTools.isValid(itemstack))
        {
            GlStateManager.pushMatrix();

            if (this.livingEntityRenderer.getMainModel().isChild)
            {
                float f = 0.5F;
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(f, f, f);
            }

            ((ModelBiped)this.livingEntityRenderer.getMainModel()).bipedBody.postRender(0.0625F);
            GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);
            GlStateManager.translate(0, 0, 0.1);
            Minecraft minecraft = Minecraft.getMinecraft();

            if (entitylivingbaseIn1.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.203125F, 0.0F);
            }

            minecraft.getItemRenderer().renderItem(entitylivingbaseIn1, itemstack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }

    @Override
	public boolean shouldCombineTextures()
    {
        return false;
    }
}