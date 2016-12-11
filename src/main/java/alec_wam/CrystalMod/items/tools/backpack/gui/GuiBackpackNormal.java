package alec_wam.CrystalMod.items.tools.backpack.gui;

import java.io.IOException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class GuiBackpackNormal extends GuiContainer {

    private static final ResourceLocation RES_LOC = new ResourceLocation("crystalmod:textures/gui/backpack/normal.png");
    
    private ItemStack backpack;
    private int slotRows;
    public GuiBackpackNormal(InventoryBackpack backpackInventory){
        super(new ContainerBackpackNormal(backpackInventory));
        this.backpack = backpackInventory.getBackpack();
        this.xSize = 176+34+34;
        slotRows = backpackInventory.getSizeInventory()/9;
        int topSpace = 16;
        int bottomSize = 101;
        this.ySize = topSpace+(18*slotRows)+bottomSize;
    }

    private final ItemStack lockIconStack = new ItemStack(ModItems.lock);
    
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
    	
    	if(!ItemStackTools.isNullStack(backpack)){
    		String name = backpack.getDisplayName();
    		this.fontRendererObj.drawString(name, 8+34, 6, 4210752);
    		if(BackpackUtil.getOwner(backpack) !=null){
    			GlStateManager.pushMatrix();
    			GlStateManager.translate(8+34+(this.fontRendererObj.getStringWidth(name)), 4.5, 0);
    			GlStateManager.scale(0.55, 0.55, 1);
    			this.itemRender.renderItemAndEffectIntoGUI(lockIconStack, 0, 0);
    			GlStateManager.popMatrix();
    		}
    	}
    	this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8+34, 16+(18*slotRows)+5, 4210752);
    }

    public static final ResourceLocation baublesBackground = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(RES_LOC);
        
        int topSpace = 16;
        int gap = 18*3;
        int bottomSize = 101;
        int slotBlock = 18*slotRows;
        //this.drawTexturedModalRect(this.guiLeft+34, this.guiTop, 0, 0, xSize, ySize);
        this.drawTexturedModalRect(this.guiLeft+34, this.guiTop, 0, 0, 176, topSpace);
        for(int i = 0; i < slotRows; i++){
        	int slotY = topSpace+(18*i);
        	this.drawTexturedModalRect(this.guiLeft+34, guiTop+slotY, 0, topSpace, 176, 18);
        }
        this.drawTexturedModalRect(this.guiLeft+34, guiTop+topSpace+slotBlock, 0, topSpace+gap, 176, bottomSize);
        
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+(ySize-32), 176, 139, 32, 36);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 208, 85, 32, 86);
       
        //Baubles
        ContainerBackpackNormal container = (ContainerBackpackNormal)this.inventorySlots;
        if (container.hasBaublesSlots()) {
        	this.drawTexturedModalRect(this.guiLeft+(xSize-32), this.guiTop, 208, 85, 32, 79);
            this.drawTexturedModalRect(this.guiLeft+(xSize-32), this.guiTop+79, 208, 110, 32, 61);
            this.mc.getTextureManager().bindTexture(baublesBackground);
	        for (int i = 0; i < container.baubles.getSlots(); i++) {
	            if (ItemStackTools.isNullStack(container.baubles.getStackInSlot(i))) {
	              final int textureX = 77 + (i / 4) * 19;
	              final int textureY = 8 + (i % 4) * 18;
	              drawTexturedModalRect(this.guiLeft+(xSize-32)+8, this.guiTop+8 + i * 18, textureX, textureY, 16, 16);
	            }
	        }
	        
	        this.mc.getTextureManager().bindTexture(RES_LOC);
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
}
