package alec_wam.CrystalMod.tiles.tank;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidTank;

public class RenderTileEntityTank<T extends TileEntityTank> extends TileEntitySpecialRenderer<T> {

	public void renderTileEntityAt(TileEntityTank tile, double x, double y, double z, float partialTicks, int destroyState)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x + 0.5F, (float)y, (float)z + 0.5F);
		GlStateManager.popMatrix();
	}
}
