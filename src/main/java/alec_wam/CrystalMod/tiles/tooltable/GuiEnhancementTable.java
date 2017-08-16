package alec_wam.CrystalMod.tiles.tooltable;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.EnhancementManager;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.api.enhancements.KnowledgeManager;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Util;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.wrapper.InvWrapper;

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
			int x = 44;
			int y = 61;
			int offset = 0;
			for(int i = 0; i < 5; i++){
				if(i+offset >= displayList.size())continue;
				IEnhancement enhancement = displayList.get(i+offset);
				
				if(isPointInRegion(x, y, 16, 16, mouseX, mouseY)){
					applyOrRemove(enhancement, mouseButton == 0 ? 0 : mouseButton == 1 ? 1 : -1);
					return;
				}
				
				x+=18;
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
	
	public int beamTick;
	
	@Override
	public void updateScreen(){
		super.updateScreen();
		this.beamTick++;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		IEnhancement hoverEnhancement = null;
		int hoverSlot = -1;
		List<IEnhancement> displayList = getEnhancements();
		ItemStack tool = table.getStackInSlot(0);
		//drawRect(80, 20, 98, 38, Color.GRAY.getRGB());
		
		//drawRect(44, 60, 44+(18*5), 78, Color.GRAY.getRGB());
		
		if(!displayList.isEmpty()){
			int x = 44;
			int y = 61;
			int offset = 0;
			for(int i = 0; i < 5; i++){
				if(i+offset >= displayList.size())continue;
				IEnhancement enhancement = displayList.get(i+offset);
				if(isPointInRegion(x, y, 16, 16, mouseX, mouseY)){
					hoverSlot = i;
					hoverEnhancement = enhancement;
					/*GlStateManager.pushMatrix();
					GlStateManager.disableLighting();
	                GlStateManager.disableDepth();
	                GlStateManager.colorMask(true, true, true, false);
	                this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
	                GlStateManager.colorMask(true, true, true, true);
	                GlStateManager.enableLighting();
	                GlStateManager.enableDepth();
	                GlStateManager.popMatrix();*/
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
			        if(ItemStackTools.isValid(tool)){
			        	if(enhancement.isApplied(tool)){
			        		//Green Tint
			        		GlStateManager.pushMatrix();
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
			                vertexbuffer.pos(x, (double)y+16, 0.0D).endVertex();
			                vertexbuffer.pos((double)x+16, (double)y+16, 0.0D).endVertex();
			                vertexbuffer.pos((double)x+16, y, 0.0D).endVertex();
			                vertexbuffer.pos(x, y, 0.0D).endVertex();
			                tessellator.draw();
			                GlStateManager.enableTexture2D();
			                GlStateManager.disableBlend();
			                GlStateManager.colorMask(true, true, true, true);
			                GlStateManager.enableLighting();
			                GlStateManager.enableDepth();
			                GlStateManager.popMatrix();							
			        	}
			        }
			        
			        GlStateManager.popMatrix();
				}
				
				x+=18;
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
			        
			        if(ItemStackTools.isValid(tool)){
			        	if(!hoverEnhancement.isApplied(tool)){
			        		int needed = ItemStackTools.getStackSize(stack);
							needed-=ItemUtil.countItems(new InvWrapper(mc.player.inventory), stack, false);
			        		TextFormatting chat = needed <= 0 ? TextFormatting.GREEN : TextFormatting.RED;
			        		this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, chat+""+ItemStackTools.getStackSize(stack));
			        	} else {
			        		this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, ""+ItemStackTools.getStackSize(stack));
			        	}
			        }
			        
			        this.zLevel = 0.0F;
			        this.itemRender.zLevel = 0.0F;
			        GlStateManager.popMatrix();
					
					x+=18;
					if(i > 0 && Util.isMultipleOf(i, 4)){
						y+=18;
						x = 8;
					}
				}
			}
		}
		

		
		if(hoverSlot > -1){
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			
			GlStateManager.disableTexture2D();
	        GlStateManager.disableLighting();
	        
	        //GlStateManager.enableBlend();
	        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
	        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
	        
	        VertexBuffer renderer =Tessellator.getInstance().getBuffer();

			double size = 1.0;
			boolean rev = false;
			if(hoverSlot == 0){
				GlStateManager.translate(76, 30, 0);
				GlStateManager.rotate(45, 0, 0, 1);
				size=2.5;
			}
			if(hoverSlot == 1){
				GlStateManager.translate(82, 38, 0);
				GlStateManager.rotate(35, 0, 0, 1);
				size=1.5;
			}
			if(hoverSlot == 2){
				GlStateManager.translate(88, 36, 0);
				size=1.5;
			}
			if(hoverSlot == 3){
				rev = true;
				GlStateManager.translate(95, 38, 0);
				GlStateManager.rotate(-30, 0, 0, 1);
				size=1.5;
			}
			if(hoverSlot == 4){
				rev = true;
				GlStateManager.translate(98, 33, 0);
				GlStateManager.rotate(-45, 0, 0, 1);
				size=2.3;
			}
			
			GlStateManager.scale(0.5, 0.5, 0.5);
			renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			double offset = 10.0d;
			double accuracy = 0.0001;
			
			double time = beamTick * 0.35;
			
			for(double i = 0; i < (Math.PI*size); i+=accuracy){
				double x = i*offset;
				double y = rev ? (MathHelper.cos(((float)i)+(float)time)*offset) : (MathHelper.sin(((float)i)+(float)time)*offset);
				renderer.pos(y, x, 0).color(1F, 0.65F, 1F, 0.3F).endVertex();
			}
			
			Tessellator.getInstance().draw();

			//GlStateManager.disableBlend();
	        GlStateManager.enableLighting();
	        GlStateManager.enableTexture2D();
			
			GlStateManager.popMatrix();
			GlStateManager.popAttrib();
		}
    }
	
	public List<IEnhancement> getEnhancements(){
		ItemStack tool = table.getStackInSlot(0);
		if(ItemStackTools.isEmpty(tool)) return Lists.newArrayList();
		List<IEnhancement> validList = EnhancementManager.findValidEnhancements(tool, CrystalMod.proxy.getClientPlayer());
		List<IEnhancement> list = Lists.newArrayList();
		for(IEnhancement e : validList){
			if(KnowledgeManager.hasClientKnowledge(e)){
				list.add(e);
			}
		}
		return list;
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
