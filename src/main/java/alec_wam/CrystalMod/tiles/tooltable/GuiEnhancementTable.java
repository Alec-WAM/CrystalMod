package alec_wam.CrystalMod.tiles.tooltable;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.enhancements.EnhancementManager;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiEnhancementTable extends GuiContainer {

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation("crystalmod:textures/gui/enhancementtable.png");
    public TileEnhancementTable table;
	
	public GuiEnhancementTable(InventoryPlayer playerInv, TileEnhancementTable table) {
		super(new ContainerEnhancementTable(playerInv, table));
		this.table = table;
		ySize = 172;
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		List<IEnhancement> displayList = getEnhancements();
		if(!displayList.isEmpty()){
			for(int i = 0; i < displayList.size(); i++){
				IEnhancement enhancement = displayList.get(i);
				int x = 30 + (18*i);
				int y = 19;
				if(isPointInRegion(x, y, 16, 16, mouseX, mouseY)){
					applyOrRemove(enhancement, mouseButton == 0 ? 0 : mouseButton == 1 ? 1 : -1);
					return;
				}
			}
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	public void applyOrRemove(IEnhancement enhancement, int mode){
		ItemStack tool = table.getStackInSlot(0);
		if(ItemStackTools.isValid(tool)){
			if(mode == 0){
				CrystalModNetwork.sendToServer(new PacketEnhancementTable(table.getPos(), enhancement, 0));
			}
			if(mode == 1){
				CrystalModNetwork.sendToServer(new PacketEnhancementTable(table.getPos(), enhancement, 1));
			}
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		IEnhancement hoverEnhancement = null;
		List<IEnhancement> displayList = getEnhancements();
		if(!displayList.isEmpty()){
			for(int i = 0; i < displayList.size(); i++){
				IEnhancement enhancement = displayList.get(i);
				int x = 30 + (18*i);
				int y = 19;
				if(isPointInRegion(x, y, 16, 16, mouseX, mouseY)){
					hoverEnhancement = enhancement;
					GlStateManager.disableLighting();
	                GlStateManager.disableDepth();
	                GlStateManager.colorMask(true, true, true, false);
	                this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
	                GlStateManager.colorMask(true, true, true, true);
	                GlStateManager.enableLighting();
	                GlStateManager.enableDepth();
				}
				ItemStack stack = enhancement.getDisplayItem();
				if(ItemStackTools.isValid(stack)){
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.0F, 0.0F, 32.0F);
			        this.zLevel = 200.0F;
			        this.itemRender.zLevel = 200.0F;
			        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
			        if (font == null) font = fontRendererObj;
			        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
			        this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, "");
			        this.zLevel = 0.0F;
			        this.itemRender.zLevel = 0.0F;
			        ItemStack tool = table.getStackInSlot(0);
			        if(ItemStackTools.isValid(tool)){
			        	if(enhancement.isApplied(tool)){
			        		//Green Tint
			        		GlStateManager.disableLighting();
			                GlStateManager.disableDepth();
			                GlStateManager.colorMask(true, true, true, false);
			                Tessellator tessellator = Tessellator.getInstance();
			                VertexBuffer vertexbuffer = tessellator.getBuffer();
			                GlStateManager.enableBlend();
			                GlStateManager.disableTexture2D();
			                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			                GlStateManager.color(0, 1, 0, 0.3f);
			                vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
			                vertexbuffer.pos((double)x, (double)y+16, 0.0D).endVertex();
			                vertexbuffer.pos((double)x+16, (double)y+16, 0.0D).endVertex();
			                vertexbuffer.pos((double)x+16, (double)y, 0.0D).endVertex();
			                vertexbuffer.pos((double)x, (double)y, 0.0D).endVertex();
			                tessellator.draw();
			                GlStateManager.enableTexture2D();
			                GlStateManager.disableBlend();
			                GlStateManager.colorMask(true, true, true, true);
			                GlStateManager.enableLighting();
			                GlStateManager.enableDepth();
			        	}
			        }
			        
			        GlStateManager.popMatrix();
				}
			}
		}
		
		if(hoverEnhancement !=null){
			List<ItemStack> stacks = hoverEnhancement.getRequiredItems();
			int x = 8;
			int y = 0;
			for(int i = 0; i < stacks.size(); i++){
				ItemStack stack = stacks.get(i);
				if(ItemStackTools.isValid(stack)){
					GlStateManager.pushMatrix();
					GlStateManager.translate(mouseX-guiLeft, mouseY-guiTop, 32.0F);
			        this.zLevel = 200.0F;
			        this.itemRender.zLevel = 200.0F;
			        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
			        if (font == null) font = fontRendererObj;
			        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
			        this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, ""+ItemStackTools.getStackSize(stack));
			        this.zLevel = 0.0F;
			        this.itemRender.zLevel = 0.0F;
			        GlStateManager.popMatrix();
					
					x+=18;
					if(i % 4 == 0){
						y+=18;
						x = 8;
					}
				}
			}
		}
    }
	
	public List<IEnhancement> getEnhancements(){
		ItemStack tool = table.getStackInSlot(0);
		if(ItemStackTools.isEmpty(tool)) return Lists.newArrayList();
		return EnhancementManager.findValidEnhancements(tool);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}

}
