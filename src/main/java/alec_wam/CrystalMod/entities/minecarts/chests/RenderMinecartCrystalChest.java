package alec_wam.CrystalMod.entities.minecarts.chests;

import alec_wam.CrystalMod.tiles.chest.TileEntityBlueCrystalChestRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMinecartCrystalChest extends RenderMinecart<EntityCrystalChestMinecartBase>
{
    public RenderMinecartCrystalChest(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    protected void renderCartContents(EntityCrystalChestMinecartBase cart, float p_188319_2_, IBlockState p_188319_3_)
    {
    	GlStateManager.pushMatrix();
        TileEntityBlueCrystalChestRenderer.renderChest(0, 0, -1, cart.getChestType(), 5, 0, -1);
        GlStateManager.popMatrix();
    }
    
    public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityCrystalChestMinecartBase> {

        @Override
        public Render<? super EntityCrystalChestMinecartBase> createRenderFor(RenderManager manager) {
          return new RenderMinecartCrystalChest(manager);
        }
    }
}