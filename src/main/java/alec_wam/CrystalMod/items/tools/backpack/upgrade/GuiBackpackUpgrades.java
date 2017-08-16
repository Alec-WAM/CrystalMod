package alec_wam.CrystalMod.items.tools.backpack.upgrade;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBackpackUpgrades extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("crystalmod:textures/gui/backpack/upgrade.png");
    private final IInventory playerInventory;
    private final InventoryBackpackUpgrades upgradeInventory;

    public GuiBackpackUpgrades(InventoryPlayer playerInv, InventoryBackpackUpgrades upgradeInventory)
    {
        super(new ContainerBackpackUpgrades(playerInv, upgradeInventory));
        this.playerInventory = playerInv;
        this.upgradeInventory = upgradeInventory;
        this.allowUserInput = false;
        this.ySize = 133;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        //this.fontRendererObj.drawString(this.hopperInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        int y = 19;
        int x = 43;
        int slotCount = upgradeInventory.getSize();
        int max = 5;
        int slotPos = x+(9*(max-slotCount));
        for(int s = 0; s < slotCount; s++){
        	this.drawTexturedModalRect(i + slotPos + (18*s), j + y, 176, 0, 18, 18);
        }
    }
}