package alec_wam.CrystalMod.items.tools.blockholder;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiBlockHolder extends GuiContainer implements IMessageHandler {

	final InventoryPlayer playerInv;
	
	public GuiBlockHolder(EntityPlayer player, ItemStack blockHolder) {
		super(new ContainerBlockHolder(player.inventory, blockHolder));
		playerInv = player.inventory;
		this.ySize = 172;
	}
	
	public ItemStack getBlockHolder(){
		return ((ContainerBlockHolder)this.inventorySlots).blockHolder;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		TextFormatting format = ItemBlockHolder.isAutoPickupEnabled(getBlockHolder()) ? TextFormatting.GREEN : TextFormatting.RED;
		this.buttonList.add(new GuiButton(0, guiLeft + 154, guiTop + 7, 16, 16, format+"A"));
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
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = getBlockHolder().getDisplayName();
		this.fontRendererObj.drawString(name, 8, 6, 4210752);
    	this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, 80, 4210752);
		
		boolean hasStack = false;
		ItemStack blockStack = ItemBlockHolder.getBlockStack(getBlockHolder());
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
            String count = ItemBlockHolder.getBlockCount(getBlockHolder()) + " / " + max;
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
            double space = (double)ItemBlockHolder.getBlockCount(getBlockHolder()) / (double)max;
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
            if(hasStack && ItemBlockHolder.getBlockCount(getBlockHolder()) <= 0 && GuiScreen.isCtrlKeyDown()){
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
			ItemStack stack = ItemBlockHolder.getBlockStack(getBlockHolder());
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
		        
		        if(ItemBlockHolder.getBlockCount(getBlockHolder()) <= 0){
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
			ItemStack blockStack = ItemBlockHolder.getBlockStack(getBlockHolder());
			ItemStack hoverStack = playerInv.getItemStack();
			if(ItemStackTools.isValid(blockStack)){
				if(ItemBlockHolder.getBlockCount(getBlockHolder()) <= 0){
					if(GuiScreen.isCtrlKeyDown()){
						ItemBlockHolder.setBlockStack(getBlockHolder(), ItemStackTools.getEmptyStack());
						CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(getBlockHolder())));
						return;
					}
				}
				
				if(!GuiScreen.isShiftKeyDown()){
					if(ItemStackTools.isValid(hoverStack)){
						if(ItemUtil.canCombine(blockStack, hoverStack)){
							int added = ItemBlockHolder.addBlocks(getBlockHolder(), mouseButton == 1 ? 1 : ItemStackTools.getStackSize(hoverStack));
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
						int remove = ItemBlockHolder.removeBlocks(getBlockHolder(), 64);
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
						ItemBlockHolder.setBlockStack(getBlockHolder(), copy);
						ItemBlockHolder.setBlockCount(getBlockHolder(), ItemStackTools.getStackSize(hoverStack));
						playerInv.setItemStack(ItemStackTools.getEmptyStack());
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setBoolean("EmptyStack", true);
						CrystalModNetwork.sendToServer(new PacketEntityMessage(playerInv.player, "#SetMouseStack#", nbt));
						CrystalModNetwork.sendToServer(new PacketItemNBT(-1, ItemNBTHelper.getCompound(getBlockHolder())));
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
