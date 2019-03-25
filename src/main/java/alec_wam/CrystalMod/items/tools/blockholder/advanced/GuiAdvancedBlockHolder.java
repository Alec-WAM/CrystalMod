package alec_wam.CrystalMod.items.tools.blockholder.advanced;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.items.tools.blockholder.ItemBlockHolder;
import alec_wam.CrystalMod.items.tools.blockholder.advanced.ItemAdvancedBlockHolder.BlockStackData;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.network.packets.PacketItemNBT;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiAdvancedBlockHolder extends GuiContainer implements IMessageHandler {

	final InventoryPlayer playerInv;
	private GuiButton buttonLeft;
	private GuiButton buttonRight;
	
	public GuiAdvancedBlockHolder(EntityPlayer player, ItemStack blockHolder) {
		super(new ContainerAdvancedBlockHolder(player.inventory, blockHolder));
		playerInv = player.inventory;
		this.ySize = 172;
	}
	
	public ItemStack getBlockHolder(){
		return ((ContainerAdvancedBlockHolder)this.inventorySlots).blockHolder;
	}
	
	public BlockStackData getCurrentData(){
		return ItemAdvancedBlockHolder.getSelectedData(getBlockHolder());
	}
	
	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		TextFormatting format = ItemBlockHolder.isAutoPickupEnabled(getBlockHolder()) ? TextFormatting.GREEN : TextFormatting.RED;
		this.buttonList.add(new GuiButton(0, guiLeft + 154, guiTop + 7, 16, 16, format+"A"));
		buttonLeft = new GuiButton(1, guiLeft + 52, guiTop + 34, 20, 20, "<");
		buttonRight = new GuiButton(2, guiLeft + 104, guiTop + 34, 20, 20, ">");
		this.buttonList.add(buttonLeft);		
		this.buttonList.add(buttonRight);
		updateButtons();
	}

	public void updateButtons(){
		int selection = ItemAdvancedBlockHolder.getSelection(getBlockHolder());
		buttonLeft.enabled = selection != 0;
		int max = Math.min(ItemAdvancedBlockHolder.getValidBlockSize(getBlockHolder()), 4);
		buttonRight.enabled = selection != max;
	}
	
	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if(button.id == 0){
			boolean oldAP = ItemBlockHolder.isAutoPickupEnabled(getBlockHolder());
			ItemBlockHolder.setAutoPickup(getBlockHolder(), !oldAP);
			CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(getBlockHolder())));
			TextFormatting format = !oldAP ? TextFormatting.GREEN : TextFormatting.RED;
			button.displayString = format+"A";
		}
		if(button == buttonLeft){
			int selection = ItemAdvancedBlockHolder.getSelection(getBlockHolder());
			if(selection > 0){
				int newSel = selection-1;
				ItemAdvancedBlockHolder.setSelection(getBlockHolder(), newSel);
				CrystalModNetwork.sendToServer(new PacketBlockHolderSelection(newSel));		
				updateButtons();
			}
		}
		if(button == buttonRight){
			int selection = ItemAdvancedBlockHolder.getSelection(getBlockHolder());
			if(selection < 4){
				int newSel = selection+1;
				ItemAdvancedBlockHolder.setSelection(getBlockHolder(), newSel);
				CrystalModNetwork.sendToServer(new PacketBlockHolderSelection(newSel));		
				updateButtons();
			}
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = getBlockHolder().getDisplayName();
		this.fontRendererObj.drawString(name, 8, 6, 4210752);
    	this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, 80, 4210752);
		
		boolean hasStack = false;
		BlockStackData currentData = getCurrentData();
		ItemStack blockStack = currentData.stack;
		if(ItemStackTools.isValid(blockStack)){
			hasStack = true;
			GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            
            GlStateManager.pushMatrix();
    		this.itemRender.zLevel = 100.0F;
            this.itemRender.renderItemAndEffectIntoGUI(blockStack, 80, 36);
            GlStateManager.popMatrix();
            this.itemRender.zLevel = 0.0F;
            GlStateManager.disableLighting();

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            
            int max = (blockStack.getMaxStackSize() * ItemBlockHolder.MAX_STACK_COUNT);
            String count = currentData.count + " / " + max;
            GlStateManager.pushMatrix();
            GlStateManager.translate(89, 59, 0);
            GlStateManager.scale(0.65, 0.65, 0.65);
            this.fontRendererObj.drawString(count, -(this.fontRendererObj.getStringWidth(count) / 2), 0, 0);
            GlStateManager.popMatrix();         
            
            int color = Color.BLACK.getRGB();
            int x = 67;
            int y = 65;
            int width = 44;
            drawRect(x, y, x + width, y+5, color);
            double space = (double)currentData.count / (double)max;
            int colorBar = Color.HSBtoRGB(Math.max(0.0F, (float) (1.0F - space)) / 3.0F, 1.0F, 1.0F);
            int barWidth = (int)((double)(width-2) * space);
            drawRect(x+1, y+1, x + 1 + barWidth, y+4, colorBar);
		}        
        if(isPointInRegion(80, 36, 16, 16, mouseX, mouseY)){
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int j1 = 80;
            int k1 = 36;
            GlStateManager.colorMask(true, true, true, false);
            int color = -2130706433;
            if(hasStack && currentData.count <= 0 && GuiScreen.isCtrlKeyDown()){
            	color = 855572480; //Clear Red
            }
            this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, color, color);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(isPointInRegion(80, 36, 16, 16, mouseX, mouseY)){
			BlockStackData currentData = getCurrentData();
			ItemStack stack = currentData.stack;
			if(ItemStackTools.isValid(stack) && ItemStackTools.isEmpty(playerInv.getItemStack())){
				List<String> list = stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips);
	
		        for (int i = 0; i < list.size(); ++i)
		        {
		            if (i == 0)
		            {
		                list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
		            }
		            else
		            {
		                list.set(i, TextFormatting.GRAY + (String)list.get(i));
		            }
		        }
		        
		        if(currentData.count <= 0){
		        	list.add(TextFormatting.RED + "Ctrl-Click to Remove Block");
		        }
	
		        FontRenderer font = stack.getItem().getFontRenderer(stack);
		        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
		        this.drawHoveringText(list, mouseX, mouseY, (font == null ? fontRendererObj : font));
		        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		if(isPointInRegion(80, 36, 16, 16, mouseX, mouseY)){
			//In Fake Slot
			BlockStackData currentData = getCurrentData();
			ItemStack blockStack = currentData.stack;
			ItemStack hoverStack = playerInv.getItemStack();
			if(ItemStackTools.isValid(blockStack)){
				if(currentData.count <= 0){
					if(GuiScreen.isCtrlKeyDown()){
						NonNullList<BlockStackData> dataList = ItemAdvancedBlockHolder.getBlockList(getBlockHolder());
						int selection = ItemAdvancedBlockHolder.getSelection(getBlockHolder());
						NonNullList<BlockStackData> newList = ItemAdvancedBlockHolder.clearSlot(dataList, selection);
						ItemAdvancedBlockHolder.setBlockList(getBlockHolder(), newList);
						CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(getBlockHolder())));
						updateButtons();
						return;
					}
				}
				
				if(!GuiScreen.isShiftKeyDown()){
					if(ItemStackTools.isValid(hoverStack)){
						if(ItemUtil.canCombine(blockStack, hoverStack)){
							int added = ItemAdvancedBlockHolder.addBlocks(getBlockHolder(), ItemAdvancedBlockHolder.getSelection(getBlockHolder()), mouseButton == 1 ? 1 : ItemStackTools.getStackSize(hoverStack));
							if(added > 0){
								ItemStackTools.incStackSize(hoverStack, -added);
								if(ItemStackTools.isEmpty(hoverStack)){
									playerInv.setItemStack(ItemStackTools.getEmptyStack());
									NBTTagCompound nbt = new NBTTagCompound();
									nbt.setBoolean("EmptyStack", true);
									CrystalModNetwork.sendToServer(new PacketEntityMessage(playerInv.player, "#SetMouseStack#", nbt));
								} else {
									playerInv.setItemStack(hoverStack);
									NBTTagCompound nbt = new NBTTagCompound();
									nbt.setTag("Stack", hoverStack.writeToNBT(new NBTTagCompound()));
									CrystalModNetwork.sendToServer(new PacketEntityMessage(playerInv.player, "#SetMouseStack#", nbt));
								}
								CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(getBlockHolder())));
								return;
							}
						}
					} else {												
						int remove = ItemAdvancedBlockHolder.removeBlocks(getBlockHolder(), ItemAdvancedBlockHolder.getSelection(getBlockHolder()), 64);
						if(remove > 0){
							ItemStack returnStack = ItemUtil.copy(blockStack, remove);
							playerInv.setItemStack(returnStack);
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setTag("Stack", returnStack.writeToNBT(new NBTTagCompound()));
							CrystalModNetwork.sendToServer(new PacketEntityMessage(playerInv.player, "#SetMouseStack#", nbt));
							CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(getBlockHolder())));
							return;
						}
					}
				} else {
					//Handle Shift transferNBTTagCompound nbt = new NBTTagCompound();
					CrystalModNetwork.sendToServer(new PacketGuiMessage("ShiftTransfer"));
					return;
				}				
			} else {
				if(ItemStackTools.isValid(hoverStack)){
					if(ItemBlockHolder.canBePlaced(hoverStack)){
						//TODO Make right click add only one item
						ItemStack copy = ItemUtil.copy(hoverStack, 1);
						BlockStackData newData = new BlockStackData(copy, ItemStackTools.getStackSize(hoverStack));
						ItemAdvancedBlockHolder.setSelectedData(getBlockHolder(), newData);
						playerInv.setItemStack(ItemStackTools.getEmptyStack());
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setBoolean("EmptyStack", true);
						CrystalModNetwork.sendToServer(new PacketEntityMessage(playerInv.player, "#SetMouseStack#", nbt));
						CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(getBlockHolder())));
						updateButtons();
						return;
					}
				}
			}
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	public final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/blockholder.png");
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		
	}
	
}
