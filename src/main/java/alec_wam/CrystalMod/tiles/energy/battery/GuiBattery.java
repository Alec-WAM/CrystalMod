package alec_wam.CrystalMod.tiles.energy.battery;

import java.text.NumberFormat;

import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.client.gui.ButtonTiny;
import alec_wam.CrystalMod.client.gui.ElementEnergy;
import alec_wam.CrystalMod.client.gui.tabs.GuiContainerTabbed;
import alec_wam.CrystalMod.client.gui.tabs.IOTab;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class GuiBattery extends GuiContainerTabbed<ContainerBattery> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/battery.png");
	public TileEntityBattery battery;
	public GuiBattery(int windowId, PlayerEntity player, TileEntityBattery battery) {
		super(new ContainerBattery(windowId, player, battery), player.inventory, null, TEXTURE);
		this.battery = battery;
	}
	
	@Override
	public void init(){
		super.init();
		this.elements.clear();
		tabManager.getTabs().clear();
		tabManager.add(new IOTab(battery));
		addElement(new ElementEnergy(this, 80, 22, this.battery.energyStorage).setCreative(battery.isCreative()));
		
		Button buttonReceiveDown = new ButtonTiny(guiLeft + 10, guiTop + 44, 10, 10, "-", new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				int amount = getPowerSettingsAmount();
				battery.receiveAmount -=amount;
				if(battery.receiveAmount < 0)battery.receiveAmount = 0;
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Amount", battery.receiveAmount);
				CrystalModNetwork.sendToServer(new PacketTileMessage(battery.getPos(), "UpdateReceive", nbt));
			}
		});
		Button buttonReceiveUp = new ButtonTiny(guiLeft + 40, guiTop + 44, 10, 10, "+", new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				int amount = getPowerSettingsAmount();
				battery.receiveAmount +=amount;
				if(battery.receiveAmount > TileEntityBattery.MAX_IO[battery.getTier()])battery.receiveAmount = TileEntityBattery.MAX_IO[battery.getTier()];
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Amount", battery.receiveAmount);
				CrystalModNetwork.sendToServer(new PacketTileMessage(battery.getPos(), "UpdateReceive", nbt));
			}
		});
		Button buttonSendDown = new ButtonTiny(guiLeft + 126, guiTop + 44, 10, 10, "-", new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				int amount = getPowerSettingsAmount();
				battery.sendAmount -=amount;
				if(battery.sendAmount < 0)battery.sendAmount = 0;
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Amount", battery.sendAmount);
				CrystalModNetwork.sendToServer(new PacketTileMessage(battery.getPos(), "UpdateSend", nbt));
			}
		});
		Button buttonSendUp = new ButtonTiny(guiLeft + 156, guiTop + 44, 10, 10, "+", new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				int amount = getPowerSettingsAmount();
				battery.sendAmount +=amount;
				if(battery.sendAmount > TileEntityBattery.MAX_IO[battery.getTier()])battery.sendAmount = TileEntityBattery.MAX_IO[battery.getTier()];
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Amount", battery.sendAmount);
				CrystalModNetwork.sendToServer(new PacketTileMessage(battery.getPos(), "UpdateSend", nbt));
			}
		});
		
		if(!battery.isCreative()){
			this.addButton(buttonReceiveDown);
			this.addButton(buttonReceiveUp);
		}
		this.addButton(buttonSendDown);
		this.addButton(buttonSendUp);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		
		NumberFormat format = NumberFormat.getNumberInstance();
		String sendFull = Lang.localize("battery.send");
		font.drawString(sendFull, 145 - (font.getStringWidth(sendFull)/2), 32, 4210752);
		
		GlStateManager.pushMatrix();
		GlStateManager.translated(146, 45, this.blitOffset);
		String sendString = format.format(battery.getEnergySend());
		int width = font.getStringWidth(sendString);
		double scale = Math.min(20F / (width + 2), 1.0F);
		GlStateManager.scaled(scale, scale, 1.0D);
		font.drawString(sendString, -(width/2), (int)(font.FONT_HEIGHT*(1.0f-scale)), 4210752);
		GlStateManager.popMatrix();
		
		if(!battery.isCreative()){
			String receiveString = format.format(battery.getEnergyReceive());
			String receiveFull = Lang.localize("battery.receive");
			font.drawString(receiveFull, 30 - (font.getStringWidth(receiveFull)/2), 32, 4210752);
			
			GlStateManager.pushMatrix();
			GlStateManager.translated(30, 45, this.blitOffset);
			width = font.getStringWidth(receiveString);
			scale = Math.min(20F / (width + 2), 1.0F);
			GlStateManager.scaled(scale, scale, 1.0D);
			font.drawString(receiveString, -(width/2), (int)(font.FONT_HEIGHT*(1.0f-scale)), 4210752);
			GlStateManager.popMatrix();
		}
	}
	
	public int getPowerSettingsAmount(){
		int amount = 10;
		if(Screen.hasControlDown() && !Screen.hasShiftDown()) amount = 1;
		else if(Screen.hasShiftDown() && !Screen.hasControlDown()) amount = 100;
		else if(Screen.hasControlDown() && Screen.hasShiftDown()) amount = 1000;
		return amount;
	}
	
	@Override
	protected void drawBackgroundTexture(){
		EnumCrystalColorSpecialWithCreative type = EnumCrystalColorSpecialWithCreative.values()[battery.getTier()];
		if(type == EnumCrystalColorSpecialWithCreative.BLUE){
			GlStateManager.color4f(0.0F, 1.0F, 1.0F, 1.0F);
		}
		if(type == EnumCrystalColorSpecialWithCreative.RED){
			GlStateManager.color4f(1.0F, 0.0F, 0.0F, 1.0F);
		}
		if(type == EnumCrystalColorSpecialWithCreative.GREEN){
			GlStateManager.color4f(0.0F, 1.0F, 0.0F, 1.0F);
		}
		if(type == EnumCrystalColorSpecialWithCreative.DARK){
			GlStateManager.color4f(0.2F, 0.2F, 0.2F, 1F);
		}
		if(type == EnumCrystalColorSpecialWithCreative.PURE){
			GlStateManager.color4f(10.0F, 10.0F, 10.0F, 1F);
		}
		if(type == EnumCrystalColorSpecialWithCreative.CREATIVE){
			GlStateManager.color4f(1.0F, 0.5F, 0.0F, 1F);
		}
		bindTexture(TEXTURE);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);		
	}

}
