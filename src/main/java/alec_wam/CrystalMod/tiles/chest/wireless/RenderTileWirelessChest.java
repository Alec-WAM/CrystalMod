package alec_wam.CrystalMod.tiles.chest.wireless;

import java.util.UUID;

import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.util.Vector3d;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;

public class RenderTileWirelessChest extends TileEntitySpecialRenderer<TileWirelessChest> implements ICustomItemRenderer {

	@Override
	public void renderTileEntityAt(TileWirelessChest chest, double x, double y, double z, float partialTicks, int destroyStage)
    {
		GlStateManager.pushMatrix();
		float lidangle = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;
		renderChest(x, y, z, chest.code, chest.getFacing(), chest.isBoundToPlayer(), lidangle, destroyStage);
		GlStateManager.popMatrix();
    }

	@Override
	public void render(ItemStack stack) {
		GlStateManager.pushMatrix();
		int code = WirelessChestHelper.getDefaultCode(EnumDyeColor.WHITE);
		UUID owner = null;
		
		if(stack.hasTagCompound()){
			code = ItemNBTHelper.getInteger(stack, WirelessChestHelper.NBT_CODE, code);
			String nbtOwner = ItemNBTHelper.getString(stack, WirelessChestHelper.NBT_OWNER, WirelessChestHelper.PUBLIC_OWNER);
			if(UUIDUtils.isUUID(nbtOwner)){
				owner = UUIDUtils.fromString(nbtOwner);
			}
		}
		double x = -0.5;
	    double y = -0.5;
	    double z = -0.5;
		int facing = 3;
		/*if(type == TransformType.GUI){
			facing = 3;
			x-=0.5;
			y-=0.5;
			z-=0.5;
		}
		if(type == TransformType.GROUND){
			facing = 3;
			x-=0.5;
			y-=0.5;
			z-=0.5;
		}*/
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        renderChest(x, y, z, code, facing, owner !=null, 0.0f, -1);
		GlStateManager.popMatrix();
	}
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}
	
	private final static ResourceLocation texture_Chest = new ResourceLocation("crystalmod:textures/model/chests/wireless_public.png");
	private final static ResourceLocation texture_Chest_Private = new ResourceLocation("crystalmod:textures/model/chests/wireless_private.png");
	private final static ResourceLocation texture_Chest_Buttons = new ResourceLocation("crystalmod:textures/model/chests/wireless_buttons.png");

	private static ModelChest model = new ModelChest();
	public static void renderChest(double x, double y, double z, int code, int facing, boolean owned, float lidangle, int breakStage){
    	if (breakStage >= 0)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(DESTROY_STAGES[breakStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else
        	Minecraft.getMinecraft().renderEngine.bindTexture(owned ? texture_Chest_Private : texture_Chest);
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1F, -1F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        int k = 0;
        int buttonFacing = 0;
        if (facing == 2) {
        	buttonFacing = 2;
            k = 180;
        }
        if (facing == 3) {
        	buttonFacing = 0;
            k = 0;
        }
        if (facing == 4) {
        	buttonFacing = 1;
            k = 90;
        }
        if (facing == 5) {
        	buttonFacing = 3;
            k = -90;
        }
        GlStateManager.rotate(k, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        lidangle = 1.0F - lidangle;
        lidangle = 1.0F - lidangle * lidangle * lidangle;
        float chestLidAngle = -((lidangle * 3.141593F) / 2.0F);
        model.chestLid.rotateAngleX = chestLidAngle;
        // Render the chest itself
        model.renderAll();
        if (breakStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderButtons(code, buttonFacing, chestLidAngle);
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    public static void renderButtons(int code, int rot, double lidAngle) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture_Chest_Buttons);

        drawButton(0, WirelessChestHelper.getColor1(code), rot, lidAngle);
        drawButton(1, WirelessChestHelper.getColor2(code), rot, lidAngle);
        drawButton(2, WirelessChestHelper.getColor3(code), rot, lidAngle);
    }

    private static void drawButton(int button, int color, int rot, double lidAngle) {
        float texx = 0.25F * (color % 4);
        float texy = 0.25F * (color / 4);

        GlStateManager.pushMatrix();

        DyeButton ebutton = TileWirelessChest.buttons[button].copy();
        ebutton.rotate(0, 0.5625, 0.0625, 1, 0, 0, lidAngle);
        ebutton.rotateMeta(rot);
        Vector3d[] verts = ebutton.verts;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
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
