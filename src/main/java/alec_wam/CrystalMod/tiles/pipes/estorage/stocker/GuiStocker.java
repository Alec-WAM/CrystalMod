package alec_wam.CrystalMod.tiles.pipes.estorage.stocker;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.google.common.primitives.Ints;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiStocker extends GuiContainer {

	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("crystalmod:textures/gui/eStorage_stocker.png");
	
	public TileEntityStocker stocker;
	public GuiTextField[] stockFeilds;
	public GuiTextField[] amountFeilds;
	
	public GuiStocker(InventoryPlayer player, TileEntityStocker stocker) {
		super(new ContainerStocker(player, stocker));
		stockFeilds = new GuiTextField[5];
		amountFeilds = new GuiTextField[5];
		this.stocker = stocker;
		this.xSize = 176;
		this.ySize = 208;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		refreshButtons();
		for(int i = 0; i < 5; i++){
			stockFeilds[i] = new GuiTextField(i, fontRendererObj, guiLeft+26, guiTop+22 + (18*i), 69 - 6, fontRendererObj.FONT_HEIGHT);
			stockFeilds[i].setEnableBackgroundDrawing(true);
			stockFeilds[i].setVisible(true);
			stockFeilds[i].setText(String.valueOf(stocker.stockAmts[i]));
			stockFeilds[i].setTextColor(16777215);
	        stockFeilds[i].setCanLoseFocus(false);
		}
		for(int i = 0; i < 5; i++){
			amountFeilds[i] = new GuiTextField(i+4, fontRendererObj, guiLeft+26+69, guiTop+22 + (18*i), 30, fontRendererObj.FONT_HEIGHT);
			amountFeilds[i].setEnableBackgroundDrawing(true);
			amountFeilds[i].setVisible(true);
			amountFeilds[i].setMaxStringLength(3);
			amountFeilds[i].setText(String.valueOf(stocker.craftAmounts[i]));
			amountFeilds[i].setTextColor(16777215);
			amountFeilds[i].setCanLoseFocus(false);
		}
	}
	
	public void refreshButtons(){
		this.buttonList.clear();
		for(int i = 0; i < 5; i++){
			this.buttonList.add(new GuiButton(i, guiLeft+158, guiTop+23+(i*14), 10, 10, stocker.useOre[i] ? TextFormatting.GREEN+"O" : TextFormatting.RED+"O"));
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		boolean canChangeSettings = true;
		boolean safe = stocker.getNetwork() !=null && stocker.getNetwork() instanceof EStorageNetworkClient;
		if(safe){
    		if(!stocker.getNetwork().hasAbility(mc.player, NetworkAbility.SETTINGS)){
    			canChangeSettings = false;
    		}
		}
		if(!canChangeSettings){
			ChatUtil.sendNoSpam(mc.player, Lang.localize("gui.networkability."+NetworkAbility.SETTINGS.getId()));
			return;
		}
		if(button.id >= 0 && button.id < 5){
			int index = button.id;
			boolean newOre = !stocker.useOre[index];
			stocker.useOre[index] = newOre;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Index", index);
			nbt.setBoolean("Value", newOre);
			nbt.setString("UUID", UUIDUtils.fromUUID(EntityPlayer.getUUID(CrystalMod.proxy.getClientPlayer().getGameProfile())));
			CrystalModNetwork.sendToServer(new PacketTileMessage(stocker.getPos(), "Ore", nbt));
			refreshButtons();
			return;
		}
	}

	@Override
	public void keyTyped(char c, int k) throws IOException{
		for(int i = 0; i < stockFeilds.length; i++){
			GuiTextField field = stockFeilds[i];
			if(field !=null && field.isFocused()){
				if(k == 1){
					field.setFocused(false);
					return;
				}
				final String preType = field.getText();
				if(field.textboxKeyTyped(c, k)){
					Integer quantity = Ints.tryParse(field.getText());
					if(quantity !=null){
						stocker.stockAmts[i] = quantity;
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setInteger("Index", i);
						nbt.setInteger("Value", quantity);
						CrystalModNetwork.sendToServer(new PacketTileMessage(stocker.getPos(), "StockAmount", nbt));
						return;
					} else {
						if(preType.length() > 1)field.setText(preType);
					}
				}
			}
		}
		for(int i = 0; i < amountFeilds.length; i++){
			GuiTextField field = amountFeilds[i];
			if(field !=null && field.isFocused()){
				if(k == 1){
					field.setFocused(false);
					return;
				}
				final String preType = field.getText();
				if(field.textboxKeyTyped(c, k)){
					Integer quantity = Ints.tryParse(field.getText());
					if(quantity !=null){
						stocker.craftAmounts[i] = quantity;
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setInteger("Index", i);
						nbt.setInteger("Value", quantity);
						CrystalModNetwork.sendToServer(new PacketTileMessage(stocker.getPos(), "CraftAmount", nbt));
						return;
					} else {
						if(preType.length() > 1)field.setText(preType);
					}
				}
			}
		}
		super.keyTyped(c, k);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		for(int i = 0; i < stockFeilds.length; i++){
			GuiTextField field = stockFeilds[i];
			if(field !=null){
				boolean inside = mouseX >=field.xPosition && mouseX <= field.xPosition+field.width && mouseY >= field.yPosition && mouseY <=field.yPosition+field.height;
				if(!inside){
					field.setFocused(false);
				} else {
					if(!field.isFocused()){
						field.setFocused(true);
						return;
					}
				}
			}
		}
		for(int i = 0; i < amountFeilds.length; i++){
			GuiTextField field = amountFeilds[i];
			if(field !=null){
				boolean inside = mouseX >=field.xPosition && mouseX <= field.xPosition+field.width && mouseY >= field.yPosition && mouseY <=field.yPosition+field.height;
				if(!inside){
					field.setFocused(false);
				} else {
					if(!field.isFocused()){
						field.setFocused(true);
						return;
					}
				}
			}
		}
		
		super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    Minecraft.getMinecraft().renderEngine.bindTexture(GUI_TEXTURE);
	    drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
	    
	    for(int i = 0; i < 5; i++){
	    	if(stockFeilds[i] !=null)stockFeilds[i].drawTextBox();
	    	if(amountFeilds[i] !=null)amountFeilds[i].drawTextBox();
	    }
	}

}
