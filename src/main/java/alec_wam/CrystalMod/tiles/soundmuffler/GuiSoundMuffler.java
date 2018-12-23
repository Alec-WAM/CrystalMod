package alec_wam.CrystalMod.tiles.soundmuffler;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.ContainerNull;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.VScrollbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class GuiSoundMuffler extends GuiContainer implements IGuiScreen{

	private VScrollbar scrollbarLeft;
	private VScrollbar scrollbarRight;
	protected VScrollbar draggingScrollbar;
	public TileSoundMuffler muffler;
	public GuiTextField searchBox;
	
	public GuiSoundMuffler(TileSoundMuffler muffler){
		super(new ContainerNull());
		this.muffler = muffler;
		xSize = this.width = 176;
        ySize = this.height = 139;
	}
	
	@Override
	public void updateScreen()
    {
        this.searchBox.updateCursorCounter();
    }
	
	@Override
	public void initGui(){
		super.initGui();
		
		scrollbarLeft = new VScrollbar(this, 4, 22, 98);
		scrollbarLeft.adjustPosition();
		
		scrollbarRight = new VScrollbar(this, 160, 22, 98);
		scrollbarRight.adjustPosition();
		
		this.buttonList.clear();
		this.searchBox = new GuiTextField(0, this.fontRendererObj, guiLeft+15, guiTop+9, 144, 12);
        this.searchBox.setMaxStringLength(200);
        this.searchBox.setFocused(true);
	}
	
	@Override
	public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }
	
	@Override
	public void actionPerformed(GuiButton button){
		
	}
	
	@Override
	public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        if (i != 0)
        {
        	if(x > width/2)this.scrollbarRight.mouseWheel(x, y, i);
        	if(x < width/2)this.scrollbarLeft.mouseWheel(x, y, i);
        }
    }
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if(!this.searchBox.textboxKeyTyped(typedChar, keyCode)){
        	super.keyTyped(typedChar, keyCode);
        } else {
        	this.soundCache = null;
        }
    }
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		if (draggingScrollbar != null) {
			draggingScrollbar.mouseClicked(mouseX, mouseY, mouseButton);
        	return;
		}
		
		if(this.scrollbarLeft !=null){
			if (scrollbarLeft.mouseClicked(mouseX, mouseY, mouseButton)) {
				draggingScrollbar = scrollbarLeft;
				return;
			}
		}
		
		if(this.scrollbarRight !=null){
			if (scrollbarRight.mouseClicked(mouseX, mouseY, mouseButton)) {
				draggingScrollbar = scrollbarRight;
				return;
			}
		}
		this.searchBox.mouseClicked(mouseX, mouseY, mouseButton);
		
		List<String> currentSounds = muffler.getSoundList();
		if(!currentSounds.isEmpty()){
			int renderCount = 0;
			for(int i = scrollbarLeft.getScrollPos(); i < currentSounds.size() && renderCount < 6; i++){
				String soundLoc = currentSounds.get(i);
				if(soundLoc !=null){
					if(this.isPointInRegion(15, 23+(16*renderCount), 71, 16, mouseX, mouseY)){	
						muffler.removeSoundFromList(soundLoc);
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setString("Name", soundLoc.toString());
						CrystalModNetwork.sendToServer(new PacketTileMessage(muffler.getPos(), "RemoveSound", nbt));
						return;
					}
				}
				renderCount++;
			}
		}
		
		List<ResourceLocation> searchedSounds = getSounds(searchBox.getText());
		if(!searchedSounds.isEmpty()){
			int renderCount = 0;
			for(int i = scrollbarRight.getScrollPos(); i < searchedSounds.size() && renderCount < 6; i++){
				ResourceLocation soundLoc = searchedSounds.get(i);
				if(soundLoc !=null){
					if(this.isPointInRegion(88, 23+(16*renderCount), 71, 16, mouseX, mouseY)){	
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setString("Name", soundLoc.toString());
						CrystalModNetwork.sendToServer(new PacketTileMessage(muffler.getPos(), "AddSound", nbt));
						return;
					}
				}
				renderCount++;
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
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
	
	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}

	private List<ResourceLocation> soundCache;
	
	public List<ResourceLocation> getSounds(String filter){
		if(soundCache == null){
			soundCache = Lists.newArrayList();
			
			SoundRegistry reg = null;
			try{
				reg = ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, Minecraft.getMinecraft().getSoundHandler(), 4);
			} catch(Exception e){}
			
			if(reg == null)return soundCache;
			
			for(ResourceLocation loc : reg.getKeys()){
				if(loc.toString().toLowerCase().contains(filter)){
					soundCache.add(loc);
				}
			}
		}
		return soundCache;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/soundmuffler.png"));
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		List<String> currentSounds = muffler.getSoundList();
		scrollbarLeft.setScrollMax(Math.max(0, currentSounds.size()-1));
	    scrollbarLeft.drawScrollbar(mouseX, mouseY);
	    
	    List<ResourceLocation> searchedSounds = getSounds(searchBox.getText());
		scrollbarRight.setScrollMax(Math.max(0, searchedSounds.size()-1));
		scrollbarRight.drawScrollbar(mouseX, mouseY);
	    
	    searchBox.drawTextBox();
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {	
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		List<String> currentSounds = muffler.getSoundList();
		List<ResourceLocation> searchedSounds = getSounds(searchBox.getText());
		GlStateManager.color(1, 1, 1, 1);
		if(!currentSounds.isEmpty()){
			GlStateManager.pushMatrix();
			int renderCount = 0;
			for(int i = scrollbarLeft.getScrollPos(); i < currentSounds.size() && renderCount < 6; i++){
				String soundLoc = currentSounds.get(i);
				if(soundLoc !=null){
					
					GlStateManager.pushMatrix();
					GlStateManager.translate(20, 29+(16*renderCount), 0);
					int width = fontRendererObj.getStringWidth(soundLoc);
					double scale = Math.min(65F / (width), 0.5F);
					GlStateManager.scale(scale, scale, 1);
					fontRendererObj.drawString(soundLoc, 0, 0, 1);
					GlStateManager.popMatrix();
					
					if(this.isPointInRegion(15, 23+(16*renderCount), 71, 16, mouseX, mouseY)){	
						drawGradientRect(16, 23+(16*renderCount), 87, 23+(16*(renderCount+1)), -2130706433, -2130706433);
					}
				}
				renderCount++;
			}
			GlStateManager.popMatrix();
		}
		
		if(!searchedSounds.isEmpty()){
			GlStateManager.pushMatrix();
			int renderCount = 0;
			for(int i = scrollbarRight.getScrollPos(); i < searchedSounds.size() && renderCount < 6; i++){
				ResourceLocation soundLoc = searchedSounds.get(i);
				if(soundLoc !=null){
					
					GlStateManager.pushMatrix();
					GlStateManager.translate(90, 29+(16*renderCount), 0);
					int width = fontRendererObj.getStringWidth(soundLoc.toString());
					double scale = Math.min(68F / (width), 0.5F);
					GlStateManager.scale(scale, scale, 1);
					fontRendererObj.drawString(soundLoc.toString(), 0, 0, 1);
					GlStateManager.popMatrix();
					
					if(this.isPointInRegion(88, 23+(16*renderCount), 71, 16, mouseX, mouseY)){	
						drawGradientRect(88, 23+(16*renderCount), 159, 23+(16*(renderCount+1)), -2130706433, -2130706433);
					}
				}
				renderCount++;
			}
			GlStateManager.popMatrix();
		}
	}

}
