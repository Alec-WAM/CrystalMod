package com.alec_wam.CrystalMod.items.backpack.gui;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.items.backpack.BackpackUtils;
import com.alec_wam.CrystalMod.items.backpack.InventoryBackpackModular;
import com.alec_wam.CrystalMod.items.backpack.PacketBackpackGuiAction;
import com.alec_wam.CrystalMod.items.backpack.container.ContainerBackpackFurnace;
import com.alec_wam.CrystalMod.network.CrystalModNetwork;
import com.alec_wam.CrystalMod.proxy.CommonProxy;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.Lang;

public class GuiBackpackFurnace extends GuiBackpackBase
{
	public static final int BTN_ID_FIRST_MOVE_ITEMS    = 0;

    protected final EntityPlayer player;
    protected final ContainerBackpackFurnace container;
    protected final InventoryBackpackModular invModular;
    protected final int invSize;
    protected final int bagTier;

    protected float oldMouseX;
    protected float oldMouseY;
    protected int firstArmorSlotX;
    protected int firstArmorSlotY;
    private boolean hasActivePotionEffects;
    private int[] lastPos = new int[2];

    public GuiBackpackFurnace(ContainerBackpackFurnace container)
    {
        super(container, 198+18, 256, "crystalmod:textures/gui/backpack/furnace.png");

        this.player = container.player;
        this.container = container;
        this.invModular = container.inventoryItemModular;
        this.invSize = this.invModular.getSlots();
        this.bagTier = this.container.getBagTier();

        this.scaledStackSizeTextTargetInventories.add(this.invModular);
    }

    private void updatePositions()
    {
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
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }
    
    @Override
    public void updateScreen()
    {
        super.updateScreen();
        //this.nameField.updateCursorCounter();
        this.updateActivePotionEffects();
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
        
        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(this.guiTexture);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        if (this.container.getModularItem() !=null && ItemNBTHelper.getInteger(this.container.getModularItem(), "Furnace.Burntime", 0) > 0)
        {
            int k = 13-this.getBurnLeftScaled(13);
            this.drawTexturedModalRect(this.guiLeft + 72, this.guiTop + 40+k, 232, k, 14, 14-k);
        }

        int l = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(this.guiLeft + 94, this.guiTop + 37, 232, 14, l + 1, 16);
        
        
        this.bindTexture(this.guiTextureWidgets);

        // The inventory is not accessible (because there is no valid Memory Card selected)
        if (this.invModular.isUseableByPlayer(this.player) == false)
        {
            // Draw the dark background icon over the disabled inventory slots
            for (int i = 0; i < BackpackUtils.MAIN_SIZE+BackpackUtils.FURNACE_SIZE; i++)
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
        //GuiInventory.drawEntityOnScreen(xOff, this.guiTop + 82, 30, xOff - this.oldMouseX, this.guiTop + 25 - this.oldMouseY, this.mc.thePlayer);, 16);

        
    }
    
    private int getCookProgressScaled(int pixels)
    {
    	if(this.container.getModularItem() ==null)return 0;
        int i = ItemNBTHelper.getInteger(this.container.getModularItem(), "Furnace.Cooktime", 0);
        int j = ItemNBTHelper.getInteger(this.container.getModularItem(), "Furnace.TotalCooktime", 0);
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    private int getBurnLeftScaled(int pixels)
    {
    	if(this.container.getModularItem() ==null)return 0;
        int i = ItemNBTHelper.getInteger(this.container.getModularItem(), "Furnace.CurrentItemBurntime", 0);

        if (i == 0)
        {
            i = 200;
        }

        return ItemNBTHelper.getInteger(this.container.getModularItem(), "Furnace.Burntime", 0) * pixels / i;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(I18n.format("container.furnace"), 70, 8, 0x404040);
    	this.fontRendererObj.drawString(I18n.format(Lang.prefix+"container.backpack"), 29, 91, 0x404040);
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
            player.openGui(CrystalMod.instance, CommonProxy.GUI_ID_ITEM, player.worldObj, value, slot, 0);
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
}