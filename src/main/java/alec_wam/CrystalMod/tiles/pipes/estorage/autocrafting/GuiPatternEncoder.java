package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import java.io.IOException;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiPatternEncoder extends GuiContainer {

	private TilePatternEncoder encoder;
	
	private GuiButton oreButton;
	
	public GuiPatternEncoder(EntityPlayer player, TilePatternEncoder encoder) {
		super(new ContainerPatternEncoder(player, encoder));
		this.encoder = encoder;
		ySize+=6;
	}
	
	public void initGui(){
		super.initGui();
		String ore = encoder.isOreDict ? TextFormatting.GREEN+"O" : TextFormatting.RED+"O";
		boolean processing = encoder instanceof TileProcessingPatternEncoder;
		oreButton = new GuiButton(0, guiLeft+(processing ? 65 : 118), guiTop+(processing ? 18 : 10), 20, 20, ore);
		this.buttonList.add(oreButton);
	}
	
	public void actionPerformed(GuiButton button){
		if(button.id == oreButton.id){
			boolean old = encoder.isOreDict;
			encoder.isOreDict = !old;
			String ore = encoder.isOreDict ? TextFormatting.GREEN+"O" : TextFormatting.RED+"O";
			oreButton.displayString = ore;
			CrystalModNetwork.sendToServer(new PacketTileMessage(encoder.getPos(), "Ore"));
		}
	}

	public boolean inBounds(int x, int y, int w, int h, int ox, int oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }
	
	public boolean isHoveringOverCreatePattern(int mouseX, int mouseY) {
        return inBounds(152, 38, 16, 16, mouseX, mouseY) && encoder.mayCreatePattern();
    }
	
	public boolean isHoveringOverClear(int mouseX, int mouseY) {
        return inBounds(152, 38, 16, 16, mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		boolean showClearButton = true;
		mc.getTextureManager().bindTexture(new ResourceLocation("crystalmod:textures/gui/eStorage_pattern.png"));
		if(encoder instanceof TileProcessingPatternEncoder){
			mc.getTextureManager().bindTexture(new ResourceLocation("crystalmod:textures/gui/eStorage_pattern_processing.png"));
			showClearButton = false;
		}
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		int ty = 0;

        if (isHoveringOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
            ty = 1;
        }

        if (!encoder.mayCreatePattern()) {
            ty = 2;
        }

        drawTexturedModalRect(guiLeft + 152, guiTop + 38, 178, ty * 16, 16, 16);
	}
	
	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (isHoveringOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
            CrystalModNetwork.sendToServer(new PacketTileMessage(encoder.getPos(), "Encode"));

            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

}
