package alec_wam.CrystalMod.tiles.chests.wireless;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.util.ProfileUtil;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class GuiWirelessChest extends ContainerScreen<ContainerWirelessChest> {

	private IWirelessChestSource chest;
	private PlayerInventory playerInventory;
	
    public GuiWirelessChest(int windowId, PlayerInventory player, IWirelessChestSource chest)
    {
        super(new ContainerWirelessChest(windowId, player, chest), player, new StringTextComponent("WirelessChest"));
        this.playerInventory = player;
        this.chest = chest;
        this.xSize = 184;
        this.ySize = 204;
        this.passEvents = false;
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
		font.drawString(inventoryName, 12, 6, 4210752);
		font.drawString(playerInventory.getDisplayName().getUnformattedComponentText(), 12, 110, 4210752);
		super.drawGuiContainerForegroundLayer(par1, par2);
    }
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.blit(x, y, 0, 0, xSize, ySize);
    }
}
