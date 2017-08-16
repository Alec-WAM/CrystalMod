package alec_wam.CrystalMod.tiles.chest;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GUIChest extends GuiContainer {
    public enum ResourceList {
        BLUE(new ResourceLocation("crystalmod", "textures/gui/chest/bluecontainer.png")),
        RED(new ResourceLocation("crystalmod", "textures/gui/chest/redcontainer.png")),
        GREEN(new ResourceLocation("crystalmod", "textures/gui/chest/greencontainer.png")),
        DARK(new ResourceLocation("crystalmod", "textures/gui/chest/darkcontainer.png")),
        PURE(new ResourceLocation("crystalmod", "textures/gui/chest/purecontainer.png")),
        DARKIRON(new ResourceLocation("crystalmod", "textures/gui/chest/darkIroncontainer.png"));
        public final ResourceLocation location;
        private ResourceList(ResourceLocation loc) {
            this.location = loc;
        }
    }
    public enum GUI {
    	BLUE(184, 202, ResourceList.BLUE, CrystalChestType.BLUE),
    	RED(184, 238, ResourceList.RED, CrystalChestType.RED),
        GREEN(184, 256, ResourceList.GREEN, CrystalChestType.GREEN),
        DARK(238, 256, ResourceList.DARK, CrystalChestType.DARK),
        PURE(238, 256, ResourceList.PURE, CrystalChestType.PURE),
        DARKIRON(184, 184, ResourceList.DARKIRON, CrystalChestType.DARKIRON);

        private int xSize;
        private int ySize;
        private ResourceList guiResourceList;
        private CrystalChestType mainType;

        private GUI(int xSize, int ySize, ResourceList guiResourceList, CrystalChestType mainType)
        {
            this.xSize = xSize;
            this.ySize = ySize;
            this.guiResourceList = guiResourceList;
            this.mainType = mainType;

        }

        protected Container makeContainer(IInventory player, IInventory chest)
        {
            return new ContainerCrystalChest(player, chest, mainType, xSize, ySize);
        }

        public static GUIChest buildGUI(CrystalChestType type, IInventory playerInventory, IInventory chestInventory)
        {
            return new GUIChest(values()[type.ordinal()], playerInventory, chestInventory);
        }
    }

    public int getRowLength()
    {
        return type.mainType.getRowLength();
    }

    private GUI type;

    private GUIChest(GUI type, IInventory player, IInventory chest)
    {
        super(type.makeContainer(player, chest));
        this.type = type;
        this.xSize = type.xSize;
        this.ySize = type.ySize;
        this.allowUserInput = false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        // new "bind tex"
        this.mc.getTextureManager().bindTexture(type.guiResourceList.location);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
