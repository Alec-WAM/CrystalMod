package alec_wam.CrystalMod.entities.minecarts.chests.wireless;

import alec_wam.CrystalMod.tiles.chest.wireless.RenderTileWirelessChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWirelessChestMinecart extends RenderMinecart<EntityWirelessChestMinecart>
{
    public RenderWirelessChestMinecart(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    @Override
	protected void renderCartContents(EntityWirelessChestMinecart cart, float partialTicks, IBlockState p_188319_3_)
    {
    	GlStateManager.pushMatrix();
    	GlStateManager.rotate(180, 0, 1, 0);
    	float lidangle = cart.prevLidAngle + (cart.lidAngle - cart.prevLidAngle) * partialTicks;
        RenderTileWirelessChest.renderChest(-1, 0, 0, cart.getCode(), 4, cart.isBoundToPlayer(), lidangle, -1);
        GlStateManager.popMatrix();
    }
    
    public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityWirelessChestMinecart> {

        @Override
        public Render<? super EntityWirelessChestMinecart> createRenderFor(RenderManager manager) {
          return new RenderWirelessChestMinecart(manager);
        }
    }
}