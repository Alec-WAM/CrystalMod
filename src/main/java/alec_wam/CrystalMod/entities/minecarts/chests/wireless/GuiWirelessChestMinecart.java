package alec_wam.CrystalMod.entities.minecarts.chests.wireless;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.ProfileUtil;

public class GuiWirelessChestMinecart extends GuiContainer {

	private EntityWirelessChestMinecart minecart;
	private IInventory playerInventory;
	
    public GuiWirelessChestMinecart(IInventory player, EntityWirelessChestMinecart minecart)
    {
        super(new ContainerWirelessChestMinecart(player, minecart));
        this.playerInventory = player;
        this.minecart = minecart;
        this.xSize = 184;
        this.ySize = 204;
        this.allowUserInput = false;
    }

    private final ResourceLocation texture = new ResourceLocation("crystalmod", "textures/gui/chest/wirelesscontainer.png");
    
    @Override
    public void drawGuiContainerForegroundLayer(int par1, int par2){
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		String inventoryName = minecart.getName();
		if(minecart !=null){
			if(minecart.isBoundToPlayer()){
				inventoryName = ProfileUtil.getUsername(minecart.getOwner());
			}
		}
		fontRendererObj.drawString(inventoryName, 12, 6, 4210752);
		fontRendererObj.drawString(playerInventory.getDisplayName().getUnformattedText(), 12, 110, 4210752);
		super.drawGuiContainerForegroundLayer(par1, par2);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        // new "bind tex"
        this.mc.getTextureManager().bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
