package alec_wam.CrystalMod.items.backpack.gui;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import org.lwjgl.input.Keyboard;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.backpack.BackpackUtils;
import alec_wam.CrystalMod.items.backpack.InventoryBackpackModular;
import alec_wam.CrystalMod.items.backpack.PacketBackpackGuiAction;
import alec_wam.CrystalMod.items.backpack.container.ContainerBackpackRepair;
import alec_wam.CrystalMod.items.backpack.container.PacketBackpackGuiActionRename;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.proxy.CommonProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.Lang;

public class GuiBackpackRepair extends GuiBackpackBase implements IContainerListener
{
	private static final ResourceLocation anvilResource = new ResourceLocation("textures/gui/container/anvil.png");
    public static final int BTN_ID_FIRST_MOVE_ITEMS    = 0;

    protected final EntityPlayer player;
    protected final ContainerBackpackRepair container;
    protected final InventoryBackpackModular invModular;
    private GuiTextField nameField;
    protected final int invSize;
    protected final int bagTier;

    protected float oldMouseX;
    protected float oldMouseY;
    protected int firstArmorSlotX;
    protected int firstArmorSlotY;
    private boolean hasActivePotionEffects;
    private int[] lastPos = new int[2];

    public GuiBackpackRepair(ContainerBackpackRepair container)
    {
        super(container, 198+18, 256, "crystalmod:textures/gui/backpack/anvil.png");

        this.player = container.player;
        this.container = container;
        this.invModular = container.inventoryItemModular;
        this.invSize = this.invModular.getSlots();
        this.bagTier = this.container.getBagTier();

        this.scaledStackSizeTextTargetInventories.add(this.invModular);
    }

    private void updatePositions()
    {
    	Slot slot = this.container.getSlot(this.invSize + 36 + 3);
    	if(slot  !=null){
    		this.firstArmorSlotX   = this.guiLeft + slot.xDisplayPosition;
    		this.firstArmorSlotY   = this.guiTop  + slot.yDisplayPosition;
    	}else{
    		this.firstArmorSlotX   = this.guiLeft + 8;
    		this.firstArmorSlotY   = this.guiTop  + 93;
    	}

        this.createButtons();

        this.lastPos[0] = this.guiLeft;
        this.lastPos[1] = this.guiTop;
    }

    private boolean needsPositionUpdate()
    {
        return this.lastPos[0] != this.guiLeft || this.lastPos[1] != this.guiTop;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.updatePositions();

        this.updateActivePotionEffects();
        Keyboard.enableRepeatEvents(true);
        this.nameField = new GuiTextField(0, this.fontRendererObj, this.guiLeft+80, this.guiTop+38, 103, 12);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(30);
        this.inventorySlots.removeListener(this);
        this.inventorySlots.addListener(this);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        this.inventorySlots.removeListener(this);
    }
    
    @Override
    public void updateScreen()
    {
        super.updateScreen();
        //this.nameField.updateCursorCounter();
        this.updateActivePotionEffects();
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float gameTicks)
    {
        super.drawScreen(mouseX, mouseY, gameTicks);

        if (this.needsPositionUpdate() == true)
        {
            this.updatePositions();
        }

        if (this.hasActivePotionEffects == true)
        {
            this.drawActivePotionEffects();
        }

        this.drawTooltips(mouseX, mouseY);
        
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.nameField.drawTextBox();
        
        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.nameField.textboxKeyTyped(typedChar, keyCode))
        {
            this.renameItem();
        }
        else
        {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    private void renameItem()
    {
    	this.container.updateItemName(nameField.getText());
        CrystalModNetwork.sendToServer(new PacketBackpackGuiActionRename(nameField.getText()));
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(this.guiTexture);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        //X
        if ((this.container.getSlot(BackpackUtils.MAIN_SIZE).getHasStack() || this.container.getSlot(BackpackUtils.MAIN_SIZE+1).getHasStack()) && !this.container.getSlot(BackpackUtils.MAIN_SIZE+2).getHasStack())
        {
            this.drawTexturedModalRect(this.guiLeft+117, this.guiTop+59, 228, 0, 28, 21);
        }
        this.bindTexture(this.guiTextureWidgets);

        // The inventory is not accessible (because there is no valid Memory Card selected)
        if (this.invModular.isUseableByPlayer(this.player) == false)
        {
            // Draw the dark background icon over the disabled inventory slots
            for (int i = 0; i < BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE; i++)
            {
                Slot slot = this.container.getSlot(i);
                this.drawTexturedModalRect(this.guiLeft + slot.xDisplayPosition - 1, this.guiTop + slot.yDisplayPosition - 1, 102, 0, 18, 18);
            }
        }
        // Draw the colored background for the selected slot (for swapping), if any
        else if (this.container.getSelectedSlot() != -1)
        {
            Slot slot = this.container.getSlot(this.container.getSelectedSlot());
            this.drawTexturedModalRect(this.guiLeft + slot.xDisplayPosition - 1, this.guiTop + slot.yDisplayPosition - 1, 102, 18, 18, 18);
        }

        //int xOff = this.guiLeft + (this.bagTier == 1 ? 91 : 51);
        // Draw the player model
        //GuiInventory.drawEntityOnScreen(xOff, this.guiTop + 82, 30, xOff - this.oldMouseX, this.guiTop + 25 - this.oldMouseY, this.mc.thePlayer);
        
        this.mc.getTextureManager().bindTexture(anvilResource);
        //TEXTBOX
        this.drawTexturedModalRect(this.guiLeft+77, this.guiTop+34, 0, 166 + (this.container.getSlot(BackpackUtils.MAIN_SIZE).getHasStack() ? 0 : 16), 110, 16);

        
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        //this.fontRendererObj.drawString(I18n.format("container.repair"), 52, 12, 0x404040);
    	this.fontRendererObj.drawString(I18n.format(Lang.prefix+"container.backpack"), 29, 91, 0x404040);
    	GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.fontRendererObj.drawString(I18n.format("container.repair", new Object[0]), 60-18, 6, 4210752);

        if (this.container.maximumCost > 0)
        {
            int i = 8453920;
            boolean flag = true;
            String s = I18n.format("container.repair.cost", new Object[] {Integer.valueOf(this.container.maximumCost)});

            if (this.container.maximumCost >= 40 && !this.mc.thePlayer.capabilities.isCreativeMode)
            {
                s = I18n.format("container.repair.expensive", new Object[0]);
                i = 16736352;
            }
            else if (!this.container.getSlot(BackpackUtils.MAIN_SIZE+2).getHasStack())
            {
                flag = false;
            }
            else if (!this.container.getSlot(BackpackUtils.MAIN_SIZE+2).canTakeStack(player))
            {
                i = 16736352;
            }

            if (flag)
            {
                int j = -16777216 | (i & 16579836) >> 2 | i & -16777216;
                int k = 76*2 - 16 - this.fontRendererObj.getStringWidth(s)/2;
                int l = 67+13;

                if (this.fontRendererObj.getUnicodeFlag())
                {
                    drawRect(k - 3, l - 2, this.xSize - 7, l + 10, -16777216);
                    drawRect(k - 2, l - 1, this.xSize - 8, l + 9, -12895429);
                }
                else
                {
                    this.fontRendererObj.drawString(s, k, l + 1, j);
                    this.fontRendererObj.drawString(s, k + 1, l, j);
                    this.fontRendererObj.drawString(s, k + 1, l + 1, j);
                }

                this.fontRendererObj.drawString(s, k, l, i);
            }
        }

        GlStateManager.enableLighting();
    }

    protected void createButtons()
    {
        this.buttonList.clear();

        // Add the Memory Card selection buttons

        int x = this.guiLeft + this.container.getSlot(0).xDisplayPosition + 2;
        int y = this.guiTop + this.container.getSlot(0).yDisplayPosition + 55;

        this.buttonList.add(new GuiButtonHoverText(BTN_ID_FIRST_MOVE_ITEMS + 0, x +   0, y + 0, 12, 12, 24,  0, this.guiTextureWidgets, 12, 0,
                Lang.prefix+"gui.label.moveallitemsexcepthotbar",
                Lang.prefix+"gui.label.moveallitemsexcepthotbar.holdshift"));

        this.buttonList.add(new GuiButtonHoverText(BTN_ID_FIRST_MOVE_ITEMS + 1, x +  18, y + 0, 12, 12, 24, 12, this.guiTextureWidgets, 12, 0,
                Lang.prefix+"gui.label.movematchingitems"));

        this.buttonList.add(new GuiButtonHoverText(BTN_ID_FIRST_MOVE_ITEMS + 2, x +  36, y + 0, 12, 12, 24, 24, this.guiTextureWidgets, 12, 0,
                Lang.prefix+"gui.label.leaveonefilledstack"));

        this.buttonList.add(new GuiButtonHoverText(BTN_ID_FIRST_MOVE_ITEMS + 3, x + 108, y + 0, 12, 12, 24, 36, this.guiTextureWidgets, 12, 0,
                Lang.prefix+"gui.label.fillstacks"));

        this.buttonList.add(new GuiButtonHoverText(BTN_ID_FIRST_MOVE_ITEMS + 4, x + 126, y + 0, 12, 12, 24, 48, this.guiTextureWidgets, 12, 0,
                 Lang.prefix+"gui.label.movematchingitems"));

        this.buttonList.add(new GuiButtonHoverText(BTN_ID_FIRST_MOVE_ITEMS + 5, x + 144, y + 0, 12, 12, 24, 60, this.guiTextureWidgets, 12, 0,
                Lang.prefix+"gui.label.moveallitems"));
        
        BackpackUtils.addTabs(this, buttonList, BTN_ID_FIRST_MOVE_ITEMS + 6, x, y, guiTop, guiLeft, container.getModularItem());
    }

    protected void drawTooltips(int mouseX, int mouseY)
    {
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

    protected void bindTexture(ResourceLocation rl)
    {
        this.mc.getTextureManager().bindTexture(rl);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);

        if (button.id >= BTN_ID_FIRST_MOVE_ITEMS && button.id <= (BTN_ID_FIRST_MOVE_ITEMS + 5))
        {
            int value = button.id - BTN_ID_FIRST_MOVE_ITEMS;
            if (GuiScreen.isShiftKeyDown() == true)
            {
                value |= 0x8000;
            }

            CrystalModNetwork.sendToServer(new PacketBackpackGuiAction(0, value));
        }
        
        if (button.id >= (BTN_ID_FIRST_MOVE_ITEMS + 6))
        {
            int value = button.id - (BTN_ID_FIRST_MOVE_ITEMS + 6);
            CrystalModNetwork.sendToServer(new PacketBackpackGuiAction(1, value));
            int slot = BackpackUtils.getSlotOfFirstMatchingItemStack(new PlayerMainInvWrapper(player.inventory), container.getModularItem());
            player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ITEM, player.worldObj, value, slot, 0);
            if(container.getModularItem() !=null){
            	ItemNBTHelper.setInteger(container.getModularItem(), "LastTab", value);
            }
        }
    }

    protected void updateActivePotionEffects()
    {
        boolean hasVisibleEffect = false;
        for(PotionEffect potioneffect : this.mc.thePlayer.getActivePotionEffects()) {
            Potion potion = potioneffect.getPotion();
            if(potion.shouldRender(potioneffect)) { hasVisibleEffect = true; break; }
        }
        if (!this.mc.thePlayer.getActivePotionEffects().isEmpty() && hasVisibleEffect)
        {
            this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
            this.hasActivePotionEffects = true;
        }
        else
        {
            this.guiLeft = (this.width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
        }
    }

    private void drawActivePotionEffects()
    {
        int i = this.guiLeft - 124;
        int j = this.guiTop;

        Collection<PotionEffect> collection = this.mc.thePlayer.getActivePotionEffects();

        if (!collection.isEmpty())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            int l = 33;

            if (collection.size() > 5)
            {
                l = 132 / (collection.size() - 1);
            }

            for (PotionEffect potioneffect : this.mc.thePlayer.getActivePotionEffects())
            {
                Potion potion = potioneffect.getPotion();
                if(!potion.shouldRender(potioneffect)) continue;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
                this.drawTexturedModalRect(i, j, 0, 166, 140, 32);

                if (potion.hasStatusIcon())
                {
                    int i1 = potion.getStatusIconIndex();
                    this.drawTexturedModalRect(i + 6, j + 7, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }

                potion.renderInventoryEffect(i, j, potioneffect, mc);
                if (!potion.shouldRenderInvText(potioneffect)) { j += l; continue; }
                String s1 = I18n.format(potion.getName(), new Object[0]);

                if (potioneffect.getAmplifier() == 1)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.2", new Object[0]);
                }
                else if (potioneffect.getAmplifier() == 2)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.3", new Object[0]);
                }
                else if (potioneffect.getAmplifier() == 3)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.4", new Object[0]);
                }

                this.fontRendererObj.drawStringWithShadow(s1, (float)(i + 10 + 18), (float)(j + 6), 16777215);
                String s = Potion.getPotionDurationString(potioneffect, 1.0F);
                this.fontRendererObj.drawStringWithShadow(s, (float)(i + 10 + 18), (float)(j + 6 + 10), 8355711);
                j += l;
            }
        }
    }
    
    /**
     * update the crafting window inventory with the items in the list
     */
    public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList)
    {
        this.sendSlotContents(containerToSend, BackpackUtils.MAIN_SIZE, containerToSend.getSlot(BackpackUtils.MAIN_SIZE).getStack());
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot. Args: Container, slot number, slot contents
     */
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
    {
    	if (slotInd == BackpackUtils.MAIN_SIZE)
        {
            this.nameField.setText(stack == null ? "" : stack.getDisplayName());
            this.nameField.setEnabled(stack != null);

            if (stack != null)
            {
                this.renameItem();
            }
        }
    }

    /**
     * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
     * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
     * value. Both are truncated to shorts in non-local SMP.
     */
    public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue)
    {
    }

    public void sendAllWindowProperties(Container p_175173_1_, IInventory p_175173_2_)
    {
    }
}