package alec_wam.CrystalMod.entities.disguise;

import alec_wam.CrystalMod.entities.disguise.render.RenderMiniPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DisguiseMini {

	public RenderMiniPlayer proxyRenderer = new RenderMiniPlayer(Minecraft.getMinecraft().getRenderManager());
	
	public void render(EntityPlayer player, double x, double y, double z, float partialTicks, boolean frontface)
	{
	    float f1 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
	    

	    double d3 = player.getYOffset();
	    
	    if(player instanceof AbstractClientPlayer){
	    	this.proxyRenderer.doRender((AbstractClientPlayer) player, x, y + d3, z, f1, partialTicks);
	    }
	}
}
