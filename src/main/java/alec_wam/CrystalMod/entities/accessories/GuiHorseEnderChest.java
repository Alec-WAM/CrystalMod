package alec_wam.CrystalMod.entities.accessories;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHorseEnderChest extends GuiContainer
{
    private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation("crystalmod:textures/gui/entity/horse_enderchest.png");
    /** The player inventory bound to this GUI. */
    private final IInventory playerInventory;
    /** The horse inventory bound to this GUI. */
    private final IInventory horseInventory;
    /** The enderchest inventory bound to this GUI. */
    private final InventoryEnderChest enderChestInventory;
    /** The EntityHorse whose inventory is currently being accessed. */
    private final AbstractHorse horseEntity;
    /** The mouse x-position recorded during the last rendered frame. */
    private float mousePosx;
    /** The mouse y-position recorded during the last renderered frame. */
    private float mousePosY;

    public GuiHorseEnderChest(IInventory playerInv, IInventory horseInv, AbstractHorse horse)
    {
        super(new ContainerHorseEnderChest(playerInv, horseInv, horse, CrystalMod.proxy.getClientPlayer()));
        xSize = 176;
        ySize = 213;
        this.playerInventory = playerInv;
        this.enderChestInventory = CrystalMod.proxy.getClientPlayer().getInventoryEnderChest();
        this.horseInventory = horseInv;
        this.horseEntity = horse;
        this.allowUserInput = false;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(this.horseInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        if(this.enderChestInventory !=null){
        	GlStateManager.pushMatrix();
        	GlStateManager.translate(8, 60, 0);
        	GlStateManager.scale(0.7, 0.7, 0);
        	this.fontRendererObj.drawString(this.enderChestInventory.getDisplayName().getUnformattedText(), 0, 0, 4210752);
        	GlStateManager.popMatrix();
        }
        GlStateManager.pushMatrix();
    	GlStateManager.translate(8, this.ySize - 96 + 4, 0);
    	GlStateManager.scale(0.7, 0.7, 0);
    	this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 0, 0, 4210752);
    	GlStateManager.popMatrix();
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        GuiInventory.drawEntityOnScreen(i + 51 + 25, j + 60, 17, i + 51 - this.mousePosx, j + 75 - 50 - this.mousePosY, this.horseEntity);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.mousePosx = mouseX;
        this.mousePosY = mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}