package alec_wam.CrystalMod.tiles.pipes.estorage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.pipes.ContainerNormalPipe;
import alec_wam.CrystalMod.tiles.pipes.item.PacketPipe;
import alec_wam.CrystalMod.util.ItemStackTools;

public class GuiEStoragePipe extends GuiContainer {

	final InventoryPlayer playerInv;
	final TileEntityPipeEStorage pipe;
	private final EnumFacing dir;
	
	private int itemRow;
	
	public GuiEStoragePipe(InventoryPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir) {
		super(new ContainerNormalPipe());
		playerInv = player;
		this.pipe = pipe;
		this.dir = dir;
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
			pipe.setConnectionMode(dir, !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? pipe.getNextConnectionMode(dir) : pipe.getPreviousConnectionMode(dir));
			CrystalModNetwork.sendToServer(new PacketPipe(pipe, "CMode", dir, pipe.getConnectionMode(dir).name()));
			refreshButtons();
			return;
		}
		boolean safe = pipe.network !=null && pipe.network instanceof EStorageNetwork;
		if(safe){
			EStorageNetwork net = (EStorageNetwork)pipe.network;
			if(button.id == 1){
				if(this.itemRow > 0){
					itemRow--;
				}
			}
			if(button.id == 2){
				int max = net.interfaces.size()/6;
				if(((net.interfaces.size()*1.0f)/6.0f) > max){
					max++;
				}
				if(itemRow == 0){
					itemRow+=1;
					return;
				}else if(this.itemRow < max-1){
					itemRow+=1;
					return;
				}
			}
		}
	}
	
	public void updateScreen(){
		super.updateScreen();
	}
	
	private void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		this.buttonList.add(new GuiButton(0, sx+40, sy+2, 100, 10, pipe.getConnectionMode(dir).getUnlocalisedName()));
		this.buttonList.add(new GuiButton(1, sx+45+45+45+18, sy+30, 10, 10, "+"));
		this.buttonList.add(new GuiButton(2, sx+45+45+45+18, sy+40, 10, 10, "-"));
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		boolean safe = pipe.network !=null && pipe.network instanceof EStorageNetwork;
		if(safe){
			EStorageNetwork net = (EStorageNetwork)pipe.network;
			final int xStart = (35);
			int x = xStart;
			int y = 16;
			int row = itemRow;
			int stacksRendered = 0;
			for(int s = row*6; (s < net.interfaces.size()); s++){
				ItemStack dis = new ItemStack(ModBlocks.hddInterface);
				
				if(!ItemStackTools.isNullStack(dis)){
					GlStateManager.pushMatrix();
		            RenderHelper.enableGUIStandardItemLighting();
		            GlStateManager.disableLighting();
		            GlStateManager.enableRescaleNormal();
		            GlStateManager.enableColorMaterial();
		            GlStateManager.enableLighting();
		            this.itemRender.zLevel = 100.0F;
		            this.itemRender.renderItemAndEffectIntoGUI(dis, x, y);
		            
		            String stackSize;
		    		if (dis.stackSize == 1) {
		    			stackSize = "";
		    		} else if (dis.stackSize < 1000) {
		    			stackSize = dis.stackSize + "";
		    		} else if (dis.stackSize < 100000) {
		    			stackSize = dis.stackSize / 1000 + "K";
		    		} else if (dis.stackSize < 1000000) {
		    			stackSize = "0M" + dis.stackSize / 100000;
		    		} else {
		    			stackSize = dis.stackSize / 1000000 + "M";
		    		}
		            
		            this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, dis, x, y, stackSize);
		            this.itemRender.zLevel = 0.0F;
		            GlStateManager.disableLighting();

		            GlStateManager.popMatrix();
		            GlStateManager.enableLighting();
		            GlStateManager.enableDepth();
		            RenderHelper.enableStandardItemLighting();
					
					
					
					stacksRendered++;
					x+=18;
					if((s+1)%6==0){
						x = xStart;
						y+=18;
					}
				}
				if(stacksRendered >=18)break;
			}
			
			x = xStart;
			y = 16;
			stacksRendered = 0;
			for(int s = row*6; (s < net.interfaces.size()); s++){
				ItemStack disOrg = new ItemStack(ModBlocks.hddInterface);
				if(disOrg !=null){
					GlStateManager.pushMatrix();
		            RenderHelper.enableGUIStandardItemLighting();
		            GlStateManager.disableLighting();
		            GlStateManager.enableRescaleNormal();
		            GlStateManager.enableColorMaterial();

		            /*if (this.isPointInRegion(x, y, 18, 18, mouseX, mouseY) && dis != null)
		            {
		            	List<String> list = Lists.newArrayList();
		            	drawHoveringText(list, mouseX-sx, mouseY-sy, fontRendererObj);
		            }*/

		            GlStateManager.popMatrix();
		            GlStateManager.enableLighting();
		            GlStateManager.enableDepth();
		            RenderHelper.enableStandardItemLighting();
					
					
					
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
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/hddInterface.png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
