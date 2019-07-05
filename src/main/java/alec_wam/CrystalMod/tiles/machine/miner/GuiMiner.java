package alec_wam.CrystalMod.tiles.machine.miner;

import java.awt.Color;
import java.util.List;

import alec_wam.CrystalMod.client.gui.ButtonTiny;
import alec_wam.CrystalMod.client.gui.ElementEnergy;
import alec_wam.CrystalMod.client.gui.GuiElementContainer;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiMiner extends GuiElementContainer<ContainerMiner> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/miner.png");
	public TileEntityMiner tileMachine;
	public GuiMiner(int windowId, PlayerEntity player, TileEntityMiner tilePart)
    {
        super(new ContainerMiner(windowId, player, tilePart), player.inventory, new TranslationTextComponent("block.crystalmod.miner"), TEXTURE);

        this.tileMachine = tilePart;
        this.name = Lang.translateToLocal("block.crystalmod.miner");
        this.xSize = 195;
        this.ySize = 168;
    }
	
	@Override
	public void init(){
		super.init();
		
		ElementEnergy energyElement = new ElementEnergy(this, 35, 23, this.tileMachine.getEnergyStorage()) {
			@Override
			public void addTooltip(List<String> list) {
				super.addTooltip(list);				
				list.add(Lang.localizeFormat("miner.energycost", ""+tileMachine.clientEnergyCost));
			}
		};
		addElement(energyElement);
		
		Button buttonReset = new ButtonTiny(guiLeft + 141, guiTop + 5, 40, 10, "Reset", new Button.IPressable(){

			@Override
			public void onPress(Button button) {
				CrystalModNetwork.sendToServer(new PacketTileMessage(tileMachine.getPos(), "Reset"));
			}
			
		});
		this.addButton(buttonReset);
	}
	
	private int guiTimer;
	@Override
	public void tick(){
		super.tick();
		guiTimer++;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		if (drawTitle) {
			font.drawString(Lang.translateToLocal(name), getCenteredOffset(Lang.translateToLocal(name)) + 2, 6, 0x404040);
		}
		if (drawInventory) {
			font.drawString(Lang.translateToLocal("container.inventory"), 18, ySize - 96 + 3, 0x404040);
		}
		drawElements(0, true);
		
		boolean running = tileMachine.isRunning;
		boolean finished = tileMachine.isFinished;
		boolean bedrock = tileMachine.isAtBedrock;
		String str = "Running";
		int strWidth = font.getStringWidth(str);
		String finStr = "Finished";
		int finStrWidth = font.getStringWidth(finStr);
		String bedStr = "Bedrock";
		int bedStrWidth = font.getStringWidth(bedStr);
		
		int strX = 137;
		int strY = 20;
		this.font.drawString(str, strX, strY, Color.GREEN.getRGB());		
		this.font.drawString(finStr, strX, strY + 10, Color.GREEN.getRGB());	
		this.font.drawString(bedStr, strX, strY + 20, Color.GREEN.getRGB());	
		
		int lightX = strX + strWidth + 2;
		int lightY = strY + 2;
		int finLightX = strX + finStrWidth + 2;
		int finLightY = strY + 10 + 2;
		int bedLightX = strX + bedStrWidth + 2;
		int bedLightY = strY + 20 + 2;
		
		if(running) {
			int div = guiTimer % 40;
			if(div > 5){
				fill(lightX, lightY, lightX + 5, lightY + 5, Color.GREEN.getRGB());
			}
		} else {
			fill(lightX, lightY, lightX + 5, lightY + 5, Color.RED.getRGB());
		}
		fill(finLightX, finLightY, finLightX + 5, finLightY + 5, finished ? Color.GREEN.getRGB() : Color.RED.getRGB());
		fill(bedLightX, bedLightY, bedLightX + 5, bedLightY + 5, bedrock ? Color.GREEN.getRGB() : Color.RED.getRGB());
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	}
}
