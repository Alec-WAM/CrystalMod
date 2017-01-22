package alec_wam.CrystalMod.items.tools.backpack.gui;

import java.io.IOException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBackpackNormal extends GuiContainer {

    private static final ResourceLocation RES_LOC = new ResourceLocation("crystalmod:textures/gui/backpack/normal.png");
    
    private ItemStack backpack;
    private int slotRows;
    private InventoryBackpackUpgrades upgrades;
    private boolean hasPockets;
    private boolean hasTabs;
    public GuiBackpackNormal(NormalInventoryBackpack backpackInventory){
        super(new ContainerBackpackNormal(backpackInventory));
        this.backpack = backpackInventory.getBackpack();
        
        if(ItemStackTools.isValid(backpack)){
        	this.upgrades = BackpackUtil.getUpgradeInventory(backpack);
        	this.hasTabs = (upgrades !=null ? upgrades.getTabs() !=null ? upgrades.getTabs().length > 0 : false : false);
        	this.hasPockets = (upgrades !=null ? upgrades.hasUpgrade(BackpackUpgrade.POCKETS) : false);
        }
        
        this.xSize = 176+34+34+(hasPockets ? 34*2 : 0);
        slotRows = backpackInventory.getSize()/9;
        int topSpace = 16+(hasTabs ? 32 : 0);
        int bottomSize = 101;
        this.ySize = topSpace+(18*slotRows)+bottomSize;
    }

    private final ItemStack lockIconStack = new ItemStack(ModItems.lock);
    
    @Override
    public void initGui(){
    	super.initGui();
    	this.buttonList.clear();
    	int offsetLeft = 34+(hasPockets ? 34 : 0);
    	int offsetTop = hasTabs ? 32 : 0;
    	if(upgrades.getSize() > 0)this.buttonList.add(new GuiButton(0, guiLeft+offsetLeft+156, guiTop+offsetTop+5, 10, 10, "S"));
    }
    
    public void actionPerformed(GuiButton button){
    	if(button.id == 0){
    		BlockUtil.openWorksiteGui(CrystalMod.proxy.getClientPlayer(), GuiHandler.GUI_ID_BACKPACK, 0, 0, 1);
    		return;
    	}
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
    	int offsetLeft = 34+(hasPockets ? 34 : 0);
    	int offsetTop = hasTabs ? 32 : 0;
    	if(!ItemStackTools.isNullStack(backpack)){
    		String name = backpack.getDisplayName();
    		this.fontRendererObj.drawString(name, 8+offsetLeft, 6+offsetTop, 4210752);
    		if(BackpackUtil.getOwner(backpack) !=null){
    			GlStateManager.pushMatrix();
    			GlStateManager.translate(8+offsetLeft+(this.fontRendererObj.getStringWidth(name)), 4.5+(offsetTop), 0);
    			GlStateManager.scale(0.55, 0.55, 1);
    			this.itemRender.renderItemAndEffectIntoGUI(lockIconStack, 0, 0);
    			GlStateManager.popMatrix();
    		}
    	}
    	this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8+offsetLeft, 16+(18*slotRows)+5+offsetTop, 4210752);
    }

    public static final ResourceLocation baublesBackground = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(RES_LOC);
        
        int offsetTop = hasTabs ? 0 : 0;
        int tabOffset = hasTabs ? 32 : 0;
        int topSpace = 16;
        int gap = 18*3;
        int bottomSize = 101;
        int slotBlock = 18*slotRows;
        
        int gapLeft = hasPockets ? 34 : 0;
    	int offsetLeft = 34+(hasPockets ? gapLeft : 0);
    	

        //Pockets
        if(hasPockets){
        	int pocketY = topSpace+(18*(slotRows-1))+tabOffset;
        	this.drawTexturedModalRect(this.guiLeft+gapLeft+8, this.guiTop+pocketY-7, 176, 139, 32, 36);
        	this.drawTexturedModalRect(this.guiLeft+offsetLeft+(9*18)+8, this.guiTop+pocketY-7, 176, 139, 32, 36);
        }
        //this.drawTexturedModalRect(this.guiLeft+34, this.guiTop, 0, 0, xSize, ySize);
        this.drawTexturedModalRect(this.guiLeft+offsetLeft, this.guiTop+tabOffset, 0, 0, 176, topSpace);
        for(int i = 0; i < slotRows; i++){
        	int slotY = topSpace+(18*i);
        	this.drawTexturedModalRect(this.guiLeft+offsetLeft, guiTop+slotY+tabOffset, 0, topSpace, 176, 18);
        }
        this.drawTexturedModalRect(this.guiLeft+offsetLeft, guiTop+topSpace+slotBlock+tabOffset, 0, topSpace+gap, 176, bottomSize);
        
        this.drawTexturedModalRect(this.guiLeft+gapLeft, this.guiTop+(ySize-32), 176, 139, 32, 36);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+tabOffset, 208, 85, 32, 86);
       
        //Baubles
        ContainerBackpackNormal container = (ContainerBackpackNormal)this.inventorySlots;
        if (container.hasBaublesSlots()) {
        	this.drawTexturedModalRect(this.guiLeft+(xSize-32), this.guiTop+tabOffset, 208, 85, 32, 79);
            this.drawTexturedModalRect(this.guiLeft+(xSize-32), this.guiTop+tabOffset+79, 208, 110, 32, 61);
            this.mc.getTextureManager().bindTexture(baublesBackground);
	        for (int i = 0; i < container.baubles.getSlots(); i++) {
	            if (ItemStackTools.isEmpty(container.baubles.getStackInSlot(i))) {
	              final int textureX = 77 + (i / 4) * 19;
	              final int textureY = 8 + (i % 4) * 18;
	              drawTexturedModalRect(this.guiLeft+(xSize-32)+8, this.guiTop+tabOffset+8 + i * 18, textureX, textureY, 16, 16);
	            }
	        }
	        
	        this.mc.getTextureManager().bindTexture(RES_LOC);
        }
        
        //Tabs
        if(hasTabs){
	        int tabStart = this.guiLeft+offsetLeft;
	        BackpackUpgrade[] tabs = upgrades.getTabs();
	        for(int t = 0; t < tabs.length+1; t++){
	        	ItemStack stack = backpack;
	        	if(t > 0)stack = new ItemStack(ModItems.backpackupgrade, 1, tabs[t-1].getMetadata());
	        	this.renderTab(t, tabStart, tabOffset, stack, t == 0);
	        }
        }
    }
    
    
    @Override
	protected void keyTyped(char par1, int par2) throws IOException {
		/*if (par2 == CrystalMod.proxy.keyHandler.keyBack.getKeyCode())
        {
            this.mc.thePlayer.closeScreen();
        } else*/
		super.keyTyped(par1, par2);
	}
    
    public void renderTab(int index, int x, int y, ItemStack itemstack, boolean isSelected){
    	this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tabs.png"));
        boolean flag = isSelected;
        int i = index;
        int j = i * 28;
        int k = flag ? 32 : 0;
        int l = x + 28 * i;
        int i1 = this.guiTop+y-28;
        int j1 = 32;

        if (i > 0)
        {
            l += i;
        }
        

        GlStateManager.disableLighting();
        GlStateManager.color(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        GlStateManager.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        if(index == 0){
        	this.drawTexturedModalRect(l, i1, j, k, 28, 32);
        }
        else this.drawTexturedModalRect(l, i1, j, k, 28, (flag) ? 32 : 28);
        
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + 1;
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        this.itemRender.renderItemAndEffectIntoGUI(itemstack, l, i1);
        this.itemRender.renderItemOverlays(this.fontRendererObj, itemstack, l, i1);
        GlStateManager.disableLighting();
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }
    
    protected boolean isMouseOverTab(int index,  int mouseX, int mouseY)
    {
        int i = index;
        int offsetLeft = 34+(hasPockets ? 34 : 0);
        int j = offsetLeft+(28 * i);
        int k = 0;

        if (i == 5)
        {
            j = this.xSize - 28 + 2;
        }
        else if (i > 0)
        {
            j += i;
        }

        return mouseX >= j && mouseX <= j + 28 && mouseY >= k && mouseY <= k + 32;
    }
    
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

            BackpackUpgrade[] upgrade = upgrades.getTabs();
            for (int u = 0; u < upgrade.length; u++)
            {
                if (upgrade[u] !=null && this.isMouseOverTab(u+1, i, j))
                {
                	ModLogger.info("Tab: "+u);
                    return;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (state == 0)
        {
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

            BackpackUpgrade[] upgrade = upgrades.getTabs();
            for (int u = 0; u < upgrade.length; u++)
            {
                if (upgrade[u] !=null && this.isMouseOverTab(u+1, i, j))
                {
                    BlockUtil.openWorksiteGui(CrystalMod.proxy.getClientPlayer(), GuiHandler.GUI_ID_BACKPACK, OpenType.BACK.ordinal(), u+1, 1);
                    return;
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }
}
