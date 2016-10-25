package alec_wam.CrystalMod.tiles.pipes.estorage.panel;

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
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.PacketEStorageAddItem;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.SortType;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.ViewType;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.VScrollbar;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.GuiPanelCrafting;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.popup.CraftingAmountPopup;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.popup.Popup;
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
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;

public class GuiPanel extends GuiContainer implements IGuiScreen, INetworkGui  {

	final InventoryPlayer playerInv;
	protected IPanelSource panel;
	
	private int itemRow;
	
	private GuiTextField searchBar;
	private VScrollbar scrollbar;
	
	public Popup currentPopup;
	
	protected VScrollbar draggingScrollbar;
	
	public GuiPanel(InventoryPlayer player, IPanelSource pipe, ContainerPanel pan) {
		super(pan);
		xSize = 232;
		ySize = 212;
		playerInv = player;
		this.panel = pipe;
	}
	
	public GuiPanel(InventoryPlayer player, IPanelSource pipe) {
		super(new ContainerPanel(player, pipe));
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
        searchBar.setText(panel.getSearchBar() == null ? "" : panel.getSearchBar());
        
        scrollbar = new VScrollbar(this, getScrollBarX(), getScrollBarY(), getScrollBarHeight());
      	scrollbar.adjustPosition();
		refreshButtons();
	}

	public int getScrollBarX() {
		return 212;
	}
	
	public int getScrollBarY() {
		return (this instanceof GuiPanelCrafting) ? 28 : 35;
	}
	
	public int getScrollBarHeight() {
		return (this instanceof GuiPanelCrafting) ? 70 : 74;
	}

	public void actionPerformed(GuiButton button){
		if(button.id == 0){
			if(panel !=null){
				int index = panel.getSortType().ordinal();
				index++;
				if(index >=SortType.values().length){
					index = 0;
				}
				panel.setSortType(SortType.values()[index]);
				refreshButtons();
			}
		}
		if(button.id == 1){
			if(panel !=null){
				int index = panel.getViewType().ordinal();
				index++;
				if(index >=ViewType.values().length){
					index = 0;
				}
				panel.setViewType(ViewType.values()[index]);
				refreshButtons();
			}
		}
		if(button.id == 2){
			if(panel !=null){
				boolean old = panel.getJEISync();
				panel.setJEISync(!old);
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
        if (i != 0 && currentPopup == null)
        {
        	if(isShiftKeyDown()){
        		if(i > 0){
        			Slot slot = this.getSlotUnderMouse();
        			if(slot !=null){
        				if (slot.canTakeStack(mc.thePlayer))
        	            {
        	                if(panel.getNetwork() !=null && slot.getStack() !=null){
        	                	ItemStack copy = slot.getStack().copy();
        	                	copy.stackSize = 1;
        	                	try {
        							CrystalModNetwork.sendToServer(new PacketEStorageAddItem(3, slot.slotNumber, 1, EStorageNetwork.compressItem(new ItemStackData(copy, BlockPos.ORIGIN, 0))));
        						} catch (Exception e) {
        							e.printStackTrace();
        						}
        	                	return;
        	        		}
        	            }
        			}
        		}
        		if(i < 0){
        			int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
        	        int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
        			int s = getNetworkSlot(mouseX, mouseY);
        			int fixednSlot = (s+getItemRow()*getItemsPerRow());
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
	    						sendUpdate(1, slot, 1, data);
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
	
	public void sendUpdate(int type, int slot, int amount, ItemStackData data){
		try {
			CrystalModNetwork.sendToServer(new PacketEStorageAddItem(type, slot, amount, EStorageNetwork.compressItem(data)));
		} catch (Exception e) {
		}
	}
	
	public void onGuiClosed(){
		super.onGuiClosed();
		this.panel.setSearchBar(searchBar.getText());
		Keyboard.enableRepeatEvents(false);
	}
	
	public void updateScreen(){
		super.updateScreen();
		
		searchBar.updateCursorCounter();
		if(currentPopup !=null){
			currentPopup.update(this);
		}
		else{
			if(JEIUtil.isInstalled() && panel.getJEISync()){
    			String text = JEIUtil.getFilterText();
    			if(!Strings.isNullOrEmpty(text))searchBar.setText(text);
    		}
		}
	}
	
	public void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		SortType currentSort = panel !=null ? panel.getSortType() : SortType.NAME;
		ViewType currentView = panel !=null ? panel.getViewType() : ViewType.BOTH;
		int y = 0;
		if(currentSort == SortType.NAME_REVERSE){
			y=14;
		}
		if(currentSort == SortType.COUNT_REVERSE){
			y=14*2;
		}
		if(currentSort == SortType.COUNT){
			y=14*3;
		}
		if(currentSort == SortType.MOD){
			y=14*4;
		}
		if(currentSort == SortType.MOD_REVERSE){
			y=14*5;
		}
		this.buttonList.add(new GuiButtonHoverText(0, sx+9, sy+7, 14, 14, 134, y, GuiBackpack.WIDGETS, 14, 0, Lang.prefix+"gui.sortType."+currentSort.name().toLowerCase()));
		
		if(currentView == ViewType.BOTH){
			y=14*8;
		}
		if(currentView == ViewType.ITEMS){
			y=14*9;
		}
		if(currentView == ViewType.PATTERNS){
			y=14*10;
		}
		
		this.buttonList.add(new GuiButtonHoverText(1, sx+9, sy+7+16, 14, 14, 134, y, GuiBackpack.WIDGETS, 14, 0, Lang.prefix+"gui.viewType."+currentView.name().toLowerCase()));
		
		if(JEIUtil.isInstalled()){
			boolean sync = panel !=null ? panel.getJEISync() : false;
			String mode = sync ? "enabled" : "disabled";
			this.buttonList.add(new GuiButtonHoverText(2, sx+9, sy+7+32, 14, 14, 134, sync ? 14*6 : 14*7, GuiBackpack.WIDGETS, 14, 0, Lang.prefix+"gui.jeisync."+mode));
		}
	}

	public List<ItemStackData> getDisplayItems(){
		boolean safe = panel !=null && panel.getNetwork() !=null && panel.getNetwork() instanceof EStorageNetworkClient;
		if(safe){
			EStorageNetworkClient net = (EStorageNetworkClient) panel.getNetwork();
			return net.getDisplayItems(searchBar.getText(), panel.getSortType(), panel.getViewType());
		}
		return Lists.newArrayList();
	}
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		if(currentPopup !=null){
			if(currentPopup.clicked(this, mouseX, mouseY, mouseButton))
			return;
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
		//Inside Search Bar
		if(isPointInRegion(getSearchBarX(), getSearchBarY(), getSearchBarWidth(), 16, mouseX, mouseY)){
			if(!this.searchBar.isFocused()){
				this.searchBar.setFocused(true);
			}
		}else{
			this.searchBar.setFocused(false);
		}
		
		if (draggingScrollbar != null) {
			draggingScrollbar.mouseClicked(mouseX, mouseY, mouseButton);
        	return;
		}
		
		if(this.scrollbar !=null){
			if (scrollbar.mouseClicked(mouseX, mouseY, mouseButton)) {
				draggingScrollbar = scrollbar;
				return;
			}
		}
		
		int nslot = getNetworkSlot(mouseX, mouseY);
		int fixednSlot = (nslot+getItemRow()*getItemsPerRow());
		
		//Inside List
		if(nslot > -1){
			
			boolean safe = panel !=null && panel.getNetwork() !=null && panel.getNetwork() instanceof EStorageNetworkClient;
			if(safe){
				EntityPlayer player = CrystalMod.proxy.getClientPlayer();
				EStorageNetworkClient net = (EStorageNetworkClient) panel.getNetwork();
				
				ItemStack held = player.inventory.getItemStack();
				
				if(held !=null && (mouseButton == 0 || mouseButton == 1)){
					try {
						ItemStack copy = held.copy();
						if(mouseButton == 1){
							copy.stackSize = 1;
						}
						sendUpdate(0, -1, -1, new ItemStackData(copy));
					} catch (Exception e) {
						e.printStackTrace();
					}
					return;
				}
				
				ItemStackData data = (fixednSlot < 0 || getDisplayItems().size() <= fixednSlot) ? null : getDisplayItems().get(fixednSlot);
				if(data !=null && data.stack !=null){
					ItemStack dis = data.stack;
					if (held == null)
	                {
	                    if (player.capabilities.isCreativeMode && mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
	                    {
	                    	sendUpdate(2, -1, -1, data);
	                    	return;
	                    }
	                }
					if(mouseButton == 0 || mouseButton == 1 || mouseButton == 2){
						boolean craftable = false;
						search : for(ItemStackData data2 : net.craftingItems){
							if(data2.stack !=null && ItemUtil.canCombine(dis, data2.stack)){
								craftable = true;
								break search;
							}
						}
						if(craftable && (isCtrlKeyDown() || data.isCrafting)){
							this.currentPopup = new CraftingAmountPopup(this, data);
							return;
						}
						
						int amount = dis.getMaxStackSize();
						
						if(mouseButton == 1){
							//HALF (Right Click)
							amount = dis.getMaxStackSize()/2;
						}
						
						if(mouseButton == 2){
							//Single (Middle Click)
							amount = 1;
						}
						int slot = -1;
						if(isShiftKeyDown()){
							slot = 1;
						}
						try {
							if(!data.isCrafting){
								CrystalModNetwork.sendToServer(new PacketEStorageAddItem(1, slot, amount, EStorageNetwork.compressItem(data)));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						return;
					}
				}
			}
		}
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
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		//scrollbar.update(mouseX - guiLeft, mouseY - guiTop);
		
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
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
		final int xStart = (getListX()+1);
		int x = xStart;
		int y = getListY()+1;
		int row = getItemRow();
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
	            
	            String stackSize = getStackSize(dis);
	            
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
		int fixednSlot = (nslot+getItemRow()*getItemsPerRow());
		
		//Inside List
		if(nslot > -1 && currentPopup == null){
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
		            renderToolTip(dis, mouseX-sx, mouseY-sy);
		            GlStateManager.popMatrix();
		            GlStateManager.enableLighting();
		            GlStateManager.enableDepth();
		            RenderHelper.enableStandardItemLighting();
				}
			}
		}
		
		if(currentPopup !=null)currentPopup.render(this);
    }
	
	public static String getStackSize(ItemStack stack){
		String stackSize;
		if (stack.stackSize == 1) {
			stackSize = "";
		} else if (stack.stackSize < 1000) {
			stackSize = stack.stackSize + "";
		} else if (stack.stackSize < 100000) {
			stackSize = stack.stackSize / 1000 + "K";
		} else if (stack.stackSize < 1000000) {
			stackSize = "0." + stack.stackSize / 100000+"M";
		} else {
			stackSize = stack.stackSize / 1000000 + "M";
		}
		return stackSize;
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
	
	public String getTexture() {
		return "crystalmod:textures/gui/eStorage_panel.png";
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
		if (keyCode == 1) {
			if(currentPopup !=null){
				currentPopup = null;
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
		
		if(currentPopup == null){
			if(!this.searchBar.isFocused()){
				if(keyCode == Keyboard.KEY_S){
					searchBar.setFocused(true);
					return;
				}
			}
			//Inside List
			int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
	        int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
			int s = getNetworkSlot(mouseX, mouseY);
			int fixednSlot = (s+getItemRow()*getItemsPerRow());
			if(s > -1){
				ItemStackData data = (fixednSlot < 0 || getDisplayItems().size() <= fixednSlot) ? null : getDisplayItems().get(fixednSlot);
				if(data !=null && data.stack !=null){
					ItemStack dis = data.stack;
					
					if(JEIUtil.isInstalled()){
						try{
							RecipesGui recipesGui = JEIUtil.getRecipesGui();
		        			if(recipesGui !=null){
							//RecipesGui recGui = /*new RecipesGui()*/JEIUtil.getRecipesGui();
								if (keyCode == KeyBindings.showRecipe.getKeyCode()) {
									Focus<?> focus = new Focus<ItemStack>(dis);
									if (focus != null) {
										if (!GuiScreen.isShiftKeyDown()) {
											recipesGui.showRecipes(focus);
										} else {
											recipesGui.showUses(focus);
										}
										return;
									}
								} else if (keyCode == KeyBindings.showUses.getKeyCode()) {
									recipesGui.showUses(dis);
								}
		        			}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					if (this.mc.thePlayer.inventory.getItemStack() == null)
				    {
						if (mc.thePlayer.capabilities.isCreativeMode && keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode())
	                    {
	                    	sendUpdate(2, -1, -1, data);
	    					return;
	                    }
				        for (int i = 0; i < 9; ++i)
				        {
				            if (keyCode == this.mc.gameSettings.keyBindsHotbar[i].getKeyCode())
				            {
				            	int slot = i;
							
				            	if(slot !=-1 && (this.mc.thePlayer.inventory.getStackInSlot(i) == null || ItemUtil.canCombine(this.mc.thePlayer.inventory.getStackInSlot(i), dis))){
									sendUpdate(1, slot, dis.getMaxStackSize(), data);
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
	        		if(panel.getNetwork() !=null && panel.getNetwork() instanceof EStorageNetworkClient){
	        			((EStorageNetworkClient)panel.getNetwork()).needsListUpdate = true;
	        		}
	        		try{
	            		if(JEIUtil.isInstalled() && panel.getJEISync()){
	            			JEIUtil.setFilterText(searchBar.getText());
	            		}
	        		}catch(Exception e){
	        			e.printStackTrace();
	        		}
	        		return;
	            }
	        }
		} else{
			if(currentPopup.keyTyped(this, typedChar, keyCode)){
				return;
			}
		}
        super.keyTyped(typedChar, keyCode);
    }
	
	public int getItemRow() {
		return this.itemRow;
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
	
	@Override
	public ItemStackData getDataUnderMouse(int mouseX, int mouseY) {
		int s = getNetworkSlot(mouseX, mouseY);
		int fixednSlot = (s+getItemRow()*getItemsPerRow());
		if(s > -1){
			ItemStackData data = (fixednSlot < 0 || getDisplayItems().size() <= fixednSlot) ? null : getDisplayItems().get(fixednSlot);
			if(data !=null && data.stack !=null){
				return data;
			}
		}
		return null;
	}

	public RenderItem getItemRender() {
		return itemRender;
	}
}
