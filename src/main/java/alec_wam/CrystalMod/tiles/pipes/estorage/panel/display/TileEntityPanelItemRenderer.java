package alec_wam.CrystalMod.tiles.pipes.estorage.panel.display;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;

public class TileEntityPanelItemRenderer<T extends TileEntityPanelItem> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(TileEntityPanelItem te, double x, double y, double z, float partialTicks, int destroyStage) {
		if(te.network == null || !te.connected)return;
		if(te.displayItem == null && te.displayFluid == null)return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		GlStateManager.translate(0.5, 0.5, 0.8);
		GlStateManager.rotate(180, 0, 1, 0);
		
		
		if(te.facing == EnumFacing.SOUTH){
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(0, 0, -0.6);
		}
		if(te.facing == EnumFacing.WEST){
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.translate(-0.28, 0, -0.3);
		}
		if(te.facing == EnumFacing.EAST){
			GlStateManager.rotate(-90, 0, 1, 0);
			GlStateManager.translate(0.28, 0, -0.3);
		}
		if(te.facing == EnumFacing.UP){
			GlStateManager.rotate(-180, 0, 1, 0);
			GlStateManager.rotate(-90, 1, 0, 0);
			GlStateManager.translate(0, 0.28, -0.3);
		}
		if(te.facing == EnumFacing.DOWN){
			GlStateManager.rotate(-180, 0, 1, 0);
			GlStateManager.rotate(-90, 1, 0, 0);
			GlStateManager.rotate(180, 1, 0, 0);
			GlStateManager.translate(0, -0.28, -0.3);
		}
		
		boolean itemBlock = false;
		
		if(te.displayFluid !=null){
			float d = 0.005f;
			RenderUtil.renderFluidCuboid(te.displayFluid, te.getPos(), -0.5, -0.5, -0.01, d+0.1d, d+0.1d, d, 0.9d - d, 0.9d - d, d, false);
		} else if(te.displayItem !=null){
			itemBlock = renderItem(te.displayItem); 
		}
		
		GlStateManager.translate( 0.0f, 0.14f, -0.24f );
		GlStateManager.scale( 1.0f / 62f, 1.0f / 62f, 1.0f );

		final String renderedStackSize = te.displayText;

		final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		final int width = fr.getStringWidth( renderedStackSize );
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate( -0.5f * width, 20f, -0.24f );
		
		fr.drawString( renderedStackSize, 0, 0, 0 );
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
        GlStateManager.translate(-0.20, itemBlock ? -15f : -13f, -0.24f );
        
        String string = "";
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        EnumRarity rarity = EnumRarity.COMMON;
        
        
        if(te.displayItem !=null){
        	string = te.displayItem.getDisplayName();
        	font = te.displayItem.getItem().getFontRenderer(te.displayItem);
        	rarity = te.displayItem.getRarity();
        } else if(te.displayFluid !=null){
        	string = te.displayFluid.getLocalizedName();
        	if(te.displayFluid.getFluid() !=null)rarity = te.displayFluid.getFluid().getRarity(te.displayFluid);
        }
        
        if(font == null){
        	font = getFontRenderer();
        }
        
        int stringWidth = font.getStringWidth(string);
        float scale = Math.min(40F / (float) (stringWidth+10), 0.8F);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(0, font.FONT_HEIGHT*(1.0f-scale), 0);
        String begin = TextFormatting.BLACK+"";
        if(rarity !=EnumRarity.COMMON){
        	begin = rarity.rarityColor+"";
        }
        font.drawString(begin+string, -stringWidth/2, 0, -1);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
		
		GlStateManager.popMatrix();
		
	}
	
	public boolean renderItem(ItemStack displayItem){
		GlStateManager.pushMatrix();
		boolean itemBlock = false;	
		try
		{
			final ItemStack sis = displayItem;

			GlStateManager.disableRescaleNormal();
			IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(sis);
			
			if(sis.getItem() instanceof ItemBanner){
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(0.5, 0.5, 0.5);
				GlStateManager.translate( 0.0f, (-0.14f*2)+0.1, 0 );
			}
			else if(sis.getItem() instanceof ItemBlock && model.isGui3d()){
				GlStateManager.scale(0.5, 0.5, 0.5);
				
				
				GlStateManager.scale(1, 1, 0.001F);
				GlStateManager.rotate(180, 0, 0, 1);
				GlStateManager.rotate(-210, 1, 0, 0);
				GlStateManager.rotate(45, 0, 1, 0);
				itemBlock = true;
			}else{
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(0.4, 0.4, 0.4);
			}
			
			if (!Minecraft.getMinecraft().getRenderItem().shouldRenderItemIn3D(sis) || sis.getItem() instanceof ItemSkull)
            {
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }
			
			GlStateManager.translate( 0.0f, 0.14f, 0 );
			if(sis.getItem() instanceof net.minecraft.item.ItemMap){
				bindTexture(new ResourceLocation("textures/map/map_background.png"));
				MapData mapdata = Items.FILLED_MAP.getMapData(sis, Minecraft.getMinecraft().theWorld);

		        if (mapdata != null)
		        {
		        	GlStateManager.rotate(180, 0, 0, 1);
		        	GlStateManager.rotate(180, 0, 1, 0);
		        	GlStateManager.translate(-1, -0.9, 0);
		        	GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
		        	Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
		        }
			}else Minecraft.getMinecraft().getRenderItem().renderItem(sis, TransformType.GUI);
		}
		catch( final Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			GlStateManager.enableRescaleNormal();
		}
		GlStateManager.popMatrix();
		
		return itemBlock;
	}

}
