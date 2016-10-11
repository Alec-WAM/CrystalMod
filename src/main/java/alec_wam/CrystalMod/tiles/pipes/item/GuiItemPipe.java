package alec_wam.CrystalMod.tiles.pipes.item;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.item.filters.CameraFilterInventory;
import alec_wam.CrystalMod.tiles.pipes.item.filters.FilterInventory;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ModLogger;

public class GuiItemPipe extends GuiContainer {

	final InventoryPlayer playerInv;
	final TileEntityPipeItem pipe;
	private final EnumFacing dir;
	private int cameraFilterRow = 0;

	private final ContainerItemPipe container;
	private boolean filter = false;
	
	public GuiItemPipe(InventoryPlayer player, TileEntityPipeItem pipe, EnumFacing dir) {
		super(new ContainerItemPipe(player, pipe, dir));
		//this.xSize = 176;
        //this.ySize = 169;
		container = (ContainerItemPipe)inventorySlots;
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
			sendPipeMessage("CMode", pipe.getConnectionMode(dir).name());
			refreshButtons();
			return;
		}
		if(!filter){
			if(button.id == 1){
				pipe.setOutputPriority(dir, pipe.getOutputPriority(dir)+1);
				sendPipeMessage("Pri", "+");
				refreshButtons();
				return;
			}
			if(button.id == 2){
				pipe.setOutputPriority(dir, pipe.getOutputPriority(dir)-1);
				sendPipeMessage("Pri", "-");
				refreshButtons();
				return;
			}
			if(button.id == 3){
				boolean feed = !pipe.isSelfFeedEnabled(dir);
				pipe.setSelfFeedEnabled(dir, feed);
				sendPipeMessage("SelfFeed", ""+feed);
				refreshButtons();
				return;
			}
			if(button.id == 4){
				boolean robin = !pipe.isRoundRobinEnabled(dir);
				pipe.setRoundRobinEnabled(dir, robin);
				sendPipeMessage("RoundRobin", ""+robin);
				refreshButtons();
				return;
			}
			if(button.id == 5){
				if(this.pipe.getFilter(dir).getStackInSlot(0) !=null){
					filter = true;
					this.container.setGhostVisible(filter);
					refreshButtons();
					sendPipeMessage("FilterVis", ""+filter);
				}
				return;
			}
			if(button.id == 6){
				final RedstoneMode next = pipe.getNextRedstoneMode(dir);
				pipe.setRedstoneMode(next, dir);
				sendPipeMessage("RMode", next.name());
				refreshButtons();
				return;
			}
		}
		if(button.id == 5){
			filter = true;
			this.container.setGhostVisible(filter);
			refreshButtons();
			sendPipeMessage("FilterVis", ""+filter);
			return;
		}
		if(button.id == 10){
			filter = false;
			this.container.setGhostVisible(filter);
			sendPipeMessage("FilterVis", ""+false);
			refreshButtons();
			return;
		}
		if(filter){
			
			boolean safe = pipe.getFilter(dir).getStackInSlot(0) !=null;
			if(safe){
				ItemStack filterStack = pipe.getFilter(dir).getStackInSlot(0);
				if(filterStack.getMetadata() == FilterType.NORMAL.ordinal()){
					if(button.id == 6){
						boolean black = !ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
						ItemNBTHelper.setBoolean(filterStack, "BlackList", black);
						sendPipeMessage("FilterSetBlack", ""+black);
						refreshButtons();
						return;
					}
					if(button.id == 7){
						boolean meta = !ItemNBTHelper.getBoolean(filterStack, "MetaMatch", true);
						ItemNBTHelper.setBoolean(filterStack, "MetaMatch", meta);
						sendPipeMessage("FilterSetMeta", ""+meta);
						refreshButtons();
						return;
					}
					if(button.id == 8){
						boolean nbtMatch = !ItemNBTHelper.getBoolean(filterStack, "NBTMatch", true);
						ItemNBTHelper.setBoolean(filterStack, "NBTMatch", nbtMatch);
						sendPipeMessage("FilterSetNBTMatch", ""+nbtMatch);
						refreshButtons();
						return;
					}
					if(button.id == 9){
						boolean ore = !ItemNBTHelper.getBoolean(filterStack, "OreMatch", false);
						ItemNBTHelper.setBoolean(filterStack, "OreMatch", ore);
						sendPipeMessage("FilterSetOre", ""+ore);
						refreshButtons();
						return;
					}
				}else if(filterStack.getMetadata() == FilterType.MOD.ordinal()){
					if(button.id == 6){
						boolean black = !ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
						ItemNBTHelper.setBoolean(filterStack, "BlackList", black);
						sendPipeMessage("FilterSetBlack", ""+black);
						refreshButtons();
						return;
					}
				}else if(filterStack.getMetadata() == FilterType.CAMERA.ordinal()){
					if(button.id == 6){
						pipe.scanInventory(dir);
						sendPipeMessage("FilterScanInv", "");
						refreshButtons();
						return;
					}
					if(button.id == 7){
						if(this.cameraFilterRow > 0){
							cameraFilterRow--;
						}
						refreshButtons();
					}
					if(button.id == 8){
						CameraFilterInventory inv = new CameraFilterInventory(filterStack, "");
						int max = inv.getSizeInventory()/6;
						if(((inv.getSizeInventory()*1.0f)/6.0f) > max){
							max++;
						}
						if(cameraFilterRow == 0){
							cameraFilterRow+=1;
							return;
						}else if(this.cameraFilterRow < max-1){
							cameraFilterRow+=1;
							return;
						}
					}
					if(button.id == 9){
						boolean black = !ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
						ItemNBTHelper.setBoolean(filterStack, "BlackList", black);
						sendPipeMessage("FilterSetBlack", ""+black);
						refreshButtons();
						return;
					}
					if(button.id == 11){
						boolean meta = !ItemNBTHelper.getBoolean(filterStack, "MetaMatch", true);
						ItemNBTHelper.setBoolean(filterStack, "MetaMatch", meta);
						sendPipeMessage("FilterSetMeta", ""+meta);
						refreshButtons();
						return;
					}
					if(button.id == 12){
						boolean nbtMatch = !ItemNBTHelper.getBoolean(filterStack, "NBTMatch", true);
						ItemNBTHelper.setBoolean(filterStack, "NBTMatch", nbtMatch);
						sendPipeMessage("FilterSetNBTMatch", ""+nbtMatch);
						refreshButtons();
						return;
					}
					if(button.id == 13){
						boolean ore = !ItemNBTHelper.getBoolean(filterStack, "OreMatch", false);
						ItemNBTHelper.setBoolean(filterStack, "OreMatch", ore);
						sendPipeMessage("FilterSetOre", ""+ore);
						refreshButtons();
						return;
					}
				}
			}
		}
	}
	
	public void sendPipeMessage(String type, String data){
		CrystalModNetwork.sendToServer(new PacketPipe(pipe, type, dir, data));
	}
	
	private void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		if(!filter)this.buttonList.add(new GuiButton(0, sx+40, sy+2, 100, 10, pipe.getConnectionMode(dir).getUnlocalisedName()));
		
		if(this.pipe.getConnectionMode(dir) !=ConnectionMode.DISABLED){
			if(filter == false){
				if(this.pipe.getConnectionMode(dir) == ConnectionMode.IN_OUT || this.pipe.getConnectionMode(dir) == ConnectionMode.OUTPUT){
					this.buttonList.add(new GuiButton(1, sx+28, sy+14, 10, 8, "+"));
					this.buttonList.add(new GuiButton(2, sx+28, sy+23, 10, 8, "-"));
				}
				if(this.pipe.getConnectionMode(dir) == ConnectionMode.IN_OUT){
					this.buttonList.add(new GuiButton(3, sx+90, sy+36, 60, 12, this.pipe.isSelfFeedEnabled(dir) ? "Enabled" : "Disabled"));
				}
				if(this.pipe.getConnectionMode(dir) == ConnectionMode.IN_OUT || this.pipe.getConnectionMode(dir) == ConnectionMode.INPUT){
					int y = this.pipe.getConnectionMode(dir) == ConnectionMode.IN_OUT ? sy+56 : sy+16;
					this.buttonList.add(new GuiButton(4, sx+90, y, 60, 12, this.pipe.isRoundRobinEnabled(dir) ? "Enabled" : "Disabled"));
				}
				RedstoneMode mode = pipe.redstoneSettings.containsKey(dir) ? pipe.redstoneSettings.get(dir) : RedstoneMode.ON;
				this.buttonList.add(new GuiButton(6, sx+8+150, sy+5, 20, 8, mode.name()));
			}
			if(!filter)this.buttonList.add(new GuiButton(5, sx+8, sy+35, 12, 12, "F"));
			if(filter)this.buttonList.add(new GuiButton(10, sx+8+130, sy+5, 12, 12, "X"));
			if(filter == true){
				boolean safe = pipe.getFilter(dir).getStackInSlot(0) !=null;
				if(safe){
					ItemStack filterStack = pipe.getFilter(dir).getStackInSlot(0);
					if(filterStack.getMetadata() == FilterType.NORMAL.ordinal()){
						boolean black = ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
						this.buttonList.add(new GuiButton(6, sx+45+(45/2), sy+2, 45, 10, (black ? "Block" : "Allow")));
						boolean meta = ItemNBTHelper.getBoolean(filterStack, "MetaMatch", true);
						this.buttonList.add(new GuiButton(7, sx+45-(45/2), sy+50+12, 45, 12, (meta ? TextFormatting.GREEN : TextFormatting.RED)+"Meta"));
						boolean nbtMatch = ItemNBTHelper.getBoolean(filterStack, "NBTMatch", true);
						this.buttonList.add(new GuiButton(8, sx+45+45+(45/2)-1, sy+50+12, 45, 12, (nbtMatch ? TextFormatting.GREEN : TextFormatting.RED)+"NBT"));
						boolean oreMatch = ItemNBTHelper.getBoolean(filterStack, "OreMatch", false);
						this.buttonList.add(new GuiButton(9, sx+45+(45/2), sy+50+12, 45, 12, (oreMatch ? TextFormatting.GREEN : TextFormatting.RED)+"Ore"));
					}else if(filterStack.getMetadata() == FilterType.MOD.ordinal()){
						boolean black = ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
						this.buttonList.add(new GuiButton(6, sx+45+(45/2), sy+2, 45, 10, (black ? "Block" : "Allow")));
					}else if(filterStack.getMetadata() == FilterType.CAMERA.ordinal()){
						this.buttonList.add(new GuiButton(6, sx+45+(45/2), sy+2, 45, 10, "Scan"));
						this.buttonList.add(new GuiButton(7, sx+45+45+45+18, sy+30, 10, 10, "+"));
						this.buttonList.add(new GuiButton(8, sx+45+45+45+18, sy+40, 10, 10, "-"));
						
						boolean black = ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
						this.buttonList.add(new GuiButton(9, sx+21, sy+50+23, 35, 10, (black ? "Block" : "Allow")));
						boolean meta = ItemNBTHelper.getBoolean(filterStack, "MetaMatch", true);
						this.buttonList.add(new GuiButton(11, sx+45+10, sy+50+23, 35, 10, (meta ? TextFormatting.GREEN : TextFormatting.RED)+"Meta"));
						boolean nbtMatch = ItemNBTHelper.getBoolean(filterStack, "NBTMatch", true);
						this.buttonList.add(new GuiButton(12, sx+45+44, sy+50+23, 35, 10, (nbtMatch ? TextFormatting.GREEN : TextFormatting.RED)+"NBT"));
						boolean oreMatch = ItemNBTHelper.getBoolean(filterStack, "OreMatch", false);
						this.buttonList.add(new GuiButton(13, sx+45+44+34, sy+50+23, 35, 10, (oreMatch ? TextFormatting.GREEN : TextFormatting.RED)+"Ore"));
					}
				}
			}
		}
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    if(!filter){
		    if(this.pipe.getConnectionMode(dir) == ConnectionMode.IN_OUT || this.pipe.getConnectionMode(dir) == ConnectionMode.OUTPUT)this.fontRendererObj.drawString("Priority: "+pipe.getOutputPriority(dir), (40), 20, Color.CYAN.getRGB());
			if(this.pipe.getConnectionMode(dir) == ConnectionMode.IN_OUT)this.fontRendererObj.drawString("Self Feed", (34), 39, Color.CYAN.getRGB());
			if(this.pipe.getConnectionMode(dir) == ConnectionMode.IN_OUT || this.pipe.getConnectionMode(dir) == ConnectionMode.INPUT){
				int y = this.pipe.getConnectionMode(dir) == ConnectionMode.IN_OUT ? 59 : 18;
				this.fontRendererObj.drawString("Round Robin", (29), y, Color.CYAN.getRGB());
			}
	    }else{
	    	boolean safe = pipe.getFilter(dir).getStackInSlot(0) !=null;
			if(safe){
				ItemStack filterStack = pipe.getFilter(dir).getStackInSlot(0);
				if(filterStack.getMetadata() == FilterType.MOD.ordinal()){
					FilterInventory inv = new FilterInventory(filterStack, 3, "");
					if(inv.getStackInSlot(0) !=null){
						ResourceLocation resourceInput = Item.REGISTRY.getNameForObject(inv.getStackInSlot(0).getItem());
		    	        String modIDInput = resourceInput.getResourceDomain();
						this.fontRendererObj.drawString("Mod: "+modIDInput, (45+18+5), 14+5, 0);
					}
					if(inv.getStackInSlot(1) !=null){
						ResourceLocation resourceInput = Item.REGISTRY.getNameForObject(inv.getStackInSlot(1).getItem());
		    	        String modIDInput = resourceInput.getResourceDomain();
						this.fontRendererObj.drawString("Mod: "+modIDInput, (45+18+5), 14+5+18, 0);
					}
					if(inv.getStackInSlot(2) !=null){
						ResourceLocation resourceInput = Item.REGISTRY.getNameForObject(inv.getStackInSlot(2).getItem());
		    	        String modIDInput = resourceInput.getResourceDomain();
						this.fontRendererObj.drawString("Mod: "+modIDInput, (45+18+5), 14+5+36, 0);
					}
				}else if(filterStack.getMetadata() == FilterType.CAMERA.ordinal()){
					CameraFilterInventory inv = new CameraFilterInventory(filterStack, "");
					final int xStart = (35);
					int x = xStart;
					int y = 16;
					int row = cameraFilterRow;
					int stacksRendered = 0;
					
					for(int s = row*6; (s < inv.getSizeInventory()); s++){
						ItemStack dis = inv.getStackInSlot(s);
						if(dis !=null){
							
							GlStateManager.pushMatrix();
				            RenderHelper.enableGUIStandardItemLighting();
				            GlStateManager.disableLighting();
				            GlStateManager.enableRescaleNormal();
				            GlStateManager.enableColorMaterial();
				            GlStateManager.enableLighting();
				            this.itemRender.zLevel = 100.0F;
				            this.itemRender.renderItemAndEffectIntoGUI(dis, x, y);
				            this.itemRender.renderItemOverlays(this.fontRendererObj, dis, x, y);
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
					for(int s = row*6; (s < inv.getSizeInventory()); s++){
						ItemStack dis = inv.getStackInSlot(s);
						if(dis !=null){
							
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
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;

	    String type = "";
	    if(filter && pipe.getFilter(dir).getStackInSlot(0) !=null){
	    	ItemStack filterStack = pipe.getFilter(dir).getStackInSlot(0);
	    	if(filterStack.getMetadata() == FilterType.NORMAL.ordinal()){
	    		type = "_filter";
	    	}
	    	if(filterStack.getMetadata() == FilterType.MOD.ordinal()){
	    		type = "_filter_mod";
	    	}
	    	if(filterStack.getMetadata() == FilterType.CAMERA.ordinal()){
	    		type = "_filter_camera";
	    	}
	    }
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/pipe"+type+".png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
