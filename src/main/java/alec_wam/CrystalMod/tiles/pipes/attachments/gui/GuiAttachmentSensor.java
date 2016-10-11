package alec_wam.CrystalMod.tiles.pipes.attachments.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.util.GuiSlider;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageExport;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageSensor;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageSensor.CompareType;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.tiles.pipes.item.PacketPipe;
import alec_wam.CrystalMod.util.ModLogger;

public class GuiAttachmentSensor extends GuiContainer {

	private TileEntityPipeEStorage pipe;
	private EnumFacing dir;
	private GuiSlider redstoneSlider;
	private GuiTextField filterAmountField;
	public GuiAttachmentSensor(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir) {
		super(new ContainerAttachmentSensor(player, pipe, dir));
		this.pipe = pipe;
		this.dir = dir;
	}
	
	public AttachmentData getData(){
		return pipe.getAttachmentData(dir);
	}
	
	public AttachmentEStorageSensor getPart(){
		if(this.getData() == null || !(getData() instanceof AttachmentEStorageSensor))return null;
		return (AttachmentEStorageSensor)getData();
	}
	
	public void updateScreen(){
		super.updateScreen();
		filterAmountField.updateCursorCounter();
		if(getPart() == null)return;
		/*NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Dir", dir.getIndex());
		if(redstoneSlider.sliderValue !=getPart().redstonePower){
			getPart().redstonePower = redstoneSlider.sliderValue;
			nbt.setInteger("Redstone", redstoneSlider.sliderValue);
		}
		if(redstoneSlider.sliderValue !=getPart().redstonePower){
			getPart().redstonePower = redstoneSlider.sliderValue;
			nbt.setInteger("Redstone", redstoneSlider.sliderValue);
		}
		if(nbt.getKeySet().size() > 1){
			CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Attachment", nbt));
		}*/
	}
	
	@Override
	public void initGui(){
		super.initGui();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		this.filterAmountField = new GuiTextField(4, this.fontRendererObj, sx + 100, sy+18, 50, 20);
        this.filterAmountField.setFocused(true);
		refreshButtons();
	}
	
	protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.filterAmountField.isFocused())
        {
            this.filterAmountField.textboxKeyTyped(typedChar, keyCode);
            try{
            	if(!filterAmountField.getText().isEmpty()){
            		Integer val = Integer.decode(filterAmountField.getText());
            		if(val !=null){
            			int newVal = val.intValue();
            			if(getPart() !=null && getPart().filterAmount !=newVal){
            				NBTTagCompound nbt = new NBTTagCompound();
            				nbt.setInteger("Dir", dir.getIndex());
            				getPart().filterAmount = newVal;
            				nbt.setInteger("FilterAmt", newVal);
            				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Attachment", nbt));
            			}
            		}else{
            			ModLogger.info("DERP "+filterAmountField.getText());
            		}
            	}
            }catch(Exception e){
            	e.printStackTrace();
            }
        }
        super.keyTyped(typedChar, keyCode);
    }
	
	private void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		if(getPart() == null)return;
		CompareType type = getPart().compare;
		this.buttonList.add(new GuiButton(0, sx+72, sy+18, 20, 20, type.getStringValue()));
		this.buttonList.add(new GuiButton(1, sx+40, sy+50, 10, 10, "+"));
		this.buttonList.add(new GuiButton(2, sx+40, sy+60, 10, 10, "-"));
		//redstoneSlider = new GuiSlider(1, sx+10, sy+55, 1, 15);
		//redstoneSlider.sliderValue = getPart().redstonePower;
		//this.buttonList.add(redstoneSlider);
		String strong = (getPart().strongPower ? TextFormatting.GREEN : TextFormatting.RED) + "Strong";
		String ore = (getPart().useOre ? TextFormatting.GREEN : TextFormatting.RED) + "Ore";
		this.buttonList.add(new GuiButton(3, sx+62, sy+50, 40, 20, strong));
		this.buttonList.add(new GuiButton(4, sx+112, sy+50, 40, 20, ore));

        this.filterAmountField.setText(""+getPart().filterAmount);
	}
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
		if(this.isPointInRegion(50, 20, 16, 16, mouseX, mouseY)){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Dir", dir.getIndex());
			getPart().filterStack = mc.thePlayer.inventory.getItemStack();
			nbt.setTag("FilterStack", getPart().filterStack == null ? new NBTTagCompound() : getPart().filterStack.writeToNBT(new NBTTagCompound()));
			CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Attachment", nbt));
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	public void actionPerformed(GuiButton button){
		if(button.id == 0){
			int current = getPart().compare.ordinal();
			current++;
			if(current >= CompareType.values().length){
				//SKIP NOT
				current = 1;
			}
			getPart().compare = CompareType.values()[current];
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Compare", current);
			nbt.setInteger("Dir", dir.getIndex());
			CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Attachment", nbt));
			refreshButtons();
			return;
		}
		
		if(button.id == 1 || button.id == 2){
			final int last = getPart().redstonePower;
			int current = getPart().redstonePower;
			if(button.id == 1){
				if(current < 15)current++;
			}
			if(button.id == 2){
				if(current > 0)current--;
			}
			if(current !=last){
				getPart().redstonePower = current;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Redstone", current);
				nbt.setInteger("Dir", dir.getIndex());
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Attachment", nbt));
				refreshButtons();
				return;
			}
		}
		
		if(button.id == 3){
			boolean old = getPart().strongPower;
			getPart().strongPower = !old;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("Strong", getPart().strongPower);
			nbt.setInteger("Dir", dir.getIndex());
			CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Attachment", nbt));
			refreshButtons();
			return;
		}
		if(button.id == 4){
			boolean old = getPart().useOre;
			getPart().useOre = !old;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("Ore", getPart().useOre);
			nbt.setInteger("Dir", dir.getIndex());
			CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Attachment", nbt));
			refreshButtons();
			return;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/pipe_attach_basic.png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	    drawTexturedModalRect(sx+49, sy+19, 7, 83, 18, 18);
	    this.filterAmountField.drawTextBox();
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
		if(getPart() == null)return;
	    String power = ""+getPart().redstonePower;
	    this.fontRendererObj.drawString(power, 20+(getPart().redstonePower >= 10 ? 0 : this.fontRendererObj.getStringWidth(power)), 56, 0);

	    if(getPart().filterStack !=null)itemRender.renderItemIntoGUI(getPart().filterStack, 50, 20);
    }
	
}
