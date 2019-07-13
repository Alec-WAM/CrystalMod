package alec_wam.CrystalMod.tiles.machine.pump;

import java.awt.Color;
import java.util.List;

import alec_wam.CrystalMod.client.gui.ButtonTiny;
import alec_wam.CrystalMod.client.gui.ElementEnergy;
import alec_wam.CrystalMod.client.gui.GuiElementContainer;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.Fluid;

public class GuiFluidPump extends GuiElementContainer<ContainerFluidPump> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/pump.png");
	public TileEntityFluidPump tileMachine;
	public GuiFluidPump(int windowId, PlayerEntity player, TileEntityFluidPump tilePart)
    {
        super(new ContainerFluidPump(windowId, player, tilePart), player.inventory, new TranslationTextComponent("block.crystalmod.fluidpump"), TEXTURE);

        this.tileMachine = tilePart;
        this.name = Lang.translateToLocal("block.crystalmod.fluidpump");
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

		boolean full = tileMachine.tank.getFluidAmount() > tileMachine.tank.getCapacity() - Fluid.BUCKET_VOLUME;
		boolean running = !full && tileMachine.isRunning;
		boolean finished = tileMachine.isFinished;
		String str = "Running";
		int strWidth = font.getStringWidth(str);
		String finStr = "Finished";
		int finStrWidth = font.getStringWidth(finStr);
		
		int strX = 137;
		int strY = 20;
		this.font.drawString(str, strX, strY, Color.GREEN.getRGB());		
		this.font.drawString(finStr, strX, strY + 10, Color.GREEN.getRGB());	
		
		int lightX = strX + strWidth + 2;
		int lightY = strY + 2;
		int finLightX = strX + finStrWidth + 2;
		int finLightY = strY + 10 + 1;
		
		if(running) {
			int div = guiTimer % 40;
			if(div > 5){
				fill(lightX, lightY, lightX + 5, lightY + 5, Color.GREEN.getRGB());
			}
		} else {
			fill(lightX, lightY, lightX + 5, lightY + 5, Color.RED.getRGB());
		}
		fill(finLightX, finLightY, finLightX + 5, finLightY + 5, finished ? Color.GREEN.getRGB() : Color.RED.getRGB());
		if(full) {
			int div = guiTimer % 20;
			if(div > 5){
				String fullStr = "Full Tank!";
				this.font.drawString(fullStr, strX, strY + 20, Color.RED.getRGB());	
			}
		} 
		
		
		final int barWidth = 16;
		final int barHeight = 52;
		int xpX = 90;
		int xpY = 18;
		RenderUtil.renderGuiTank(tileMachine.tank, xpX, xpY, 0, barWidth, barHeight, true);
	}
	
	@Override
	public void addTooltips(List<String> tooltip) {
		super.addTooltips(tooltip);
		final int barWidth = 16;
		final int barHeight = 52;
		int xpX = 90;
		int xpY = 18;
		if(mouseX > xpX && mouseX < (xpX)+(barWidth+2) && mouseY >= xpY && mouseY <= xpY+(barHeight)+2)
		{
			if(tileMachine.tank.getFluid() == null){
				tooltip.add(Lang.localize("empty"));
			}else{
				tooltip.add(tileMachine.tank.getFluid().getLocalizedName()+" "+(tileMachine.tank.getFluid().amount+" / "+tileMachine.tank.getCapacity()+"MB"));
			}
		}
	}
	
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	}
}
