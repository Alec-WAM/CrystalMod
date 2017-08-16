package alec_wam.CrystalMod.tiles.pipes.estorage.security;

import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiSecurityEncoder extends GuiContainer {

	private TileSecurityEncoder encoder;
	private GuiCheckBox[] checkboxes = new GuiCheckBox[NetworkAbility.values().length];
	
	public GuiSecurityEncoder(EntityPlayer player, TileSecurityEncoder encoder) {
		super(new ContainerSecurityEncoder(player, encoder));
		this.encoder = encoder;
		ySize+=6;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		int x = 10;
		int y = 10;
		for(int i = 0; i < checkboxes.length; i++){
			NetworkAbility ability = NetworkAbility.values()[i];
			if(i == 4) {
				x = 90;
				y = 10;
			}
			y+=10;
			buttonList.add(checkboxes[i] = new GuiCheckBox(i, guiLeft+x, guiTop+y, ability.getId(), false));
		}
	}
	
	@Override
	public void updateScreen(){
		super.updateScreen();
		ItemStack card = encoder.getStackInSlot(0);
		for(int i = 0; i < checkboxes.length; i++){
			NetworkAbility ability = NetworkAbility.values()[i];
			checkboxes[i].setIsChecked(ItemStackTools.isValid(card) && ItemSecurityCard.hasAbility(card, ability));
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		for(int i = 0; i < checkboxes.length; i++){
			if(button == checkboxes[i]){
				NetworkAbility ability = NetworkAbility.values()[i];
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("Type", ability.getId());
				nbt.setBoolean("Value", checkboxes[i].isChecked());
				CrystalModNetwork.sendToServer(new PacketTileMessage(encoder.getPos(), "CardAbility", nbt));
			}
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(new ResourceLocation("crystalmod:textures/gui/estorage_security_encoder.png"));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

}
