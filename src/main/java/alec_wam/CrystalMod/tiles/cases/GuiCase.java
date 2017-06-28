package alec_wam.CrystalMod.tiles.cases;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCase extends GuiContainer {

	final InventoryPlayer playerInv;
	final TileEntityCaseBase caseTile;
	
	public GuiCase(EntityPlayer player, TileEntityCaseBase caseTile) {
		super(new ContainerCase(player.inventory, caseTile));
		playerInv = player.inventory;
		this.caseTile = caseTile;
		this.ySize = 172;
	}
	
	@Override
	public void initGui(){
		super.initGui();
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
	
	public final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/case.png");
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
