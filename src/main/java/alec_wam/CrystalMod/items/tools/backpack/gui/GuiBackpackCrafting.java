package alec_wam.CrystalMod.items.tools.backpack.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class GuiBackpackCrafting extends GuiContainer {

    private static final ResourceLocation RES_LOC = new ResourceLocation("crystalmod:textures/gui/backpack/backpack_crafting.png");
    public OpenType openType;
    
    public GuiBackpackCrafting(InventoryPlayer inventory, OpenType type){
        super(new ContainerBackpackCrafting(inventory, type));
        this.openType = type;
        this.xSize = 176+34+34;
        this.ySize = 166;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
    	//this.fontRendererObj.drawString(text, x, y, color);
        //AssetUtil.displayNameString(this.fontRendererObj, this.xSize, -10, StringUtil.localize("container."+ModUtil.MOD_ID+"."+(this.isVoid ? "voidBag" : "bag")+".name"));
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(RES_LOC);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
        
        this.drawTexturedModalRect(this.guiLeft+34, this.guiTop, 0, 0, 176, 166);
        
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+(166-32), 176, 134, 32, 32);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 208, 80, 32, 86);
    }
}
