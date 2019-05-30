package alec_wam.CrystalMod.tiles.energy.battery;

import java.text.NumberFormat;

import alec_wam.CrystalMod.client.gui.ElementEnergy;
import alec_wam.CrystalMod.client.gui.GuiButtonTiny;
import alec_wam.CrystalMod.client.gui.tabs.GuiContainerTabbed;
import alec_wam.CrystalMod.client.gui.tabs.IOTab;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiBattery extends GuiContainerTabbed {
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/battery.png");
	public TileEntityBattery battery;
	public GuiBattery(EntityPlayer player, TileEntityBattery battery) {
		super(new ContainerBattery(player, battery), TEXTURE);
		this.battery = battery;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		this.elements.clear();
		tabManager.getTabs().clear();
		tabManager.add(new IOTab(battery));
		addElement(new ElementEnergy(this, 80, 22, this.battery.energyStorage).setCreative(battery.isCreative()));
		
		GuiButton buttonReceiveDown = new GuiButtonTiny(0, guiLeft + 10, guiTop + 44, 10, 10, "-") {
			@Override
			public void onClick(double mouseX, double mouseY){
				int amount = getPowerSettingsAmount();
				battery.receiveAmount -=amount;
				if(battery.receiveAmount < 0)battery.receiveAmount = 0;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Amount", battery.receiveAmount);
				CrystalModNetwork.sendToServer(new PacketTileMessage(battery.getPos(), "UpdateReceive", nbt));
			}
		};
		GuiButton buttonReceiveUp = new GuiButtonTiny(1, guiLeft + 40, guiTop + 44, 10, 10, "+") {
			@Override
			public void onClick(double mouseX, double mouseY){
				int amount = getPowerSettingsAmount();
				battery.receiveAmount +=amount;
				if(battery.receiveAmount > TileEntityBattery.MAX_IO[battery.getTier()])battery.receiveAmount = TileEntityBattery.MAX_IO[battery.getTier()];
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Amount", battery.receiveAmount);
				CrystalModNetwork.sendToServer(new PacketTileMessage(battery.getPos(), "UpdateReceive", nbt));
			}
		};
		GuiButton buttonSendDown = new GuiButtonTiny(2, guiLeft + 126, guiTop + 44, 10, 10, "-") {
			@Override
			public void onClick(double mouseX, double mouseY){
				int amount = getPowerSettingsAmount();
				battery.sendAmount -=amount;
				if(battery.sendAmount < 0)battery.sendAmount = 0;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Amount", battery.sendAmount);
				CrystalModNetwork.sendToServer(new PacketTileMessage(battery.getPos(), "UpdateSend", nbt));
			}
		};
		GuiButton buttonSendUp = new GuiButtonTiny(3, guiLeft + 156, guiTop + 44, 10, 10, "+") {
			@Override
			public void onClick(double mouseX, double mouseY){
				int amount = getPowerSettingsAmount();
				battery.sendAmount +=amount;
				if(battery.sendAmount > TileEntityBattery.MAX_IO[battery.getTier()])battery.sendAmount = TileEntityBattery.MAX_IO[battery.getTier()];
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Amount", battery.sendAmount);
				CrystalModNetwork.sendToServer(new PacketTileMessage(battery.getPos(), "UpdateSend", nbt));
			}
		};
		
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
		fontRenderer.drawString(sendFull, 145 - (fontRenderer.getStringWidth(sendFull)/2), 32, 4210752);
		
		GlStateManager.pushMatrix();
		GlStateManager.translated(146, 45, this.zLevel);
		String sendString = format.format(battery.getEnergySend());
		int width = fontRenderer.getStringWidth(sendString);
		double scale = Math.min(20F / (width + 2), 1.0F);
		GlStateManager.scaled(scale, scale, 1.0D);
		fontRenderer.drawString(sendString, -(width/2), (int)(fontRenderer.FONT_HEIGHT*(1.0f-scale)), 4210752);
		GlStateManager.popMatrix();
		
		if(!battery.isCreative()){
			String receiveString = format.format(battery.getEnergyReceive());
			String receiveFull = Lang.localize("battery.receive");
			fontRenderer.drawString(receiveFull, 30 - (fontRenderer.getStringWidth(receiveFull)/2), 32, 4210752);
			
			GlStateManager.pushMatrix();
			GlStateManager.translated(30, 45, this.zLevel);
			width = fontRenderer.getStringWidth(receiveString);
			scale = Math.min(20F / (width + 2), 1.0F);
			GlStateManager.scaled(scale, scale, 1.0D);
			fontRenderer.drawString(receiveString, -(width/2), (int)(fontRenderer.FONT_HEIGHT*(1.0f-scale)), 4210752);
			GlStateManager.popMatrix();
		}
	}
	
	public int getPowerSettingsAmount(){
		int amount = 10;
		if(GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown()) amount = 1;
		else if(GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown()) amount = 100;
		else if(GuiScreen.isCtrlKeyDown() && GuiScreen.isShiftKeyDown()) amount = 1000;
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
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);		
	}

}
