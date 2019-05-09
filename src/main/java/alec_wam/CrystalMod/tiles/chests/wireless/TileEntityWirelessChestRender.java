package alec_wam.CrystalMod.tiles.chests.wireless;

import alec_wam.CrystalMod.util.RenderUtil;
import alec_wam.CrystalMod.util.math.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityWirelessChestRender extends TileEntityRenderer<TileEntityWirelessChest>
{
	public static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("crystalmod", "textures/model/chests/wireless.png");
	public static final ResourceLocation TEXTURE_PRIVATE = new ResourceLocation("crystalmod", "textures/model/chests/wireless_private.png");
	public static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation("crystalmod", "textures/model/chests/wireless_buttons.png");

	private static ModelChest model = new ModelChest();

	@Override
	public void render(TileEntityWirelessChest tile, double x, double y, double z, float partialTick, int breakStage)
	{
		if (tile == null) {
			return;
		}
		EnumFacing facing = EnumFacing.NORTH;

		if (tile.getBlockState().getBlock() instanceof BlockWirelessChest) {
			facing = tile.getBlockState().get(BlockWirelessChest.FACING);
		}
		float lidangle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTick;
		renderChest(x, y, z, tile.getCode(), facing, tile.isBoundToPlayer(), lidangle, breakStage);
	}

	public static void renderChest(double x, double y, double z, int code, EnumFacing facing, boolean owned, float lidangle, int breakStage){
		if (breakStage >= 0)
		{
			Minecraft.getInstance().textureManager.bindTexture(DESTROY_STAGES[breakStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 4.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else{
			Minecraft.getInstance().textureManager.bindTexture(owned ? TEXTURE_PRIVATE : TEXTURE_NORMAL);
		}
		GlStateManager.pushMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GlStateManager.scalef(1.0F, -1F, -1F);
		GlStateManager.translatef(0.5F, 0.5F, 0.5F);
		int k = 0;
		if (facing == EnumFacing.SOUTH) {
			k = 180;
		}
		if (facing == EnumFacing.EAST) {
			k = 90;
		}
		if (facing == EnumFacing.WEST) {
			k = -90;
		}
		GlStateManager.rotatef(k, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
		lidangle = 1.0F - lidangle;
		lidangle = 1.0F - lidangle * lidangle * lidangle;
		float actualLidAngle =  -((lidangle * 3.141593F) / 2.0F);
		model.getLid().rotateAngleX = actualLidAngle;
		// Render the chest itself
		model.renderAll();
		
		GlStateManager.popMatrix();
		
		
		if (breakStage >= 0)
		{
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
		
		GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.translatef(0.5F, 0.5F, 0.5F);
        float offset = (facing == EnumFacing.EAST || facing == EnumFacing.WEST) ? 180 : 0;
		GlStateManager.rotatef(k + offset, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
		
		renderButtons(code, facing, actualLidAngle);        
        GlStateManager.popMatrix();
        
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public static void renderButtons(int code, EnumFacing facing, double lidAngle) {
        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_BUTTONS);

        drawButton(0, WirelessChestHelper.getColor1(code), facing, lidAngle);
        drawButton(1, WirelessChestHelper.getColor2(code), facing, lidAngle);
        drawButton(2, WirelessChestHelper.getColor3(code), facing, lidAngle);
    }

    private static void drawButton(int button, int color, EnumFacing facing, double lidAngle) {
        float texx = 0.25F * (color % 4);
        float texy = 0.25F * (color / 4);

        GlStateManager.pushMatrix();
        //We use 2 because the rotation is rotated to handle the lid opening
        DyeButton ebutton = BlockWirelessChest.buttons[2][button].copy();
        ebutton.rotate(0, 0.5625, 0.0625, 1, 0, 0, lidAngle);
        Vector3d[] verts = ebutton.verts;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderUtil.startDrawing(tessellator);
        RenderUtil.addVertexWithUV(buffer, verts[7], texx + 0.0938, texy + 0.0625);
        RenderUtil.addVertexWithUV(buffer, verts[3], texx + 0.0938, texy + 0.1875);
        RenderUtil.addVertexWithUV(buffer, verts[2], texx + 0.1562, texy + 0.1875);
        RenderUtil.addVertexWithUV(buffer, verts[6], texx + 0.1562, texy + 0.0625);

        RenderUtil.addVertexWithUV(buffer, verts[4], texx + 0.0938, texy + 0.0313);
        RenderUtil.addVertexWithUV(buffer, verts[7], texx + 0.0938, texy + 0.0313);
        RenderUtil.addVertexWithUV(buffer, verts[6], texx + 0.1562, texy + 0.0624);
        RenderUtil.addVertexWithUV(buffer, verts[5], texx + 0.1562, texy + 0.0624);

        RenderUtil.addVertexWithUV(buffer, verts[0], texx + 0.0938, texy + 0.2186);
        RenderUtil.addVertexWithUV(buffer, verts[1], texx + 0.1562, texy + 0.2186);
        RenderUtil.addVertexWithUV(buffer, verts[2], texx + 0.1562, texy + 0.1876);
        RenderUtil.addVertexWithUV(buffer, verts[3], texx + 0.0938, texy + 0.1876);

        RenderUtil.addVertexWithUV(buffer, verts[6], texx + 0.1563, texy + 0.0626);
        RenderUtil.addVertexWithUV(buffer, verts[2], texx + 0.1563, texy + 0.1874);
        RenderUtil.addVertexWithUV(buffer, verts[1], texx + 0.1874, texy + 0.1874);
        RenderUtil.addVertexWithUV(buffer, verts[5], texx + 0.1874, texy + 0.0626);

        RenderUtil.addVertexWithUV(buffer, verts[7], texx + 0.0937, texy + 0.0626);
        RenderUtil.addVertexWithUV(buffer, verts[4], texx + 0.0626, texy + 0.0626);
        RenderUtil.addVertexWithUV(buffer, verts[0], texx + 0.0626, texy + 0.1874);
        RenderUtil.addVertexWithUV(buffer, verts[3], texx + 0.0937, texy + 0.1874);
        tessellator.draw();

        GlStateManager.popMatrix();
    }
}
