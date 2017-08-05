package alec_wam.CrystalMod.tiles.pipes.estorage.security;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiSecurityController extends GuiContainer {

	final InventoryPlayer playerInv;
	final TileSecurityController controller;
	
	public GuiSecurityController(EntityPlayer player, TileSecurityController controller) {
		super(new ContainerSecurityController(player, controller));
		playerInv = player.inventory;
		this.controller = controller;
	}
	
	@Override
	public void initGui(){
		super.initGui();
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.fontRendererObj.drawString(ModBlocks.securityController.getLocalizedName(), 8, 8, 4210752);	
    }
	
	public final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/eStorage_security.png");
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
