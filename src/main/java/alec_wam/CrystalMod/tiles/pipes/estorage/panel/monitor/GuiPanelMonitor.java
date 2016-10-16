package alec_wam.CrystalMod.tiles.pipes.estorage.panel.monitor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.PacketEStorageAddItem;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.GuiUtil;
import alec_wam.CrystalMod.util.client.Scrollbar;

import com.google.common.collect.Lists;

public class GuiPanelMonitor extends GuiContainer implements IGuiScreen {
    public static final int VISIBLE_ROWS = 3;

    public static final int ITEM_WIDTH = 72;
    public static final int ITEM_HEIGHT = 30;

    private TileEntityPanelMonitor craftingMonitor;

    private GuiButton cancelButton;
    private GuiButton cancelAllButton;

    private int itemSelected = -1;

    private boolean renderItemSelection;
    private int renderItemSelectionX;
    private int renderItemSelectionY;

    private Scrollbar scrollbar = new Scrollbar(157, 20, 12, 89);

    public GuiPanelMonitor(EntityPlayer player, TileEntityPanelMonitor craftingMonitor) {
        super(new ContainerPanelMonitor(player, craftingMonitor));
        xSize = this.width = 176;
        ySize = this.height = 230;
        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void initGui() {
    	super.initGui();
        String cancel = Lang.localize("gui.cancel", false);
        String cancelAll = Lang.localize("gui.cancel_all");

        int cancelButtonWidth = 14 + fontRendererObj.getStringWidth(cancel);
        int cancelAllButtonWidth = 14 + fontRendererObj.getStringWidth(cancelAll);

        cancelButton = new GuiButton(0, guiLeft + 7, guiTop + 113, cancelButtonWidth, 20, cancel);
        cancelAllButton = new GuiButton(1, guiLeft + 7 + cancelButtonWidth + 4, guiTop + 113, cancelAllButtonWidth, 20, cancelAll);
        buttonList.add(cancelButton);
        buttonList.add(cancelAllButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        scrollbar.update(mouseX - guiLeft, mouseY - guiTop);
    }

    @Override
    public void updateScreen() {
        scrollbar.setCanScroll(getRows() > VISIBLE_ROWS);
        scrollbar.setScrollDelta((float) scrollbar.getScrollbarHeight() / (float) getRows());

        if (itemSelected >= craftingMonitor.getTasks().size()) {
            itemSelected = -1;
        }

        cancelButton.enabled = itemSelected != -1;
        cancelAllButton.enabled = craftingMonitor.getTasks().size() > 0;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(new ResourceLocation("crystalmod:textures/gui/eStorage_crafting_monitor.png"));

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (renderItemSelection) {
            drawTexturedModalRect(guiLeft + renderItemSelectionX, guiTop + renderItemSelectionY, 178, 0, ITEM_WIDTH, ITEM_HEIGHT);
        }

        scrollbar.draw(this);
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        //drawString(7, 7, t("gui.refinedstorage:crafting_monitor"));
        GlStateManager.disableLighting();
    	drawString(fontRendererObj, Lang.localize("container.inventory", false), 7, 137, 4210752);
    	GlStateManager.enableLighting();
        int x = 8;
        int y = 20;

        int item = getOffset() * 2;

        RenderHelper.enableGUIStandardItemLighting();

        List<String> lines = Lists.newArrayList();

        renderItemSelection = false;

        for (int i = 0; i < 6; ++i) {
            if (item < craftingMonitor.getTasks().size()) {
                if (item == itemSelected) {
                    renderItemSelection = true;
                    renderItemSelectionX = x;
                    renderItemSelectionY = y;
                }

                TileEntityPanelMonitor.ClientSideCraftingTask task = craftingMonitor.getTasks().get(i);

                zLevel = 200.0F;
                itemRender.zLevel = 200.0F;
                itemRender.renderItemIntoGUI(task.output, x, y);
                zLevel = 0.0F;
                itemRender.zLevel = 0.0F;

                float scale = 0.5f;

                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1);
                int textX = GuiUtil.calculateOffsetOnScale(x + 20, scale);
                fontRendererObj.drawString(task.output.getDisplayName(), textX, GuiUtil.calculateOffsetOnScale(y + 4, scale), 4210752);

                GlStateManager.popMatrix();

                if (GuiUtil.inBounds(x, y, 16, 16, mouseX-guiLeft, mouseY-guiTop)) {
                	if(!task.info.trim().equals("")){
	                	String[] preFix = task.info.split("\n");
	                	for (int j = 0; j < preFix.length; ++j) {
	                		 String line = preFix[j];
	                		 if (line.startsWith("T=")) {
	                			 String data = line.substring(2);
	                			 String[] items = data.split("&");
	                			 for(String itemS : items){
	                				 lines.add(itemS.substring(0, 2)+Lang.localize(itemS.substring(2), false));
	                			 }
	                		 } else if (line.startsWith("I=")) {
	                			 line = TextFormatting.YELLOW + Lang.localize(line.substring(2), false);
	                			 lines.add(line);
	                		 }
	                		 
	                    }
                	}else {
                		lines.add("Empty Info");
                	}
                }
            }

            if (i == 1 || i == 3) {
                x = 8;
                y += ITEM_HEIGHT;
            } else {
                x += ITEM_WIDTH;
            }

            item++;
        }

        if (lines != null) {
            drawHoveringText(lines, mouseX-guiLeft, mouseY-guiTop);
        }
    }

    public int getOffset() {
        return (int) (scrollbar.getCurrentScroll() / 89f * (float) getRows());
    }

    private int getRows() {
        int max = (int) Math.ceil((float) craftingMonitor.getTasks().size() / (float) 2);

        return max < 0 ? 0 : max;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == cancelButton && itemSelected != -1) {
        	CrystalModNetwork.sendToServer(new PacketEStorageAddItem(5, craftingMonitor.getTasks().get(itemSelected).id, 0, new byte[0]));
        } else if (button == cancelAllButton && craftingMonitor.getTasks().size() > 0) {
        	CrystalModNetwork.sendToServer(new PacketEStorageAddItem(5, -1, 0, new byte[0]));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0 && GuiUtil.inBounds(8, 20, 144, 90, mouseX - guiLeft, mouseY - guiTop)) {
            itemSelected = -1;

            int item = getOffset() * 2;

            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 2; ++x) {
                    int ix = 8 + (x * ITEM_WIDTH);
                    int iy = 20 + (y * ITEM_HEIGHT);

                    if (GuiUtil.inBounds(ix, iy, ITEM_WIDTH, ITEM_HEIGHT, mouseX - guiLeft, mouseY - guiTop) && item < craftingMonitor.getTasks().size()) {
                        itemSelected = item;
                    }

                    item++;
                }
            }
        }
    }

	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}
	
}
