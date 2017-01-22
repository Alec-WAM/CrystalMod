package alec_wam.CrystalMod.tiles.chest;

import java.util.Map;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.ItemStackTools;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.primitives.SignedBytes;

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

    private Random random;
    private RenderEntityItem itemRenderer;
    private static ModelChest model = new ModelChest();

    private static float[][] shifts = { { 0.3F, 0.45F, 0.3F }, { 0.7F, 0.45F, 0.3F }, { 0.3F, 0.45F, 0.7F }, { 0.7F, 0.45F, 0.7F }, { 0.3F, 0.1F, 0.3F },
            { 0.7F, 0.1F, 0.3F }, { 0.3F, 0.1F, 0.7F }, { 0.7F, 0.1F, 0.7F }, { 0.5F, 0.32F, 0.5F }, };

    public TileEntityBlueCrystalChestRenderer(Class<T> type)
    {
        random = new Random();
        itemRenderer = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()){
            @Override
            public int getModelCount(ItemStack stack) {
                return SignedBytes.saturatedCast(Math.min(ItemStackTools.getStackSize(stack) / 32, 15) + 1);
            }
            @Override
            public boolean shouldBob() {
                return false;
            }
            @Override
            public boolean shouldSpreadItems() {
                return false;
            }
            
            public void bindTexture(ResourceLocation location)
            {
            	if(location !=null && Minecraft.getMinecraft().getTextureManager() !=null)
            	Minecraft.getMinecraft().getTextureManager().bindTexture(location);
            }
            
            private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_)
            {
                ItemStack itemstack = itemIn.getEntityItem();
                Item item = itemstack.getItem();

                if (item == null)
                {
                    return 0;
                }
                else
                {
                    boolean flag = p_177077_9_.isGui3d();
                    int i = this.getModelCount(itemstack);
                    float f1 = shouldBob() ? MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F : 0;
                    @SuppressWarnings("deprecation")
					float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
                    GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f1 + 0.25F * f2, (float)p_177077_6_);

                    if (flag || Minecraft.getMinecraft().getRenderManager().options != null)
                    {
                        float f3 = (((float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180F / (float)Math.PI);
                        GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
                    }

                    if (!flag)
                    {
                        float f6 = -0.0F * (float)(i - 1) * 0.5F;
                        float f4 = -0.0F * (float)(i - 1) * 0.5F;
                        float f5 = -0.046875F * (float)(i - 1) * 0.5F;
                        GlStateManager.translate(f6, f4, f5);
                    }

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    return i;
                }
            }
            
            public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks)
            {
                ItemStack itemstack = entity.getEntityItem();
                random.setSeed(187L);
                boolean flag = false;

                if (this.bindEntityTexture(entity))
                {
                    Minecraft.getMinecraft().getTextureManager().getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
                    flag = true;
                }

                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                IBakedModel ibakedmodel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(itemstack);
                int i = func_177077_a(entity, x, y, z, partialTicks, ibakedmodel);

                for (int j = 0; j < i; ++j)
                {
                    {
                        GlStateManager.pushMatrix();

                        if (j > 0)
                        {
                            float f = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                            float f1 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                            float f2 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                            GlStateManager.translate(shouldSpreadItems() ? f : 0.0F, shouldSpreadItems() ? f1 : 0.0F, f2);
                        }

                        if (ibakedmodel.isGui3d())
                        GlStateManager.scale(0.5F, 0.5F, 0.5F);
                        ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
                        Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
                        GlStateManager.popMatrix();
                    }
                }

                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                this.bindEntityTexture(entity);

                if (flag)
                {
                	Minecraft.getMinecraft().getTextureManager().getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
                }

                //super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }
        };
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
            type = (CrystalChestType)state.getValue(BlockCrystalChest.VARIANT_PROP);
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
