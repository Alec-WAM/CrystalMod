package alec_wam.CrystalMod.tiles.pipes.liquid;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.item.PacketPipe;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GuiLiquidPipe extends GuiContainer {

	final InventoryPlayer playerInv;
	final TileEntityPipeLiquid pipe;
	private final EnumFacing dir;
	
	private boolean inOutShowIn = true;
	
	public GuiLiquidPipe(InventoryPlayer player, TileEntityPipeLiquid pipe, EnumFacing dir) {
		super(new ContainerLiquidPipe(player, pipe));
		playerInv = player;
		this.pipe = pipe;
		this.dir = dir;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		refreshButtons();
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		if(button.id == 0){
			pipe.setConnectionMode(dir, !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? pipe.getNextConnectionMode(dir) : pipe.getPreviousConnectionMode(dir));
			CrystalModNetwork.sendToServer(new PacketPipe(pipe, "CMode", dir, pipe.getConnectionMode(dir).name()));
			refreshButtons();
			return;
		}
		
		if(button.id == 1){
			boolean old = inOutShowIn;
			this.inOutShowIn = !old;
			refreshButtons();
			return;
		}
		
		if(button.id == 2){
			FluidFilter filter = getFilter();
			if(filter == null){
				filter = new FluidFilter();
			}
			boolean old = filter.isBlacklist();
			getFilter().setBlacklist(!old);
			updateFilter(filter);
			refreshButtons();
			return;
		}
	}
	
	private boolean isFilterVisible() {
	    ConnectionMode mode = pipe.getConnectionMode(dir);
	    return mode == ConnectionMode.INPUT || mode == ConnectionMode.OUTPUT || mode == ConnectionMode.IN_OUT;
	}
	
	public FluidFilter getFilter(){
		FluidFilter filter =  pipe.getFilter(dir, isInput());
		if(filter == null) {
			filter = new FluidFilter();
    	}
		return filter;
	}
	
	public void updateFilter(FluidFilter filter){
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("Dir", dir.getIndex());
		data.setBoolean("isInput", isInput());
		
		NBTTagCompound filterData = new NBTTagCompound();
		if(filter !=null){
			filter.writeToNBT(filterData);
		}
		data.setTag("FilterData", filterData);
		this.pipe.setFilter(dir, filter, isInput());
		CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "FilterFluid", data));
	}
	
	private boolean isInput() {
	    ConnectionMode mode = pipe.getConnectionMode(dir);
	    return (mode == ConnectionMode.IN_OUT && inOutShowIn) || (mode == ConnectionMode.INPUT);
	}
	
	private void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		this.buttonList.add(new GuiButton(0, sx+40, sy+2, 100, 10, Lang.localize(pipe.getConnectionMode(dir).getUnlocalisedName())));
		if(isFilterVisible()){
			FluidFilter filter = pipe.getFilter(dir, isInput());
			this.buttonList.add(new GuiButton(1, sx+8, sy+35, 12, 12, inOutShowIn ? "I" : "O"));
			if(filter !=null){
				this.buttonList.add(new GuiButton(2, sx+8+15, sy+35, 12, 12, filter.isBlacklist() ? "B" : "W"));
			}
		}
	}

	@Override
	  public void mouseClicked(int x, int y, int par3) {
		try {
			super.mouseClicked(x, y, par3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		x-=guiLeft;
		y-=guiTop;
	    if(!isFilterVisible()) {
	      return;
	    }
	    int filterX = 59;
	    int filterY = 63;
	    Rectangle filterBounds = new Rectangle(filterX, filterY, 90, 18);
	   
	    if(!filterBounds.contains(x, y)) {
	      return;
	    }
	    
	    ItemStack st = CrystalMod.proxy.getClientPlayer().inventory.getItemStack();
	    FluidFilter filter = getFilter();
	    if(filter == null && ItemStackTools.isNullStack(st)) {
	      return;
	    }
	    if(filter == null) {
	      filter = new FluidFilter();
	    }
	    int slot = (x - filterX) / 18;
	    if(slot < 5 && slot >= 0){
	    	ModLogger.info(slot+" "+(FluidUtil.getFluidTypeFromItem(st) !=null ? FluidUtil.getFluidTypeFromItem(st).getLocalizedName() : ""));
	    	filter.setFluid(slot, st);
	    	updateFilter(filter);
	    }
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		boolean isInput = isInput();
		int filterX = 59;
	    int filterY = 63;
	    if(isInput) {
	      int y = guiTop;
	      fontRendererObj.drawString("Input", guiLeft+filterX, y+filterY-10, Color.DARK_GRAY.getRGB());
	    }
	    if(isFilterVisible()) {
	    	
	      if(pipe.getConnectionMode(dir) == ConnectionMode.IN_OUT) {
	        String inOutStr = inOutShowIn ? Lang.localize("gui.pipe.ioMode.input") : Lang.localize("gui.pipe.ioMode.output");
	        int x = 0 + xSize - 20 - 5 - fontRendererObj.getStringWidth(inOutStr);
	        int y = 20;
	        fontRendererObj.drawString(inOutStr, x, y, Color.DARK_GRAY.getRGB());
	      }

	      GlStateManager.color(1, 1, 1);
	      //gui.bindGuiTexture(1);
	      //gui.drawTexturedModalRect(gui.getGuiLeft(), gui.getGuiTop() + 55, 0, 55, gui.getXSize(), 145);
	      String filterStr = "Filter";
	      FontRenderer fr = fontRendererObj;
	      int sw = fr.getStringWidth(filterStr);
	      int x = sw / 2;
	      int y = 0 + 40;
	      fr.drawString(filterStr, x, y, Color.DARK_GRAY.getRGB());
	      
	      x = 0 + filterX;
	      y = 0 + filterY;
	      GlStateManager.color(1, 1, 1);
	      //gui.bindGuiTexture();
	      //gui.drawTexturedModalRect(x, y, 24, 238, 90, 18);

	      FluidFilter filter = getFilter();
	      for(int i = 0; i < 5; i++){
	    	  drawRect(x + (i * 18), y, x + (i * 18) + 16, y + 16, Color.GRAY.getRGB());
	      }
	      
	      if(filter != null && !filter.isEmpty()) {
	    	  
	        for (int i = 0; i < filter.size(); i++) {
	          FluidStack f = filter.getFluidStackAt(i);
	          if(f != null) {
	            renderFluid(f, x + (i * 18), y);
	          }
	        }
	      }

	    }
    }
	
	private void renderFluid(FluidStack f, int x, int y) {
	    
	    RenderUtil.renderGuiTank(f, 1000, 1000, x + 1, y + 1, 0, 16, 16);

	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;

	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/pipe.png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}

