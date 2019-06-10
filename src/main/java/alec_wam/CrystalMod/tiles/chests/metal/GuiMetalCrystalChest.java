package alec_wam.CrystalMod.tiles.chests.metal;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class GuiMetalCrystalChest extends ContainerScreen<ContainerMetalCrystalChest> {
    public enum ResourceList {
        BLUE(new ResourceLocation("crystalmod", "textures/gui/chest/blue_metal.png")),
        RED(new ResourceLocation("crystalmod", "textures/gui/chest/red_metal.png")),
        GREEN(new ResourceLocation("crystalmod", "textures/gui/chest/green_metal.png")),
        DARK(new ResourceLocation("crystalmod", "textures/gui/chest/dark_metal.png")),
        PURE(new ResourceLocation("crystalmod", "textures/gui/chest/pure_metal.png")),
        DARK_IRON(new ResourceLocation("crystalmod", "textures/gui/chest/darkiron_metal.png"));
    	
        public final ResourceLocation location;
        private ResourceList(ResourceLocation loc) {
            this.location = loc;
        }
    }
    public enum GUI {
    	BLUE(184, 202, ResourceList.BLUE, MetalCrystalChestType.BLUE),
    	RED(184, 238, ResourceList.RED, MetalCrystalChestType.RED),
        GREEN(184, 256, ResourceList.GREEN, MetalCrystalChestType.GREEN),
        DARK(238, 256, ResourceList.DARK, MetalCrystalChestType.DARK),
        PURE(238, 256, ResourceList.PURE, MetalCrystalChestType.PURE),
        DARK_IRON(184, 184, ResourceList.DARK_IRON, MetalCrystalChestType.DARKIRON);

        private int xSize;
        private int ySize;
        private ResourceList guiResourceList;
        private MetalCrystalChestType mainType;

        private GUI(int xSize, int ySize, ResourceList guiResourceList, MetalCrystalChestType mainType)
        {
            this.xSize = xSize;
            this.ySize = ySize;
            this.guiResourceList = guiResourceList;
            this.mainType = mainType;

        }

        protected ContainerMetalCrystalChest makeContainer(int windowId, PlayerInventory player, IInventory chest)
        {
            return new ContainerMetalCrystalChest(windowId, player, chest, mainType, xSize, ySize);
        }

        public static GuiMetalCrystalChest buildGUI(int windowId, MetalCrystalChestType type, PlayerInventory playerInventory, IInventory chestInventory)
        {
            return new GuiMetalCrystalChest(windowId, values()[type.ordinal()], playerInventory, chestInventory);
        }
    }

    public int getRowLength()
    {
        return type.mainType.getRowLength();
    }

    private GUI type;

    private GuiMetalCrystalChest(int windowId, GUI type, PlayerInventory player, IInventory chest)
    {
        super(type.makeContainer(windowId, player, chest), player, new StringTextComponent("MetalChest"));
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
        this.blit(x, y, 0, 0, xSize, ySize);
    }
}
