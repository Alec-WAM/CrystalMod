package alec_wam.CrystalMod.items.tools.backpack.gui;

import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBackpackCrafting extends GuiContainer {

    private static final ResourceLocation RES_LOC = new ResourceLocation("crystalmod:textures/gui/backpack/backpack_crafting.png");
    
    public GuiBackpackCrafting(InventoryBackpack backpackInventory){
        super(new ContainerBackpackCrafting(backpackInventory));
        this.xSize = 176+34+34;
        this.ySize = 166;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
    	//this.fontRendererObj.drawString(text, x, y, color);
        //AssetUtil.displayNameString(this.fontRendererObj, this.xSize, -10, StringUtil.localize("container."+ModUtil.MOD_ID+"."+(this.isVoid ? "voidBag" : "bag")+".name"));
    }

    public static final ResourceLocation baublesBackground = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(RES_LOC);
        this.drawTexturedModalRect(this.guiLeft+34, this.guiTop, 0, 0, 176, 166);
        
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+(166-32), 176, 134, 32, 32);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 208, 80, 32, 86);
        
        //Baubles
        ContainerBackpackCrafting container = (ContainerBackpackCrafting)this.inventorySlots;
        if (container.hasBaublesSlots()) {
        	this.drawTexturedModalRect(this.guiLeft+(xSize-32), this.guiTop, 208, 80, 32, 79);
        	this.drawTexturedModalRect(this.guiLeft+(xSize-32), this.guiTop+79, 208, 105, 32, 61);
            //this.drawTexturedModalRect(this.guiLeft+(xSize-32), this.guiTop+79, 208, 110, 32, 61);
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
}
