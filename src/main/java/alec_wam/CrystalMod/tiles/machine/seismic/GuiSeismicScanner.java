package alec_wam.CrystalMod.tiles.machine.seismic;

import java.awt.Color;
import java.awt.Polygon;
import java.io.IOException;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.VScrollbar;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class GuiSeismicScanner extends GuiScreen implements IGuiScreen {

	public TileEntitySeismicScanner scanner;
	private int guiLeft, guiTop;
	private float zoom = 1.0F;
	private VScrollbar scrollbarY;
	protected VScrollbar draggingScrollbar;
	
	
	public GuiSeismicScanner(TileEntitySeismicScanner scanner){
		this.scanner = scanner;
		ModLogger.info("Scanner");
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		this.guiLeft = (this.width - 176) / 2;	
        this.guiTop = (this.height - 190) / 2;
		this.buttonList.add(new GuiButton(0, guiLeft + 25, guiTop + 135, 34, 12, "Scan"));
		this.buttonList.add(new GuiButton(1, guiLeft + 65, guiTop + 135, 12, 12, "-"));
		this.buttonList.add(new GuiButton(2, guiLeft + 95, guiTop + 135, 12, 12, "+"));
		
		scrollbarY = new VScrollbar(this, 153, 7, 125);
		scrollbarY.adjustPosition();
	}
	
	@Override
	public void actionPerformed(GuiButton button) throws IOException{
		if(button.id == 0){
			if(scanner !=null){
				CrystalModNetwork.sendToServer(new PacketTileMessage(scanner.getPos(), TileEntitySeismicScanner.MESSAGE_SCAN));
				needsUpdate = true;
				return;
			}
		}
		if(button.id == 1 || button.id == 2){
			if(scanner !=null){
				int value = scanner.radius;
				boolean change = false;
				if(button.id == 1 && value > 1){
					value--;
					change = true;
				}
				if(button.id == 2 && value < 16){
					value++;
					change = true;
				}
				if(change){
					NBTTagCompound nbt = new NBTTagCompound();
					scanner.radius = value;
					nbt.setInteger("Value", value);
					CrystalModNetwork.sendToServer(new PacketTileMessage(scanner.getPos(), TileEntitySeismicScanner.MESSAGE_RADIUS, nbt));
				}
				return;
			}
		}
		super.actionPerformed(button);
	}
	
	@Override
	public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if(scroll < 0) {
        	if(zoom > 1.0){
        		zoom -=0.05;
        		if(zoom < 1.0)zoom = 1.0f;
        	}
        }
        if(scroll > 0) {
        	if(zoom < 5.0){
        		zoom +=0.05;
        		if(zoom > 5.0)zoom = 5.0f;
        	}
        }
        //this.scrollbar.mouseWheel(x, y, i);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (draggingScrollbar != null) {
			draggingScrollbar.mouseClicked(mouseX, mouseY, mouseButton);
        	return;
		}
		
		if(this.scrollbarY !=null){
			if (scrollbarY.mouseClicked(mouseX, mouseY, mouseButton)) {
				draggingScrollbar = scrollbarY;
				return;
			}
		}
    }
	
	@Override
	protected void mouseReleased(int x, int y, int button) {
		if (draggingScrollbar != null) {
			draggingScrollbar.mouseMovedOrUp(x, y, button);
			draggingScrollbar = null;
		}
		super.mouseReleased(x, y, button);
	}

	@Override
	protected void mouseClickMove(int x, int y, int button, long time) {
		if (draggingScrollbar != null) {
			draggingScrollbar.mouseClickMove(x, y, button, time);
			return;
		}
		super.mouseClickMove(x, y, button, time);
	}
	
	public static final ResourceLocation TEXTURE = CrystalMod.resourceL("textures/gui/seismic_scanner.png");
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(CrystalMod.resourceL("textures/gui/machine/seismic_scanner.png"));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, 176, 190);

		String r = ""+scanner.radius;
		this.fontRendererObj.drawString(r, guiLeft + 86 - (this.fontRendererObj.getStringWidth(r)/2), guiTop + 138, 0);
		
		int max = scanner.seismicData == null ? 0 : scanner.seismicData.getLayers().length;
		scrollbarY.setScrollMax(Math.max(0, max-1));
	    scrollbarY.drawScrollbar(mouseX, mouseY);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		int dataRadius = scanner.seismicData == null ? 16 : scanner.seismicData.radius;
    	GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 0);
		float boxSize = (125)/2;
    	GlStateManager.translate(boxSize+25, boxSize+8, 0);
    	float zoomCalc = (16.0F / (dataRadius+1))*(2.0F);
    	float scale = zoomCalc;
		GlStateManager.translate(0.8 * (scale), -(scrollbarY.getScrollPos()*(scale*0.8)), 0);
		GlStateManager.scale(scale, scale, 1.0f);
		
		renderData(partialTicks);
		GlStateManager.popMatrix();
		
		int boxSide = guiLeft + 25;
		int[] xs = {boxSide + 30, boxSide + 30 + 28, boxSide + 30 + 68, boxSide + 30 + 40};
		int[] ys = {100, 76, 92, 116};
		
		Polygon diamond = new Polygon(/*xs, ys, 4*/);
		diamond.addPoint(xs[0], ys[0]);
		diamond.addPoint(xs[1], ys[1]);
		diamond.addPoint(xs[2], ys[2]);
		diamond.addPoint(xs[3], ys[3]);
		boolean inside = diamond.contains(mouseX, mouseY);
		
		if(inside/*mouseX > boxSide + 29 && mouseX < boxSide + 29 + 68 && mouseY > 72 && mouseY < 72 + (28)*/){
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 500);
			drawRect(mouseX, mouseY, mouseX + 16, mouseY + 16, Color.RED.getRGB());
			GlStateManager.popMatrix();
		}
	}
	
	public boolean needsUpdate = true;
    private int glRenderList = -1;
	public SeismicDataWorldWrapper fakeWorld;
	
    public void renderData(float partialTicks){
    	if(scanner.seismicData == null){
    		return;
    	}
    	if(fakeWorld == null){
    		fakeWorld = new SeismicDataWorldWrapper(scanner.getWorld(), null);
    	}
    	RenderUtil.pre(0, 0, 0);
    	GlStateManager.scale(1, -1, -1);
    	GlStateManager.translate(0, 0, -100);
    	GlStateManager.rotate(35, -1, 0, 0);
    	GlStateManager.rotate(35, 0, 1, 0);
    	GlStateManager.rotate(180, 0, 1, 0);
    	
    	if (needsUpdate) {
    		fakeWorld.data = scanner.seismicData;
        	/*if (glRenderList >= 0) {
                GLAllocation.deleteDisplayLists(glRenderList);
                glRenderList = -1;
            }
    		glRenderList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(glRenderList, GL11.GL_COMPILE);
            updateSimpleRender(partialTicks);
            GL11.glEndList();*/
            needsUpdate = false;
        }
    	updateSimpleRender(partialTicks);
        /*if(this.glRenderList >=0){
        	GlStateManager.callList(glRenderList);
        }*/
    	
    	RenderUtil.post();
	}

	private void updateSimpleRender(float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        } else {
            GlStateManager.shadeModel(7424);
        }

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		BlockPos scannerPos = scanner.getPos();
    	worldrenderer.setTranslation(-scannerPos.getX(), -scannerPos.getY(), -scannerPos.getZ());
        Map<BlockPos, IBlockState>[] layers = scanner.seismicData.getLayers();
        int layerOffset = 0;
        
        int scrollIndex = scrollbarY.getScrollPos();
        int radius = scanner.seismicData.radius;
        int secondOffset = scrollIndex;
        int bottom = secondOffset + ((radius*2)-(radius/2));
        
        for(int l = layerOffset+secondOffset; l < Math.min(bottom, layers.length); l++){
        	for(BlockPos pos : layers[l].keySet()){
        		IBlockState blockState = layers[l].get(pos);
        		Block block = blockState.getBlock();

        		for (BlockRenderLayer enumWorldBlockLayer : BlockRenderLayer.values()) {
        			if (!block.canRenderInLayer(blockState, enumWorldBlockLayer)) continue;


        			if (block.getRenderType(blockState) != EnumBlockRenderType.INVISIBLE) {
        				net.minecraftforge.client.ForgeHooksClient.setRenderLayer(enumWorldBlockLayer);
        				dispatchBlockRender(blockState, pos, worldrenderer, (layerOffset+secondOffset) == l ? false : true);
        			}
        		}
        	}
        }
        worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
	}

	private void dispatchBlockRender(IBlockState blockState, BlockPos blockPos, VertexBuffer worldRenderer, boolean checkSides) {
		 worldRenderer.color(1.0F, 1.0F, 1.0F, 1.0F);
		 renderBlock(blockState, blockPos, fakeWorld, worldRenderer, checkSides);
	}
	
	public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, VertexBuffer worldRendererIn, boolean checkSides)
    {
		BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		 try
        {
            EnumBlockRenderType enumblockrendertype = state.getRenderType();

            if (enumblockrendertype == EnumBlockRenderType.INVISIBLE)
            {
                return false;
            }
            else
            {
                if (blockAccess.getWorldType() != WorldType.DEBUG_WORLD)
                {
                    try
                    {
                        state = state.getActualState(blockAccess, pos);
                    }
                    catch (Exception var8)
                    {
                        ;
                    }
                }

                switch (enumblockrendertype)
                {
                    case MODEL:
                        IBakedModel model = blockRendererDispatcher.getModelForState(state);
                        state = state.getBlock().getExtendedState(state, blockAccess, pos);
                        return blockRendererDispatcher.getBlockModelRenderer().renderModel(blockAccess, model, state, pos, worldRendererIn, checkSides);
                    case ENTITYBLOCK_ANIMATED:
                        return false;
                    case LIQUID:{
                    	BlockFluidRenderer fr = ObfuscationReflectionHelper.getPrivateValue(BlockRendererDispatcher.class, blockRendererDispatcher, 3);
                        return fr.renderFluid(blockAccess, state, pos, worldRendererIn);
                    }
                    default:
                        return false;
                }
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(crashreport);
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
