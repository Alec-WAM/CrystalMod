package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiHDDInterface extends GuiContainer {

	final InventoryPlayer playerInv;
	final TileEntityHDDInterface inter;

	private int itemRow;
	private int selectedSlot = -1;
	
	public GuiHDDInterface(InventoryPlayer player, TileEntityHDDInterface inter) {
		super(new ContainerHDDInterface(player, inter));
		playerInv = player;
		this.inter = inter;
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
		
		boolean safe = !ItemStackTools.isNullStack(inter.getStackInSlot(0));
		if(safe){
			ItemStack hddStack = inter.getStackInSlot(0);
			if(button.id == 0){
				if(this.itemRow > 0){
					itemRow--;
				}
			}
			if(button.id == 1){
				int max = ItemHDD.getItemCount(hddStack)/6;
				if(((ItemHDD.getItemCount(hddStack)*1.0f)/6.0f) > max){
					max++;
				}
				if(itemRow == 0 && itemRow > 1){
					itemRow+=1;
					return;
				}else if(this.itemRow < max-1){
					itemRow+=1;
					return;
				}
			}
			if(button.id == 2){
				inter.dumpIndex = selectedSlot;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("DumpIndex", selectedSlot);
				CrystalModNetwork.sendToServer(new PacketTileMessage(inter.getPos(), "DumpIndex", nbt));
			}
			if(button.id == 3){
				inter.dumpIndex = -1;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("DumpIndex", -1);
				CrystalModNetwork.sendToServer(new PacketTileMessage(inter.getPos(), "DumpIndex", nbt));
			}
			if(button.id == 4){
				inter.setPriority(inter.getPriority()+1);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Priority", inter.getPriority());
				CrystalModNetwork.sendToServer(new PacketTileMessage(inter.getPos(), "Priority", nbt));
			}
			if(button.id == 5){
				inter.setPriority(inter.getPriority()-1);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Priority", inter.getPriority());
				CrystalModNetwork.sendToServer(new PacketTileMessage(inter.getPos(), "Priority", nbt));
			}
		}
	}
	
	public void updateScreen(){
		super.updateScreen();
		if(ItemStackTools.isNullStack(this.inter.getStackInSlot(0))){
			itemRow = 0;
			setSlot(-1);
		}else{
			if(selectedSlot > -1){
				ItemStack stack = ItemHDD.getItem(inter.getStackInSlot(0), selectedSlot);
				if(ItemStackTools.isNullStack(stack)){
					setSlot(-1);
				}
			}
		}
		
	}
	
	public void setSlot(int slot){
		this.selectedSlot = slot;
		refreshButtons();
	}
	
	private void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		
		this.buttonList.add(new GuiButton(0, sx+45+45+45+18, sy+30, 10, 10, "+"));
		this.buttonList.add(new GuiButton(1, sx+45+45+45+18, sy+40, 10, 10, "-"));
		
		if(selectedSlot > -1){
			if(inter.dumpIndex == -1)this.buttonList.add(new GuiButton(2, sx+45+45+45+18, sy+10, 10, 10, "Dump"));
			else this.buttonList.add(new GuiButton(3, sx+45+45+45+18, sy+20, 10, 10, "Cancel"));
		}
		
		this.buttonList.add(new GuiButton(4, sx+45+45+45+18, sy+60, 10, 10, "+"));
		this.buttonList.add(new GuiButton(5, sx+45+45+45+18, sy+70, 10, 10, "-"));
	}
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		//Inside List
		if(isPointInRegion(28, 14, 122, 59, mouseX, mouseY)){
			boolean safe = !ItemStackTools.isNullStack(inter.getStackInSlot(0));
			if(safe){
				final int xStart = (35);
				int x = xStart;
				int y = 16;
				int row = itemRow;
				int stacksRendered = 0;
				ItemStack hddStack = inter.getStackInSlot(0);
				for(int s = row*6; (s < ItemHDD.getItemLimit(hddStack)); s++){
					ItemStack dis = ItemHDD.getItem(hddStack, s);
					
					if(!ItemStackTools.isNullStack(dis)){
						if(isPointInRegion(x, y, 18, 18, mouseX, mouseY)){
							if(s == selectedSlot){
								setSlot(-1);
							}else{
								setSlot(s);
							}
							break;
						}
						stacksRendered++;
						x+=18;
						if((s+1)%6==0){
							x = xStart;
							y+=18;
						}
					}
					if(stacksRendered >=18)break;
				}
			}
		}
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    boolean safe = !ItemStackTools.isNullStack(inter.getStackInSlot(0));
		if(safe){
			final int xStart = (35);
			int x = xStart;
			int y = 16;
			int row = itemRow;
			int stacksRendered = 0;
			ItemStack hddStack = inter.getStackInSlot(0);
			for(int s = row*6; (s < ItemHDD.getItemLimit(hddStack)); s++){
				ItemStack dis = ItemHDD.getItem(hddStack, s);
				if(ItemStackTools.isNullStack(dis))continue;
				if(s == selectedSlot){
					GlStateManager.pushMatrix();
					GlStateManager.disableLighting();
	                GlStateManager.disableDepth();
	                GlStateManager.colorMask(true, true, true, false);
	                int color = Color.GREEN.getRGB();
	                this.drawGradientRect(x-1, y-1, x+17, y, color, color);
	                this.drawGradientRect(x-1, y+16, x+17, y+17, color, color);
	                this.drawGradientRect(x-1, y-1, x, y+17, color, color);
	                this.drawGradientRect(x+16, y-1, x+17, y+17, color, color);
	                GlStateManager.colorMask(true, true, true, true);
	                GlStateManager.enableLighting();
	                GlStateManager.enableDepth();
					GlStateManager.popMatrix();
				}
				
				
				GlStateManager.pushMatrix();
	            RenderHelper.enableGUIStandardItemLighting();
	            GlStateManager.disableLighting();
	            GlStateManager.enableRescaleNormal();
	            GlStateManager.enableColorMaterial();
	            GlStateManager.enableLighting();
	            this.itemRender.zLevel = 100.0F;
	            this.itemRender.renderItemAndEffectIntoGUI(dis, x, y);
	            
	            String stackSize = GuiPanel.getStackSize(dis);
	            
	            this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, dis, x, y, stackSize);
	            this.itemRender.zLevel = 0.0F;
	            GlStateManager.disableLighting();

	            GlStateManager.popMatrix();
	            GlStateManager.enableLighting();
	            GlStateManager.enableDepth();
	            RenderHelper.enableStandardItemLighting();
	            
	            stacksRendered++;
				x+=18;
				if(stacksRendered%6==0){
					x = xStart;
					y+=18;
				}
				if(stacksRendered >=18)break;
			}
			
			x = xStart;
			y = 16;
			stacksRendered = 0;
			for(int s = row*6; (s < ItemHDD.getItemLimit(hddStack)); s++){
				ItemStack disOrg = ItemHDD.getItem(hddStack, s);
				if(!ItemStackTools.isNullStack(disOrg)){
					ItemStack dis = disOrg.copy();
					ItemNBTHelper.setBoolean(dis, "DummyItem", true);
					GlStateManager.pushMatrix();
		            RenderHelper.enableGUIStandardItemLighting();
		            GlStateManager.disableLighting();
		            GlStateManager.enableRescaleNormal();
		            GlStateManager.enableColorMaterial();

		            if (this.isPointInRegion(x, y, 18, 18, mouseX, mouseY) && dis != null)
		            {
		            	this.renderToolTip(dis, mouseX-sx, mouseY-sy);
		            }

		            GlStateManager.popMatrix();
		            GlStateManager.enableLighting();
		            GlStateManager.enableDepth();
		            RenderHelper.enableStandardItemLighting();
		            stacksRendered++;
					x+=18;
					if(stacksRendered%6==0){
						x = xStart;
						y+=18;
					}
					if(stacksRendered >=18)break;
				}
				
			}
		}
		
		if(this.fontRendererObj !=null){
			this.fontRendererObj.drawString(""+this.inter.getPriority(), 45+45+45+18+10, 65, 0);
		}
		
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/hddInterface.png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
