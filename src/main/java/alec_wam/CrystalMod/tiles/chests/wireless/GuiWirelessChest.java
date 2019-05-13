package alec_wam.CrystalMod.tiles.chests.wireless;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.util.ProfileUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiWirelessChest extends GuiContainer {

	private IWirelessChestSource chest;
	private IInventory playerInventory;
	
    public GuiWirelessChest(IInventory player, IWirelessChestSource chest)
    {
        super(new ContainerWirelessChest(player, chest));
        this.playerInventory = player;
        this.chest = chest;
        this.xSize = 184;
        this.ySize = 204;
        this.allowUserInput = false;
    }

    private final ResourceLocation texture = new ResourceLocation("crystalmod", "textures/gui/chest/wireless.png");
    
    @Override
    public void drawGuiContainerForegroundLayer(int par1, int par2){
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		String inventoryName = ModBlocks.wirelessChest.getNameTextComponent().getFormattedText();
		if(chest !=null){
			if(chest.isPrivate()){
				inventoryName = ProfileUtil.getUsernameClient(chest.getOwner());
			}
		}
		fontRenderer.drawString(inventoryName, 12, 6, 4210752);
		fontRenderer.drawString(playerInventory.getDisplayName().getUnformattedComponentText(), 12, 110, 4210752);
		super.drawGuiContainerForegroundLayer(par1, par2);
    }
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
