package alec_wam.CrystalMod.items.tools.backpack.upgrade;

import java.awt.Color;
import java.io.IOException;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackOpenSource;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ContainerBackpackUpgradeWindow.UpgradeWindowType;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBackpackUpgradeWindow extends GuiContainer {

    private static final ResourceLocation RES_LOC = new ResourceLocation("crystalmod:textures/gui/backpack/normal.png");
    private static final ResourceLocation RES_LOC_SINGLESLOT = new ResourceLocation("crystalmod:textures/gui/backpack/tab_upgrade_singleslot.png");

    private ItemStack backpack;
    private InventoryBackpackUpgrades upgrades;
    private BackpackUpgrade upgrade;
    private IBackpackOpenSource source;
    private boolean hasPockets;
    public GuiBackpackUpgradeWindow(InventoryPlayer player, InventoryBackpackUpgrades upgrades, BackpackUpgrade upgrade, IBackpackOpenSource source){
        super(new ContainerBackpackUpgradeWindow(player, upgrades, upgrade));
        this.source = source;
        this.upgrades = upgrades;
        this.backpack = upgrades.getBackpack();
        this.upgrade = upgrade;
        
        this.hasPockets = (upgrades !=null ? upgrades.hasUpgrade(BackpackUpgrade.POCKETS) : false);
        
        this.xSize = 176+34+34+(hasPockets ? 34*2 : 0);
        
        int topSpace = 48+(18*3);
        int bottomSize = 101;
        this.ySize = topSpace+bottomSize;
    }
    
    @Override
    public void initGui(){
    	super.initGui();
    }
    
    @Override
	public void actionPerformed(GuiButton button){
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
    	int offsetLeft = 34+(hasPockets ? 34 : 0);
    	int offsetTop = 32;
    	/*if(!ItemStackTools.isNullStack(backpack)){
    		String name = backpack.getDisplayName();
    		this.fontRendererObj.drawString(name, 8+offsetLeft, 6+offsetTop, 4210752);
    		if(BackpackUtil.getOwner(backpack) !=null){
    			GlStateManager.pushMatrix();
    			GlStateManager.translate(8+offsetLeft+(this.fontRendererObj.getStringWidth(name)), 4.5+(offsetTop), 0);
    			GlStateManager.scale(0.55, 0.55, 1);
    			this.itemRender.renderItemAndEffectIntoGUI(lockIconStack, 0, 0);
    			GlStateManager.popMatrix();
    		}
    	}*/
    	this.fontRendererObj.drawString(Lang.translateToLocal("item.crystalmod.backpackupgrade."+upgrade.getName()+".name"), 8+offsetLeft, 6+offsetTop, 4210752);
    	this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8+offsetLeft, 16+(18*3)+5+offsetTop, 4210752);
    }

    public static final ResourceLocation baublesBackground = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        boolean singleSlot = upgrade.windowType == UpgradeWindowType.SINGLESLOT;
        this.mc.getTextureManager().bindTexture(singleSlot ? RES_LOC_SINGLESLOT : RES_LOC);
        
        int tabOffset = 32;
        int topSpace = 16;
        int gap = 18*3;
        int bottomSize = 101;
        int slotBlock = 18*3;
        
        int gapLeft = hasPockets ? 34 : 0;
    	int offsetLeft = 34+(hasPockets ? gapLeft : 0);

        //Pockets
        if(hasPockets){
        	int pocketY = topSpace+(36)+tabOffset;
        	this.drawTexturedModalRect(this.guiLeft+gapLeft+8, this.guiTop+pocketY-7, 176, 139, 32, 36);
        	this.drawTexturedModalRect(this.guiLeft+offsetLeft+(9*18)+8, this.guiTop+pocketY-7, 176, 139, 32, 36);
        }
        //this.drawTexturedModalRect(this.guiLeft+34, this.guiTop, 0, 0, xSize, ySize);
        //this.drawTexturedModalRect(this.guiLeft+offsetLeft, this.guiTop+tabOffset, 0, 0, 176, topSpace);
        
        if(singleSlot){
        	int slotY = topSpace;
        	this.drawTexturedModalRect(this.guiLeft+offsetLeft, guiTop+slotY+tabOffset, 0, topSpace, 176, 18*3);
        }
        else {
	        for(int i = 0; i < 3; i++){
	        	int slotY = topSpace+(18*i);
	        	this.drawTexturedModalRect(this.guiLeft+offsetLeft, guiTop+slotY+tabOffset, 0, topSpace, 176, 18);
	        }
        }
        this.drawTexturedModalRect(this.guiLeft+offsetLeft, guiTop+topSpace+slotBlock+tabOffset, 0, topSpace+gap, 176, bottomSize);
        
        this.drawTexturedModalRect(this.guiLeft+gapLeft, this.guiTop+(ySize-32), 176, 139, 32, 36);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+tabOffset, 208, 85, 32, 86);
       
        //Baubles
        ContainerBackpackUpgradeWindow container = (ContainerBackpackUpgradeWindow)this.inventorySlots;
        if (container.hasBaublesSlots()) {
        	this.drawTexturedModalRect(this.guiLeft+(xSize-32), this.guiTop+tabOffset, 208, 85, 32, 79);
            this.drawTexturedModalRect(this.guiLeft+(xSize-32), this.guiTop+tabOffset+79, 208, 110, 32, 61);
            this.mc.getTextureManager().bindTexture(baublesBackground);
	        for (int i = 0; i < container.baubles.getSlots(); i++) {
	            if (ItemStackTools.isNullStack(container.baubles.getStackInSlot(i))) {
	              final int textureX = 77 + (i / 4) * 19;
	              final int textureY = 8 + (i % 4) * 18;
	              drawTexturedModalRect(this.guiLeft+(xSize-32)+8, this.guiTop+tabOffset+8 + i * 18, textureX, textureY, 16, 16);
	            }
	        }
	        
	        this.mc.getTextureManager().bindTexture(RES_LOC);
        }
        
        //Tabs
        if(true){
	        int tabStart = this.guiLeft+offsetLeft;
	        BackpackUpgrade[] tabs = upgrades.getTabs();
	        int guiTab = 0;
	        for(int i = 0; i < tabs.length; i++){
	        	if(tabs[i] == this.upgrade){
	        		guiTab = i+1;
	        		break;
	        	}
	        }
	        
	        for(int t = 0; t < tabs.length+1; t++){
		        int index = t;
		        boolean flag = index == guiTab;
		        if(flag)continue;
		        ItemStack stack = backpack;
	        	if(t > 0)stack = new ItemStack(ModItems.backpackupgrade, 1, tabs[t-1].getMeta());
		        renderTab(index, tabStart, tabOffset, stack, false);
	        }
	        this.mc.getTextureManager().bindTexture(RES_LOC);
	        this.drawTexturedModalRect(this.guiLeft+offsetLeft, this.guiTop+tabOffset, 0, 0, 176, topSpace);
	        
	        ItemStack itemstack = new ItemStack(ModItems.backpackupgrade, 1, upgrade.getMeta());
	        renderTab(guiTab, tabStart, tabOffset, itemstack, true);
        }
    }
    

    
    public void renderTab(int index, int x, int y, ItemStack itemstack, boolean isSelected){
    	this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tabs.png"));
        boolean flag = isSelected;
        int i = index;
        int j = i * 28;
        int k = flag ? 32 : 0;
        int l = x + 28 * i;
        int i1 = this.guiTop+y-28;
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
    
    @Override
	protected void keyTyped(char par1, int par2) throws IOException {
		/*if (par2 == CrystalMod.proxy.keyHandler.keyBack.getKeyCode())
        {
            this.mc.thePlayer.closeScreen();
        } else*/
		super.keyTyped(par1, par2);
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
    
    @Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

            if(this.isMouseOverTab(0, i, j)){
            	return;
            }
            
            BackpackUpgrade[] upgrade = upgrades.getTabs();
            for (int u = 0; u < upgrade.length; u++)
            {
                if (upgrade[u] !=null && this.isMouseOverTab(u+1, i, j))
                {
                	return;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     */
    @Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (state == 0)
        {
            int i = mouseX - this.guiLeft;
            int j = mouseY - this.guiTop;

            if(this.isMouseOverTab(0, i, j)){
            	source.openMainInventory();
                return;
            }
            
            BackpackUpgrade[] upgrade = upgrades.getTabs();
            for (int u = 0; u < upgrade.length; u++)
            {
                if (upgrade[u] !=null && this.isMouseOverTab(u+1, i, j))
                {
                	source.openUpgradesInventory(u+1);
                    return;
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }
    
    @Override
    public void onGuiClosed(){
    	super.onGuiClosed();
    }
}
