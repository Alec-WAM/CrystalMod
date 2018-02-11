package alec_wam.CrystalMod.tiles.machine.epst;

import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEPST extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("crystalmod:textures/gui/epst.png");
    /** The player inventory bound to this GUI. */
    private final InventoryPlayer playerInventory;
    /** The inventory contained within the corresponding Dispenser. */
    public IInventory dispenserInventory;

    public GuiEPST(InventoryPlayer playerInv, IInventory dispenserInv)
    {
        super(new ContainerEPST(playerInv, dispenserInv));
        this.playerInventory = playerInv;
        this.dispenserInventory = dispenserInv;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = ModBlocks.epst.getLocalizedName();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}