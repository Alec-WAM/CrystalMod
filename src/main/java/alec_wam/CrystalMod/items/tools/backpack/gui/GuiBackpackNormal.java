package alec_wam.CrystalMod.items.tools.backpack.gui;

import java.io.IOException;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackOpenSource;
import alec_wam.CrystalMod.items.tools.backpack.types.NormalInventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
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
public class GuiBackpackNormal extends GuiContainer {

    private static final ResourceLocation RES_LOC = new ResourceLocation("crystalmod:textures/gui/backpack/normal.png");
    
    private ItemStack backpack;
    private int slotRows;
    private IBackpackOpenSource source;
    private InventoryBackpackUpgrades upgrades;
    private boolean hasPockets;
    private boolean hasTabs;
    
    public GuiBackpackNormal(NormalInventoryBackpack backpackInventory, InventoryPlayer invPlayer, IBackpackOpenSource source){
        super(new ContainerBackpackNormal(backpackInventory, invPlayer));
        this.backpack = source.getBackpack();
        this.source = source;
        
        if(ItemStackTools.isValid(backpack)){
        	this.upgrades = BackpackUtil.getUpgradeInventory(backpack);
        	this.hasTabs = (upgrades !=null ? upgrades.getTabs() !=null ? upgrades.getTabs().length > 0 : false : false);
        	this.hasPockets = (upgrades !=null ? upgrades.hasUpgrade(BackpackUpgrade.POCKETS) : false);
        }

        slotRows = backpackInventory.getSize()/9;
        int pocketOffset = 0;
        if(hasPockets){
        	if(slotRows < 6){
        		pocketOffset = 34*2;
        	}
        }
        this.xSize = 176+34+34+pocketOffset;
        int topSpace = 16+(hasTabs ? 32 : 0);
        int bottomSize = 101;
        this.ySize = topSpace+(18*slotRows)+bottomSize;
    }

    private final ItemStack lockIconStack = new ItemStack(ModItems.lock);
    
    public int getPocketOffset(){
    	if(hasPockets){
        	if(slotRows < 6){
        		return 34;
        	}
        }
    	return 0;
    }
    
    @Override
    public void initGui(){
    	super.initGui();
    	this.buttonList.clear();
    	int offsetLeft = 34+getPocketOffset();
    	int offsetTop = hasTabs ? 32 : 0;
    	if(upgrades.getSize() > 0)this.buttonList.add(new GuiButton(0, guiLeft+offsetLeft+156, guiTop+offsetTop+5, 10, 10, "S"));
    }
    
    @Override
	public void actionPerformed(GuiButton button){
    	if(button.id == 0){
    		source.openUpgradesInventory(0);
    		return;
    	}
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
    	int offsetLeft = 34+getPocketOffset();
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
    		
    		int upgradeIndex = 0;
    		for(int i = 0; i < upgrades.getSize(); i++){
    			ItemStack stack = upgrades.getStackInSlot(i);
    			if(ItemStackTools.isValid(stack)){
    				int upgradeX = offsetLeft + (170 - (16*upgrades.getUpgradeCount())) + 16*upgradeIndex;
    				int upgradeY = 16+(18*slotRows)+3+offsetTop;
    				GlStateManager.pushMatrix();
        			GlStateManager.translate(upgradeX, upgradeY, 0);
        			GlStateManager.scale(0.75, 0.75, 1);
        			GlStateManager.pushMatrix();
        			this.itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
        			GlStateManager.popMatrix();
        			GlStateManager.popMatrix();
    				upgradeIndex++;
    			}
    		}
    	}
    	this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8+offsetLeft, 16+(18*slotRows)+5+offsetTop, 4210752);
    }

    public static final ResourceLocation baublesBackground = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(RES_LOC);
        
        int tabOffset = hasTabs ? 32 : 0;
        int topSpace = 16;
        int gap = 18*3;
        int bottomSize = 101;
        int slotBlock = 18*slotRows;
        
        int gapLeft = getPocketOffset();
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
        
        //Offhand
        this.drawTexturedModalRect(this.guiLeft+gapLeft, this.guiTop+(ySize-32), 176, 139, 32, 36);
        
        //Armor
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
	        	if(t > 0)stack = new ItemStack(ModItems.backpackupgrade, 1, tabs[t-1].getMeta());
	        	this.renderTab(t, tabStart, tabOffset, stack, t == 0);
	        }
        }
        
        //drawRect(guiLeft, guiTop, guiLeft+xSize, guiTop+ySize, Color.RED.getRGB());
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
    
    @Override
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

            BackpackUpgrade[] upgrade = upgrades.getTabs();
            for (int u = 0; u < upgrade.length; u++)
            {
                if (upgrade[u] !=null && this.isMouseOverTab(u+1, i, j))
                {
                    source.openUpgradesInventory(u+1);
                	//BlockUtil.openWorksiteGui(CrystalMod.proxy.getClientPlayer(), GuiHandler.GUI_ID_BACKPACK, OpenType.BACK.ordinal(), u+1, 1);
                    return;
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }
}
