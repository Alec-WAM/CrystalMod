package alec_wam.CrystalMod.items.tools.backpack.gui;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
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
    public OpenType openType;
    
    public GuiBackpackNormal(InventoryPlayer inventory, OpenType type){
        super(new ContainerBackpackNormal(inventory, type));
        openType = type;
        this.xSize = 176+34+34;
        this.ySize = 171;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
    	
    	ItemStack backpack = BackpackUtil.getItemStack(mc.thePlayer.inventory, openType);
    	
    	if(backpack !=null)this.fontRendererObj.drawString(backpack.getDisplayName(), 8+34, 6, 4210752);
    	this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8+34, 76, 4210752);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(RES_LOC);
        this.drawTexturedModalRect(this.guiLeft+34, this.guiTop, 0, 0, 176, 171);
        
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+(171-32), 176, 139, 32, 32);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 208, 85, 32, 86);
    }
}
