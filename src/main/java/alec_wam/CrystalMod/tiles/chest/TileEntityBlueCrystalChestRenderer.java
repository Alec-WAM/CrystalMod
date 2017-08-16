package alec_wam.CrystalMod.tiles.chest;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class TileEntityBlueCrystalChestRenderer<T extends TileEntityBlueCrystalChest> extends TileEntitySpecialRenderer<T>
{
    private static Map<CrystalChestType, ResourceLocation> locations;

    static {
        Builder<CrystalChestType, ResourceLocation> builder = ImmutableMap.<CrystalChestType,ResourceLocation>builder();
        for (CrystalChestType typ : CrystalChestType.values()) {
            builder.put(typ, new ResourceLocation("crystalmod","textures/model/chests/"+typ.getModelTexture()));
        }
        locations = builder.build();
    }

    private static ModelChest model = new ModelChest();

    public TileEntityBlueCrystalChestRenderer(Class<T> type)
    {
        
    }

    public void render(TileEntityBlueCrystalChest tile, double x, double y, double z, float partialTick, int breakStage)
    {
        if (tile == null) {
            return;
        }
        int facing = 3;
        CrystalChestType type = tile.getType();

        if (tile != null && tile.hasWorld() && tile.getWorld().getBlockState(tile.getPos()).getBlock() == ModBlocks.crystalChest) {
            facing = tile.getFacing();
            type = tile.getType();
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            type = state.getValue(BlockCrystalChest.VARIANT_PROP);
        }
        float lidangle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTick;
        renderChest(x, y, z, type, facing, lidangle, breakStage);
        
        /*if (type.isTransparent() && tile.getDistanceSq(this.rendererDispatcher.entityX, this.rendererDispatcher.entityY, this.rendererDispatcher.entityZ) < 128d) {
            random.setSeed(254L);
            float shiftX;
            float shiftY;
            float shiftZ;
            int shift = 0;
            float blockScale = 0.70F;
            float timeD = (float) (360.0 * (double) (System.currentTimeMillis() & 0x3FFFL) / (double) 0x3FFFL);
            if (tile.getTopItemStacks()[1] == null) {
                shift = 8;
                blockScale = 0.85F;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x, (float) y, (float) z);
            EntityItem customitem = new EntityItem(this.getWorld());
            customitem.hoverStart = 0f;
            for (ItemStack item : tile.getTopItemStacks()) {
                if (shift > shifts.length) {
                    break;
                }
                if (item == null) {
                    shift++;
                    continue;
                }
                shiftX = shifts[shift][0];
                shiftY = shifts[shift][1];
                shiftZ = shifts[shift][2];
                shift++;
                GlStateManager.pushMatrix();
                GlStateManager.translate(shiftX, shiftY, shiftZ);
                GlStateManager.rotate(timeD, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(blockScale, blockScale, blockScale);
                customitem.setEntityItemStack(item);
                //Minecraft.getMinecraft().getRenderItem().renderItem(item, TransformType.GROUND);

                itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }*/
    }

    @Override
	public void renderTileEntityAt(TileEntityBlueCrystalChest tileentity, double x, double y, double z, float partialTick, int breakStage)
    {
        render(tileentity, x, y, z, partialTick, breakStage);
    }
    
    public static void renderChest(double x, double y, double z, CrystalChestType type, int facing, float lidangle, int breakStage){
    	if (breakStage >= 0)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(DESTROY_STAGES[breakStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else
        	Minecraft.getMinecraft().renderEngine.bindTexture(locations.get(type));
        GlStateManager.pushMatrix();
        if(type == CrystalChestType.PURE)
            GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1F, -1F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        int k = 0;
        if (facing == 2) {
            k = 180;
        }
        if (facing == 3) {
            k = 0;
        }
        if (facing == 4) {
            k = 90;
        }
        if (facing == 5) {
            k = -90;
        }
        GlStateManager.rotate(k, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        lidangle = 1.0F - lidangle;
        lidangle = 1.0F - lidangle * lidangle * lidangle;
        model.chestLid.rotateAngleX = -((lidangle * 3.141593F) / 2.0F);
        // Render the chest itself
        model.renderAll();
        if (breakStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
        if(type == CrystalChestType.PURE)
            GlStateManager.enableCull();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
