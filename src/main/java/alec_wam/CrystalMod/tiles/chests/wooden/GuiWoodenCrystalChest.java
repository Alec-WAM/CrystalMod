package alec_wam.CrystalMod.tiles.chests.wooden;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class GuiWoodenCrystalChest extends ContainerScreen<WoodenCrystalChestContainer> {
    public enum ResourceList {
        BLUE(new ResourceLocation("crystalmod", "textures/gui/chest/blue_wooden.png")),
        RED(new ResourceLocation("crystalmod", "textures/gui/chest/red_wooden.png")),
        GREEN(new ResourceLocation("crystalmod", "textures/gui/chest/green_wooden.png")),
        DARK(new ResourceLocation("crystalmod", "textures/gui/chest/dark_wooden.png"));
        public final ResourceLocation location;
        private ResourceList(ResourceLocation loc) {
            this.location = loc;
        }
    }
    public enum GUI {
    	BLUE(184, 202, ResourceList.BLUE, WoodenCrystalChestType.BLUE),
    	RED(184, 238, ResourceList.RED, WoodenCrystalChestType.RED),
        GREEN(184, 256, ResourceList.GREEN, WoodenCrystalChestType.GREEN),
        DARK(238, 256, ResourceList.DARK, WoodenCrystalChestType.DARK);

        private int xSize;
        private int ySize;
        private ResourceList guiResourceList;
        private WoodenCrystalChestType mainType;

        private GUI(int xSize, int ySize, ResourceList guiResourceList, WoodenCrystalChestType mainType)
        {
            this.xSize = xSize;
            this.ySize = ySize;
            this.guiResourceList = guiResourceList;
            this.mainType = mainType;

        }

        protected WoodenCrystalChestContainer makeContainer(int windowId, PlayerInventory player, IInventory chest)
        {
            return new WoodenCrystalChestContainer(windowId, player, chest, mainType, xSize, ySize);
        }

        public static GuiWoodenCrystalChest buildGUI(int windowId, WoodenCrystalChestType type, PlayerInventory playerInventory, IInventory chestInventory)
        {
            return new GuiWoodenCrystalChest(windowId, values()[type.ordinal()], playerInventory, chestInventory);
        }
    }

    public int getRowLength()
    {
        return type.mainType.getRowLength();
    }

    private GUI type;

    private GuiWoodenCrystalChest(int windowId, GUI type, PlayerInventory player, IInventory chest)
    {
        super(type.makeContainer(windowId, player, chest), player, new StringTextComponent("WoodenChest"));
        this.type = type;
        this.xSize = type.xSize;
        this.ySize = type.ySize;
        this.passEvents = false;
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
        // new "bind tex"
        this.minecraft.getTextureManager().bindTexture(type.guiResourceList.location);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        blit(x, y, 0, 0, xSize, ySize);
    }
}
