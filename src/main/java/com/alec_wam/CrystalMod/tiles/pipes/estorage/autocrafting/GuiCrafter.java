package com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCrafter extends GuiContainer {

	final InventoryPlayer playerInv;
	final TileCrafter crafter;
	
	public GuiCrafter(EntityPlayer player, TileCrafter array) {
		super(new ContainerCrafter(player, array));
		playerInv = player.inventory;
		this.crafter = array;
	}
	
	@Override
	public void initGui(){
		super.initGui();
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	    
	    this.fontRendererObj.drawString("Facing: "+this.crafter.getDirection().getName(), 16, 10, 0);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/eStorage_crafting_array.png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
