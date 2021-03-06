package alec_wam.CrystalMod.tiles.pipes.estorage.power;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;
import alec_wam.CrystalMod.tiles.pipes.estorage.power.TileNetworkPowerCore.ClientPowerTileInfo;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.StringUtils;
import alec_wam.CrystalMod.util.client.GuiUtil;
import alec_wam.CrystalMod.util.client.Scrollbar;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiPowerCore extends GuiContainer implements IGuiScreen {

	private TileNetworkPowerCore core;
	private Scrollbar scrollbar = new Scrollbar(157, 20, 12, 89);
	public GuiPowerCore(EntityPlayer player, TileNetworkPowerCore core) {
		super(new ContainerPowerCore(player, core));
		xSize = this.width = 176;
        ySize = this.height = 230;
		this.core = core;
	}
	ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/eStorage_powercore.png");
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        scrollbar.update(mouseX - guiLeft, mouseY - guiTop);
    }
	public static final int VISIBLE_ROWS = 3;

    public static final int ITEM_WIDTH = 72;
    public static final int ITEM_HEIGHT = 30;
    @Override
    public void updateScreen() {
        scrollbar.setCanScroll(getRows() > VISIBLE_ROWS);
        scrollbar.setScrollDelta((float) scrollbar.getScrollbarHeight() / (float) getRows());
    }
    
    public int getOffset() {
        return (int) (scrollbar.getCurrentScroll() / 89f * getRows());
    }
    
    private int getRows() {
    	if(core == null || core.info == null)return 0;
        int max = (int) Math.ceil((float) core.info.infoList.size() / (float) 2);

        return max < 0 ? 0 : max;
    }
    
	@Override
    protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
        scrollbar.draw(this);
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    	this.fontRendererObj.drawString(ModBlocks.powerCore.getLocalizedName(), 8, 8, 4210752);
		if(core !=null){
    		if(core.info !=null){
    			this.fontRendererObj.drawString("Power: "+StringUtils.convertToCommas(core.info.storedEnergy)+" / "+StringUtils.convertToCommas(core.info.maxEnergy)+" "+Lang.localize("power.cu"), 10, 115, 0);
    			this.fontRendererObj.drawString("Usage: "+core.info.energyUsage+" "+Lang.localize("power.cu"), 10, 115+this.fontRendererObj.FONT_HEIGHT+5, 0);
    			
    			GlStateManager.enableLighting();
    	        int x = 8;
    	        int y = 20;

    	        int item = getOffset() * 2;

    	        RenderHelper.enableGUIStandardItemLighting();

    	        ClientPowerTileInfo hoveringInfo = null;
    	        for (int i = 0; i < 6; ++i) {
    	            if (item < core.info.infoList.size()) {
    	                ClientPowerTileInfo powertile = core.info.infoList.get(item);

    	                if(ItemStackTools.isValid(powertile.stack)){
    	                	zLevel = 200.0F;
    	                	itemRender.zLevel = 200.0F;
    	                	itemRender.renderItemIntoGUI(powertile.stack, x, y);
    	                	zLevel = 0.0F;
    	                	itemRender.zLevel = 0.0F;
    	                	
    	                	float scale = 0.5f;
        	                GlStateManager.pushMatrix();
        	                GlStateManager.scale(scale, scale, 1);
        	                int textX = GuiUtil.calculateOffsetOnScale(x + 20, scale);
        	                fontRendererObj.drawSplitString(powertile.stack.getDisplayName(), textX, GuiUtil.calculateOffsetOnScale(y + 4, scale), 100, 4210752);
        	                fontRendererObj.drawString("x"+powertile.count, textX, GuiUtil.calculateOffsetOnScale(y + 14, scale), 4210752);

        	                GlStateManager.popMatrix();
    	                }
    	                

    	                if (GuiUtil.inBounds(x, y, 16, 16, mouseX-guiLeft, mouseY-guiTop)) {
    	                	hoveringInfo = powertile;
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

    	        if (hoveringInfo != null) {
    	            drawCreativeTabHoveringText(hoveringInfo.usage+" "+Lang.localize("power.cu")+"/t", mouseX-guiLeft, mouseY-guiTop);
    	        }
    		}
    	}
    }

}
