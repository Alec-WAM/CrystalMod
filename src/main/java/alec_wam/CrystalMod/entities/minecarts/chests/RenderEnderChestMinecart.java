package alec_wam.CrystalMod.entities.minecarts.chests;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEnderChestMinecart extends RenderMinecart<EntityEnderChestMinecart>
{
    public RenderEnderChestMinecart(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    @Override
	protected void renderCartContents(EntityEnderChestMinecart cart, float p_188319_2_, IBlockState p_188319_3_)
    {
    	super.renderCartContents(cart, p_188319_2_, p_188319_3_);
    	
    	/*EntitySlime slime = new EntitySlime(CrystalMod.proxy.getClientWorld());
    	
    	int pass = net.minecraftforge.client.MinecraftForgeClient.getRenderPass();
    	if(pass == 0 && slime.shouldRenderInPass(pass))
    	Minecraft.getMinecraft().getRenderManager().doRenderEntity(slime, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);*/
    	
    }
    
    public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityEnderChestMinecart> {

        @Override
        public Render<? super EntityEnderChestMinecart> createRenderFor(RenderManager manager) {
          return new RenderEnderChestMinecart(manager);
        }
    }
}