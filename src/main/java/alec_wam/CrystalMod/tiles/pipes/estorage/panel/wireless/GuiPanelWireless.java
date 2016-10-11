package alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.integration.jei.JEIUtil;
import alec_wam.CrystalMod.items.backpack.gui.GuiBackpack;
import alec_wam.CrystalMod.items.backpack.gui.GuiButtonHoverText;
import alec_wam.CrystalMod.items.backpack.gui.GuiButtonIcon;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketItemNBT;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient;
import alec_wam.CrystalMod.tiles.pipes.estorage.PacketEStorageAddItem;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.SortType;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.VScrollbar;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import mezz.jei.config.KeyBindings;
import mezz.jei.gui.Focus;
import mezz.jei.gui.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;

public class GuiPanelWireless extends GuiContainer implements IGuiScreen {

	final InventoryPlayer playerInv;
	final TileEntityWirelessPanel panel;
	final ItemStack stack;
	
	private int itemRow;
	
	private GuiTextField searchBar;
	private VScrollbar scrollbar;
	
	private boolean craftingPopup;
	private ItemStackData currentCraft;
	private GuiTextField craftingRequestAmount;
	
	protected VScrollbar draggingScrollbar;
	
	public GuiPanelWireless(InventoryPlayer player, TileEntityWirelessPanel pipe, ItemStack held, ContainerPanelWireless pan) {
		super(pan);
		this.stack = held;
		xSize = 232;
		ySize = 212;
		playerInv = player;
		this.panel = pipe;
	}
	
	public GuiPanelWireless(InventoryPlayer player, TileEntityWirelessPanel pipe, ItemStack held) {
		super(new ContainerPanelWireless(player, pipe));
		this.stack = held;
		xSize = 232;
		ySize = 212;
		playerInv = player;
		this.panel = pipe;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		
		Keyboard.enableRepeatEvents(true);
        searchBar = new GuiTextField(0, this.fontRendererObj, sx + getSearchBarX(), sy + getSearchBarY(), getSearchBarWidth(), 16);
        searchBar.setEnableBackgroundDrawing(false);
        if(stack !=null){
        	searchBar.setText(ItemNBTHelper.getString(stack, "SearchBarText", ""));
        }
        
        
        craftingRequestAmount = new GuiTextField(1, this.fontRendererObj, getCraftBoxX()+7, getCraftBoxY()+28, 78, 10);
        craftingRequestAmount.setMaxStringLength(16);
        craftingRequestAmount.setEnableBackgroundDrawing(false);
        craftingRequestAmount.setText("1");
        
        
        scrollbar = new VScrollbar(this, 212, 35, 74);
        scrollbar.adjustPosition();
		refreshButtons();
	}

	public void actionPerformed(GuiButton button){
		if(button.id == 0){
			if(stack !=null){
				int index = ItemNBTHelper.getInteger(stack, "SortType", 0);
				index++;
				if(index >=SortType.values().length){
					index = 0;
				}
				ItemNBTHelper.setInteger(stack, "SortType", index);
				CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(stack)));
				refreshButtons();
			}
		}
		if(button.id == 1){
			if(stack !=null){
				boolean old = ItemNBTHelper.getBoolean(stack, "JEISync", false);
				ItemNBTHelper.setBoolean(stack, "JEISync", !old);
				CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(stack)));
				refreshButtons();
			}
		}
	}
	
	public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        if (i != 0 && !craftingPopup)
        {
        	if(isShiftKeyDown()){
        		if(i > 0){
        			Slot slot = this.getSlotUnderMouse();
        			if(slot !=null){
        				if (slot.canTakeStack(mc.thePlayer))
        	            {
        	                if(panel !=null && panel.network !=null && slot.getStack() !=null){
        	                	ItemStack copy = slot.getStack().copy();
        	                	copy.stackSize = 1;
        	                	CrystalModNetwork.sendToServer(new PacketEStorageAddItem(3, slot.slotNumber, 1, EStorageNetwork.compressItem(new ItemStackData(copy, 0, BlockPos.ORIGIN, 0))));
        						return;
        	        		}
        	            }
        			}
        		}
        		if(i < 0){
        			int s = getNetworkSlot(x, y);
        			int fixednSlot = (s+itemRow*getItemsPerRow());
        			if(s > -1){
        				ItemStackData data = (fixednSlot < 0 || getDisplayItems().size() <= fixednSlot) ? null : getDisplayItems().get(fixednSlot);
        				if(data !=null && data.stack !=null){
        					int slot = -1;
							boolean found = false;
							slotSearch : for(int iS = 0; iS < mc.thePlayer.inventory.getSizeInventory(); iS++){
								if(mc.thePlayer.inventory.getStackInSlot(iS) !=null && mc.thePlayer.inventory.getStackInSlot(iS).stackSize < mc.thePlayer.inventory.getStackInSlot(iS).getMaxStackSize() && ItemUtil.canCombine(data.stack, mc.thePlayer.inventory.getStackInSlot(iS))){
									slot = iS;
									found = true;
									break slotSearch;
								}
							}
							if(found == false){
								slotSearch : for(int iS = 0; iS < mc.thePlayer.inventory.getSizeInventory(); iS++){
									if(mc.thePlayer.inventory.getStackInSlot(iS) == null){
										slot = iS;
										found = true;
										break slotSearch;
									}
								}
							}
							if(slot !=-1){
								CrystalModNetwork.sendToServer(new PacketEStorageAddItem(1, slot, 1, EStorageNetwork.compressItem(data)));
	    						return;
							}
    					}
        			}
        		}
        	}else{
        		this.scrollbar.mouseWheel(x, y, i);
        	}
        }
    }
	
	public void onGuiClosed(){
		super.onGuiClosed();
		if(stack !=null){
			ItemNBTHelper.setString(stack, "SearchBarText", searchBar.getText());
			CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(stack)));
		}
		Keyboard.enableRepeatEvents(false);
	}
	
	public void updateScreen(){
		super.updateScreen();
		searchBar.updateCursorCounter();
		if(craftingPopup)craftingRequestAmount.updateCursorCounter();
		else{
			if(JEIUtil.isInstalled() && ItemNBTHelper.getBoolean(stack, "JEISync", false)){
    			String text = JEIUtil.getFilterText();
    			if(!Strings.isNullOrEmpty(text))searchBar.setText(text);
    		}
		}
	}
	
	public void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		SortType current = stack !=null ? SortType.values()[ItemNBTHelper.getInteger(stack, "SortType", 0)] : SortType.NAME;
		int y = 0;
		if(current == SortType.NAME_REVERSE){
			y=14;
		}
		if(current == SortType.COUNT_REVERSE){
			y=14*2;
		}
		if(current == SortType.COUNT){
			y=14*3;
		}
		if(current == SortType.MOD){
			y=14*4;
		}
		if(current == SortType.MOD_REVERSE){
			y=14*5;
		}
		this.buttonList.add(new GuiButtonIcon(0, sx+9, sy+7, 14, 14, 134, y, GuiBackpack.WIDGETS, 14, 0));
		
		if(JEIUtil.isInstalled()){
			boolean sync = stack !=null ? ItemNBTHelper.getBoolean(stack, "JEISync", false) : false;
			String mode = sync ? "enabled" : "disabled";
			this.buttonList.add(new GuiButtonHoverText(1, sx+9, sy+7+16, 14, 14, 134, sync ? 14*6 : 14*7, GuiBackpack.WIDGETS, 14, 0, Lang.prefix+"gui.jeisync."+mode));
		}
	}

	public List<ItemStackData> getDisplayItems(){
		boolean safe = panel !=null && panel.network !=null && panel.network instanceof EStorageNetworkClient;
		if(safe){
			EStorageNetworkClient net = (EStorageNetworkClient) panel.network;
			SortType current = stack !=null ? SortType.values()[ItemNBTHelper.getInteger(stack, "SortType", 0)] : SortType.NAME;
			return net.getDisplayItems(searchBar.getText(), current);
		}
		return Lists.newArrayList();
	}
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		//Inside Search Bar
		if(isPointInRegion(getSearchBarX(), getSearchBarY(), getSearchBarWidth(), 16, mouseX, mouseY)){
			if(!this.searchBar.isFocused()){
				this.searchBar.setFocused(true);
			}
		}else{
			this.searchBar.setFocused(false);
		}
		
		if(this.craftingPopup){
			return;
		}
		
		int nslot = getNetworkSlot(mouseX, mouseY);
		int fixednSlot = (nslot+itemRow*getItemsPerRow());
		
		//Inside List
		if(nslot > -1){
			boolean safe = panel !=null && panel.network !=null && panel.network instanceof EStorageNetworkClient;
			if(safe){
				EntityPlayer player = CrystalMod.proxy.getClientPlayer();
				EStorageNetworkClient net = (EStorageNetworkClient) panel.network;
				
				if(player !=null & player.inventory.getItemStack() !=null  && (mouseButton == 0 || mouseButton == 1)){
					ItemStack copy = player.inventory.getItemStack().copy();
					if(mouseButton == 1){
						copy.stackSize = 1;
					}
					CrystalModNetwork.sendToServer(new PacketEStorageAddItem(0, -1, -1, EStorageNetwork.compressItem(new ItemStackData(copy, 0, BlockPos.ORIGIN, 0))));
					return;
				}
				ItemStackData data = (fixednSlot < 0 || getDisplayItems().size() <= fixednSlot) ? null : getDisplayItems().get(fixednSlot);
				if(data !=null && data.stack !=null){
					ItemStack dis = data.stack;
					if (player !=null && player.inventory.getItemStack() == null)
	                {
	                    if (player.capabilities.isCreativeMode && mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
	                    {
	                    	CrystalModNetwork.sendToServer(new PacketEStorageAddItem(2, -1, -1, EStorageNetwork.compressItem(data)));
	    					return;
	                    }
	                }
					if(mouseButton == 0 || mouseButton == 1){
						boolean craftable = false;
						search : for(ItemStackData data2 : net.craftingItems){
							if(data2.stack !=null && ItemUtil.canCombine(dis, data2.stack)){
								craftable = true;
								break search;
							}
						}
						if(craftable && (isCtrlKeyDown() || data.isCrafting) && mouseButton == 0){
							this.currentCraft = data;
							this.craftingRequestAmount.setEnabled(true);
							this.craftingRequestAmount.setFocused(true);
							this.craftingPopup = true;
							return;
						}
						
						int amount = mouseButton == 1 ? dis.getMaxStackSize()/2 : dis.getMaxStackSize();
						int slot = -1;
						if(isShiftKeyDown()){
							boolean found = false;
							slotSearch : for(int iS = 0; iS < player.inventory.getSizeInventory(); iS++){
								if(player.inventory.getStackInSlot(iS) !=null && player.inventory.getStackInSlot(iS).stackSize < player.inventory.getStackInSlot(iS).getMaxStackSize() && ItemUtil.canCombine(dis, player.inventory.getStackInSlot(iS))){
									slot = iS;
									found = true;
									break slotSearch;
								}
							}
							if(found == false){
								slotSearch : for(int iS = 0; iS < player.inventory.getSizeInventory(); iS++){
									if(player.inventory.getStackInSlot(iS) == null){
										slot = iS;
										found = true;
										break slotSearch;
									}
								}
							}
						}
						if(!data.isCrafting)CrystalModNetwork.sendToServer(new PacketEStorageAddItem(1, slot, amount, EStorageNetwork.compressItem(data)));
						return;
					}
				}
			}
		}
	}
	
	public int getNetworkSlot(int mouseX, int mouseY){
		if(isPointInRegion(getListX(), getListY(), getListWidth(), getListHeight(), mouseX, mouseY)){
			int mX = mouseX - this.guiLeft- getListX();
			int mY = mouseY - guiTop - getListY();
			int row = (mX/18);
			int col = (mY/18);
			int slot = row+(getItemsPerRow()*col);
			return slot;
		}
		return -1;
	}
	
	@Override
	  protected void mouseReleased(int x, int y, int button) {
	      if (draggingScrollbar != null) {
	          draggingScrollbar.mouseMovedOrUp(x, y, button);
	          draggingScrollbar = null;
	      }
	      super.mouseReleased(x, y, button);
	  }

	  @Override
	  protected void mouseClickMove(int x, int y, int button, long time) {
	    if (draggingScrollbar != null) {
	      draggingScrollbar.mouseClickMove(x, y, button, time);
	      return;
	    }
	    super.mouseClickMove(x, y, button, time);
	  }
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    final int xStart = (getListX()+1);
		int x = xStart;
		int y = getListY()+1;
		int row = itemRow;
		int stacksRendered = 0;
		for(int s = row*getItemsPerRow(); (s < getDisplayItems().size()); s++){
			ItemStackData data = getDisplayItems().get(s);
			ItemStack dis = data.stack;
			
			if(dis !=null){
				GlStateManager.pushMatrix();
	            RenderHelper.enableGUIStandardItemLighting();
	            GlStateManager.disableLighting();
	            GlStateManager.enableRescaleNormal();
	            GlStateManager.enableColorMaterial();
	            GlStateManager.enableLighting();
	            
	            
	            String stackSize;
	    		if (dis.stackSize == 1) {
	    			stackSize = "";
	    		} else if (dis.stackSize < 1000) {
	    			stackSize = dis.stackSize + "";
	    		} else if (dis.stackSize < 100000) {
	    			stackSize = dis.stackSize / 1000 + "K";
	    		} else if (dis.stackSize < 1000000) {
	    			stackSize = "0." + dis.stackSize / 100000+"M";
	    		} else {
	    			stackSize = dis.stackSize / 1000000 + "M";
	    		}
	    		if(data.isCrafting){
	    			stackSize = "Craft";
	    		}
	    		GlStateManager.pushMatrix();
	    		this.itemRender.zLevel = 100.0F;
	            this.itemRender.renderItemAndEffectIntoGUI(dis, x, y);
	            int offset = data.isCrafting ? 6 : 3;
	    		GlStateManager.translate(x+offset, y+offset, 0);
	    		double scale = data.isCrafting ? 0.6 : 0.8;
	    		GlStateManager.scale(scale, scale, 0);
	            this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, dis, 0, 0, stackSize);
	            GlStateManager.scale(1, 1, 0);
	            GlStateManager.popMatrix();
	            this.itemRender.zLevel = 0.0F;
	            GlStateManager.disableLighting();

	            GlStateManager.popMatrix();
	            GlStateManager.enableLighting();
	            GlStateManager.enableDepth();
	            RenderHelper.enableStandardItemLighting();
				
				
				
				
			}
			stacksRendered++;
			x+=18;
			if(stacksRendered%getItemsPerRow()==0){
				x = xStart;
				y+=18;
			}
			if(stacksRendered >=getMaxRenderCount())break;
		}
		
		int nslot = getNetworkSlot(mouseX, mouseY);
		int fixednSlot = (nslot+itemRow*getItemsPerRow());
		
		//Inside List
		if(nslot > -1 && !craftingPopup){
			ItemStackData data = (fixednSlot < 0 || getDisplayItems().size() <= fixednSlot) ? null : getDisplayItems().get(fixednSlot);
			if(data !=null && data.stack !=null){
				ItemStack disOrg = data.stack;
				if(disOrg !=null){
					ItemStack dis = disOrg.copy();
					ItemNBTHelper.setBoolean(dis, "DummyItem", true);
					GlStateManager.pushMatrix();
		            RenderHelper.enableGUIStandardItemLighting();
		            GlStateManager.disableLighting();
		            GlStateManager.enableRescaleNormal();
		            GlStateManager.enableColorMaterial();
		            this.renderToolTip(dis, mouseX-sx, mouseY-sy);

		            GlStateManager.popMatrix();
		            GlStateManager.enableLighting();
		            GlStateManager.enableDepth();
		            RenderHelper.enableStandardItemLighting();
				}
			}
		}
		
		

	    if(this.craftingPopup){
	    	GlStateManager.pushMatrix();
	    	GlStateManager.disableDepth();
	    	GlStateManager.disableLighting();
	    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/eStorage_panel_craftbox.png"));
		    drawTexturedModalRect(getCraftBoxX(), getCraftBoxY(), 0, 0, 90, 59);
	    	this.craftingRequestAmount.drawTextBox();
	    	
	    	ItemStack dis = this.currentCraft !=null ? this.currentCraft.stack : null;
			
			if(dis !=null){
				GlStateManager.pushMatrix();
	            RenderHelper.enableGUIStandardItemLighting();
	            GlStateManager.disableLighting();
	            GlStateManager.enableRescaleNormal();
	            GlStateManager.enableColorMaterial();
	            GlStateManager.enableLighting();
	            
	            
	            GlStateManager.pushMatrix();
	    		this.itemRender.zLevel = 100.0F;
	            this.itemRender.renderItemAndEffectIntoGUI(dis, getCraftBoxX()+5, getCraftBoxY()+5);
	            GlStateManager.popMatrix();
	            this.itemRender.zLevel = 0.0F;
	            GlStateManager.disableLighting();

	            GlStateManager.popMatrix();
	            GlStateManager.enableLighting();
	            GlStateManager.enableDepth();
	            RenderHelper.enableStandardItemLighting();
			}
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
	    }
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(getTexture()));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	    searchBar.drawTextBox();
	    
	    int max = getDisplayItems().size()/getItemsPerRow();
		if(((getDisplayItems().size()*1.0f)/(getItemsPerRow()*1.0f)) > max){
			max++;
		}
	    scrollbar.setScrollMax(Math.max(0, max-1));
	    if(itemRow != scrollbar.getScrollPos()) {
	    	itemRow = scrollbar.getScrollPos();
	    }
	    scrollbar.drawScrollbar(par2, par3);
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (int i = 0; i < this.buttonList.size(); i++)
        {
            GuiButton button = (GuiButton)this.buttonList.get(i);

            // Mouse is over the button
            if ((button instanceof GuiButtonHoverText) && button.mousePressed(this.mc, mouseX, mouseY) == true)
            {
                this.drawHoveringText(((GuiButtonHoverText)button).getHoverStrings(), mouseX, mouseY, this.fontRendererObj);
            }
        }
	}
	
	public String getTexture() {
		return "crystalmod:textures/gui/eStorage_panel.png";
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
		
		if (keyCode == 1) {
			if(this.craftingPopup){
				this.craftingPopup = false;
				this.craftingRequestAmount.setEnabled(false);
				this.craftingRequestAmount.setFocused(false);
				this.refreshButtons();
				return;
			}
			else if (searchBar.isFocused()) {
				searchBar.setFocused(false);
		        return;
			} else{
				this.mc.thePlayer.closeScreen();
				return;
			}
    	}
		
		if(!craftingPopup){
		if(!this.searchBar.isFocused()){
			if(keyCode == Keyboard.KEY_S && !craftingPopup){
				searchBar.setFocused(true);
				return;
			}
		}
		
		//Inside List
		int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		int s = getNetworkSlot(mouseX, mouseY);
		int fixednSlot = (s+itemRow*getItemsPerRow());
		if(s > -1){
			ItemStackData data = (fixednSlot < 0 || getDisplayItems().size() <= fixednSlot) ? null : getDisplayItems().get(fixednSlot);
			if(data !=null && data.stack !=null){
				ItemStack dis = data.stack;
				
				if(JEIUtil.isInstalled()){
					RecipesGui recipesGui = JEIUtil.getRecipesGui();
        			if(recipesGui !=null){
						if (keyCode == KeyBindings.showRecipe.getKeyCode()) {
							Focus<ItemStack> focus = new Focus<ItemStack>(dis);
							if (focus != null) {
								if (!GuiScreen.isShiftKeyDown()) {
									recipesGui.showRecipes(focus);
								} else {
									recipesGui.showUses(focus);
								}
								return;
							}
						} else if (keyCode == KeyBindings.showUses.getKeyCode()) {
							Focus<ItemStack> focus = new Focus<ItemStack>(dis);
							if (focus != null) {
								recipesGui.showUses(focus);
								return;
							}
						}
        			}
				}
				
				if (this.mc.thePlayer.inventory.getItemStack() == null)
			    {
					if (mc.thePlayer.capabilities.isCreativeMode && keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode())
                    {
                    	CrystalModNetwork.sendToServer(new PacketEStorageAddItem(2, -1, -1, EStorageNetwork.compressItem(data)));
    					return;
                    }
			        for (int i = 0; i < 9; ++i)
			        {
			            if (keyCode == this.mc.gameSettings.keyBindsHotbar[i].getKeyCode())
			            {
			            	int slot = i;
						
			            	if(slot !=-1 && (this.mc.thePlayer.inventory.getStackInSlot(i) == null || ItemUtil.canCombine(this.mc.thePlayer.inventory.getStackInSlot(i), dis))){
								CrystalModNetwork.sendToServer(new PacketEStorageAddItem(1, slot, dis.getMaxStackSize(), EStorageNetwork.compressItem(data)));
							}
							return;
						}
			        }
			    }
			}
		}
		
        if (this.searchBar.isFocused())
        {
        	if (!this.checkHotbarKeys(keyCode))
            {
        		this.searchBar.textboxKeyTyped(typedChar, keyCode);
        		if(JEIUtil.isInstalled() && ItemNBTHelper.getBoolean(stack, "JEISync", false)){
        			JEIUtil.setFilterText(searchBar.getText());
        		}
        		return;
            }
        }
		}else{
			if(keyCode == Keyboard.KEY_RETURN){
				if(currentCraft == null || currentCraft.stack == null)return;
				int current = 1;
				try{
					current = Integer.parseInt(craftingRequestAmount.getText());
				}catch(Exception e){
				}
				ItemStack copy = currentCraft.stack.copy();
				copy.stackSize = 1;
				
				ItemStackData data = new ItemStackData(copy, currentCraft.index, currentCraft.interPos, currentCraft.interDim);
				data.isCrafting = currentCraft.isCrafting;
				
				CrystalModNetwork.sendToServer(new PacketEStorageAddItem(4, -1, current, EStorageNetwork.compressItem(data)));
				this.craftingPopup = false;
				this.craftingRequestAmount.setText("1");
				this.craftingRequestAmount.setEnabled(false);
				this.craftingRequestAmount.setFocused(false);
				return;
			}
			if (this.craftingRequestAmount.isFocused())
	        {
	        	if (!this.checkHotbarKeys(keyCode))
	            {
	        		this.craftingRequestAmount.textboxKeyTyped(typedChar, keyCode);
	        		return;
	            }
	        }
		}
        super.keyTyped(typedChar, keyCode);
    }
	
	public int getItemsPerRow(){
		return 10;
	}
	
	public int getMaxRenderCount(){
		return 50;
	}
	
	public int getSearchBarX(){
		return 34;
	}
	
	public int getSearchBarY(){
		return 11;
	}
	
	public int getSearchBarWidth() {
		return 178;
	}
	
	public int getCraftBoxX(){
		return 73;
	}
	
	public int getCraftBoxY(){
		return 42;
	}
	
	public int getListX(){
		return 29;
	}
	
	public int getListY(){
		return 27;
	}
	
	public int getListWidth() {
		return 180;
	}
	
	public int getListHeight() {
		return 90;
	}
	
	@Override
	public int getGuiLeft() {
		return this.guiLeft;
	}

	@Override
	public int getGuiTop() {
		return this.guiTop;
	}
}
