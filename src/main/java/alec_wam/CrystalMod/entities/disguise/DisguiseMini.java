package alec_wam.CrystalMod.entities.disguise;

import alec_wam.CrystalMod.entities.disguise.render.RenderMiniPlayer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DisguiseMini {

	@SideOnly(Side.CLIENT)
    public RenderMiniPlayer renderNormal = new RenderMiniPlayer(Minecraft.getMinecraft().getRenderManager(), false);
	@SideOnly(Side.CLIENT)
    public RenderMiniPlayer renderSlim = new RenderMiniPlayer(Minecraft.getMinecraft().getRenderManager(), true);
}
