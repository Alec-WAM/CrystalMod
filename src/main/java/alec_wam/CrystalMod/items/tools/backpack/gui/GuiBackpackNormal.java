package alec_wam.CrystalMod.items.tools.backpack.gui;

import java.io.IOException;

import alec_wam.CrystalMod.CrystalMod;
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
    
    public GuiBackpackNormal(InventoryBackpack backpackInventory){
        super(new ContainerBackpackNormal(backpackInventory));
        this.backpack = backpackInventory.getBackpack();
        this.xSize = 176+34+34;
        this.ySize = 171;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
    	
    	if(!ItemStackTools.isNullStack(backpack))this.fontRendererObj.drawString(backpack.getDisplayName(), 8+34, 6, 4210752);
    	this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8+34, 76, 4210752);
    }

    public static final ResourceLocation baublesBackground = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(RES_LOC);
        this.drawTexturedModalRect(this.guiLeft+34, this.guiTop, 0, 0, 176, 171);
        
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+(171-32), 176, 139, 32, 32);
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
