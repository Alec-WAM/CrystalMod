package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiHDDArray extends GuiContainer {

	final InventoryPlayer playerInv;
	final TileHDDArray array;
	
	public GuiHDDArray(InventoryPlayer player, TileHDDArray array) {
		super(new ContainerHDDArray(player, array));
		playerInv = player;
		this.array = array;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		refreshButtons();
	}
	
	public void actionPerformed(GuiButton button){
		if(button.id == 0){
			array.setPriority(array.getPriority()+1);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Priority", array.getPriority());
			CrystalModNetwork.sendToServer(new PacketTileMessage(array.getPos(), "Priority", nbt));
		}
		if(button.id == 1){
			array.setPriority(array.getPriority()-1);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Priority", array.getPriority());
			CrystalModNetwork.sendToServer(new PacketTileMessage(array.getPos(), "Priority", nbt));
		}
	}
	
	public void updateScreen(){
		super.updateScreen();
	}
	
	private void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		this.buttonList.add(new GuiButton(0, sx+45+45+45+18, sy+60, 10, 10, "+"));
		this.buttonList.add(new GuiButton(1, sx+45+45+45+18, sy+70, 10, 10, "-"));
	}
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if(this.fontRendererObj !=null){
			this.fontRendererObj.drawString(""+this.array.getPriority(), 45+45+45+18+10, 65, 0);
		}
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/hddArray.png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
