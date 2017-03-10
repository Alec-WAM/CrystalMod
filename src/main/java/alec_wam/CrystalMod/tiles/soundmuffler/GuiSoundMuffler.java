package alec_wam.CrystalMod.tiles.soundmuffler;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.util.ButtonSprite;
import alec_wam.CrystalMod.client.util.GuiButtonSprite;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.ContainerNull;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.VScrollbar;
import alec_wam.CrystalMod.util.client.Scrollbar;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

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
        ySize = this.height = 230;
	}
	
	@Override
	public void updateScreen()
    {
        this.searchBox.updateCursorCounter();
    }
	
	@Override
	public void initGui(){
		super.initGui();
		
		scrollbarLeft = new VScrollbar(this, 6, 22, 91);
		scrollbarLeft.adjustPosition();
		
		scrollbarRight = new VScrollbar(this, 162, 22, 91);
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
			for(SoundEvent event : SoundEvent.REGISTRY){
				if(event.getRegistryName().toString().toLowerCase().contains(filter)){
					soundCache.add(event.getRegistryName());
				}
			}
		}
		return soundCache;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/soundmuffler.png"));
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);;
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
					GlStateManager.translate(20, 27+(16*renderCount), 0);
					GlStateManager.scale(0.5, 0.5, 1);
					this.drawString(fontRendererObj, soundLoc, 0, 0, 1);
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
					GlStateManager.translate(90, 27+(16*renderCount), 0);
					GlStateManager.scale(0.5, 0.5, 1);
					this.drawString(fontRendererObj, soundLoc.toString(), 0, 0, 1);
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
