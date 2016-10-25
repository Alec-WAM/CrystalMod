package alec_wam.CrystalMod.tiles.pipes.estorage.panel.popup;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.sun.prism.paint.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.util.ModLogger;

public class CraftingAmountPopup extends Popup {

	public ItemStackData currentCraft;
	private GuiTextField craftingRequestAmount;
	
	public CraftingAmountPopup(GuiPanel guiPanel, ItemStackData data){
		this.currentCraft = data;
		
		craftingRequestAmount = new GuiTextField(1, Minecraft.getMinecraft().fontRendererObj, guiPanel.getCraftBoxX()+7, guiPanel.getCraftBoxY()+28, 78, 10);
        craftingRequestAmount.setEnableBackgroundDrawing(false);
        craftingRequestAmount.setText("1");
        
        this.craftingRequestAmount.setEnabled(true);
		this.craftingRequestAmount.setFocused(true);
	}
	
	@Override
	public void update(GuiPanel guiPanel) {
		craftingRequestAmount.updateCursorCounter();
	}

	@Override
	public boolean clicked(GuiPanel guiPanel, int mouseX, int mouseY, int mouseButton) {
		int mX = mouseX - guiPanel.guiLeft;
		int mY = mouseY - guiPanel.guiTop;
		if(mX >= guiPanel.getCraftBoxX() && mY >= guiPanel.getCraftBoxY()){
			if(mX <= guiPanel.getCraftBoxX()+90 && mY <= guiPanel.getCraftBoxY()+59){
				int x = mX - guiPanel.getCraftBoxX();
				int y = mY - guiPanel.getCraftBoxY();
				if(x >=48 && y >= 41){
					if(x <=78 && y <= 52){
						return keyTyped(guiPanel, '0', Keyboard.KEY_RETURN);
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public void render(GuiPanel guiPanel){
		GlStateManager.pushMatrix();
    	GlStateManager.disableDepth();
    	GlStateManager.disableLighting();
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/eStorage_panel_craftbox.png"));
    	guiPanel.drawTexturedModalRect(guiPanel.getCraftBoxX(), guiPanel.getCraftBoxY(), 0, 0, 90, 59);
    	craftingRequestAmount.drawTextBox();
    	
    	ItemStack dis = this.currentCraft !=null ? this.currentCraft.stack : null;
		
		if(dis !=null){
			GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            
            
            GlStateManager.pushMatrix();
            guiPanel.getItemRender().zLevel = 200.0F;
            guiPanel.getItemRender().renderItemAndEffectIntoGUI(dis, guiPanel.getCraftBoxX()+5, guiPanel.getCraftBoxY()+5);
            GlStateManager.popMatrix();
            guiPanel.getItemRender().zLevel = 0.0F;
            GlStateManager.disableLighting();

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            
            GlStateManager.pushMatrix();
            GlStateManager.enableLighting();
            GlStateManager.disableDepth();
            
            GlStateManager.translate(guiPanel.getCraftBoxX()+26, guiPanel.getCraftBoxY()+10, 0);
            String string = dis.getDisplayName();
            int stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(string);
            float scale = Math.min(60F / (float) (stringWidth+10), 1F);
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(0, Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT*(1.0f-scale), 0);
            Minecraft.getMinecraft().fontRendererObj.drawString(""+string, 0, 0, 0x404040);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            
            
            GlStateManager.pushMatrix();
            GlStateManager.enableLighting();
            GlStateManager.disableDepth();
            GlStateManager.translate(guiPanel.getCraftBoxX(), guiPanel.getCraftBoxY(), 0);
            int color = java.awt.Color.GRAY.getRGB();
            
            
            boolean hovered = false;
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i1 = scaledresolution.getScaledWidth();
            int j1 = scaledresolution.getScaledHeight();
            int mouseX = Mouse.getX() * i1 / mc.displayWidth;
            int mouseY = j1 - Mouse.getY() * j1 / mc.displayHeight - 1;
            
            int mX = mouseX - guiPanel.guiLeft;
    		int mY = mouseY - guiPanel.guiTop;
    		if(mX >= guiPanel.getCraftBoxX()+48 && mY >= guiPanel.getCraftBoxY()+41){
    			if(mX <= guiPanel.getCraftBoxX()+78 && mY <= guiPanel.getCraftBoxY()+52){
    				hovered = true;
    			}
    		}
            int j = 14737632;

            /*if (false)
            {
                j = 10526880;
            }
            else */if (hovered)
            {
                j = 16777120;
                color = java.awt.Color.GRAY.brighter().getRGB();
            }
            GuiPanel.drawRect(48, 41, 78, 52, color);
            Minecraft.getMinecraft().fontRendererObj.drawString("Start", 50, 43, j);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            
            
		}
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean keyTyped(GuiPanel guiPanel, char typedChar, int keyCode) {

		if(keyCode == Keyboard.KEY_RETURN){
			if(currentCraft == null || currentCraft.stack == null)return true;
			int current = 0;
			try{
				current = Integer.parseInt(craftingRequestAmount.getText());
			}catch(Exception e){
			}
			if(current > 0){
				ItemStack copy = currentCraft.stack.copy();
				copy.stackSize = 1;
				
				ItemStackData data = new ItemStackData(copy, currentCraft.interPos, currentCraft.interDim);
				data.isCrafting = currentCraft.isCrafting;
				
				guiPanel.sendUpdate(4, -1, current, data);
			}
			craftingRequestAmount.setText("1");
			this.craftingRequestAmount.setEnabled(false);
			this.craftingRequestAmount.setFocused(false);
			guiPanel.currentPopup = null;
			return true;
		}
		if (this.craftingRequestAmount.isFocused())
        {
        	this.craftingRequestAmount.textboxKeyTyped(typedChar, keyCode);
    		return true;
        }
		return false;
	}

}
