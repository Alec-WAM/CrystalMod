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

	public RenderMiniPlayer renderNormal = new RenderMiniPlayer(Minecraft.getMinecraft().getRenderManager(), false);
	public RenderMiniPlayer renderSlim = new RenderMiniPlayer(Minecraft.getMinecraft().getRenderManager(), true);
}
