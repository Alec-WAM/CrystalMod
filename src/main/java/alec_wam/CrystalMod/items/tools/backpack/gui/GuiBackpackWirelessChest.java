package alec_wam.CrystalMod.items.tools.backpack.gui;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager.WirelessInventory;
import alec_wam.CrystalMod.util.ProfileUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBackpackWirelessChest extends GuiContainer {

	private InventoryBackpack backpackInv;
	private WirelessInventory wirelessInventory;
	private IInventory playerInventory;
	
    public GuiBackpackWirelessChest(InventoryBackpack backpackInv, WirelessInventory wirelessInventory)
    {
        super(new ContainerBackpackWirelessChest(backpackInv, wirelessInventory));
        this.playerInventory = backpackInv.getPlayer().inventory;
        this.wirelessInventory = wirelessInventory;
        this.backpackInv = backpackInv;
        this.xSize = 184;
        this.ySize = 204;
        this.allowUserInput = false;
    }

    private final ResourceLocation texture = new ResourceLocation("crystalmod", "textures/gui/chest/wirelesscontainer.png");
    
    @Override
    public void drawGuiContainerForegroundLayer(int par1, int par2){
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		String inventoryName = backpackInv.getName();
		UUID owner = BackpackUtil.getOwner(backpackInv.getBackpack());
		if(wirelessInventory !=null && owner !=null){
			inventoryName = ProfileUtil.getUsername(owner);
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
