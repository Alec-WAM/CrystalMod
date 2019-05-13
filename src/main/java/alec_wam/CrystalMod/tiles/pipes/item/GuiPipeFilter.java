package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.Arrays;
import java.util.Objects;

import alec_wam.CrystalMod.client.gui.GuiButtonIcon;
import alec_wam.CrystalMod.client.gui.GuiButtonIconTooltip;
import alec_wam.CrystalMod.client.gui.GuiButtonTooltip;
import alec_wam.CrystalMod.client.gui.GuiContainerBase;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packet.PacketUpdateItemNBT;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiPipeFilter extends GuiContainerBase {

	private final ItemStack filter;
	private final EnumHand hand;
	private GuiTextField textBoxName;
	private GuiButtonIcon buttonWhitelist;
	private GuiButtonIcon buttonMeta;
	private GuiButton buttonNBT;
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/pipe/filter.png");
	public GuiPipeFilter(EntityPlayer player, ItemStack filter, EnumHand hand) {
		super(new ContainerPipeFilter(player, filter, hand), TEXTURE);
		this.filter = filter;
		this.hand = hand;
		this.ySize = 144;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		this.textBoxName = new GuiTextField(0, this.fontRenderer, this.guiLeft + 45, this.guiTop + 6, 80, this.fontRenderer.FONT_HEIGHT);
        this.textBoxName.setMaxStringLength(50);
        this.textBoxName.setFocused(false);
        this.textBoxName.setEnableBackgroundDrawing(false);
        this.textBoxName.setTextColor(16777215);
        this.textBoxName.setText(ItemNBTHelper.getString(filter, "FilterName", ""));
        this.children.add(this.textBoxName);
		setupButtons();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.isPointInRegion(textBoxName.x, textBoxName.y, textBoxName.width, textBoxName.height, mouseX, mouseY)){
			this.textBoxName.setFocused(true);
		} else {
			this.textBoxName.setFocused(false);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	public void setupButtons(){
		//TODO Add tooltips
		this.buttons.clear();
		boolean blacklist = ItemNBTHelper.getBoolean(filter, "BlackList", false);
		boolean meta = ItemNBTHelper.getBoolean(filter, "MetaMatch", false);
		boolean nbt = ItemNBTHelper.getBoolean(filter, "NBTMatch", false);
		int wLIconX = 16;
		int wLIconY = blacklist ? 16 : 0;
		String wLTooltip = blacklist ? Lang.localize("gui.filter.blacklist") : Lang.localize("gui.filter.whitelist");
		buttonWhitelist = new GuiButtonIconTooltip(0, guiLeft + 16, guiTop + 26, wLIconX, wLIconY, wLTooltip) {
			@Override
			public void onClick(double mouseX, double mouseY){
				boolean blacklist = ItemNBTHelper.getBoolean(filter, "BlackList", false);
				ItemNBTHelper.setBoolean(filter, "BlackList", !blacklist);
				syncStack();
				setupButtons();
			}
		};
		
		int dIconX = 16;
		int dIconY = meta ? 32 : 48;
		buttonMeta = new GuiButtonIconTooltip(1, guiLeft + 140, guiTop + 16, dIconX, dIconY, Lang.localize("gui.filter.damage")) {
			@Override
			public void onClick(double mouseX, double mouseY){
				boolean meta = ItemNBTHelper.getBoolean(filter, "MetaMatch", false);
				ItemNBTHelper.setBoolean(filter, "MetaMatch", !meta);
				syncStack();
				setupButtons();				
			}
		};
		
		String nButton = nbt ? TextFormatting.GREEN + "N" : TextFormatting.RED + "N";
		buttonNBT = new GuiButtonTooltip(2, guiLeft + 140, guiTop + 37, 20, 20, nButton, Arrays.asList(Lang.localize("gui.filter.nbt"))) {
			@Override
			public void onClick(double mouseX, double mouseY){
				boolean nbt = ItemNBTHelper.getBoolean(filter, "NBTMatch", false);
				ItemNBTHelper.setBoolean(filter, "NBTMatch", !nbt);
				syncStack();
				setupButtons();				
			}
		};
		
		this.addButton(buttonWhitelist);
		this.addButton(buttonMeta);
		this.addButton(buttonNBT);
	}
	
	public void syncStack(){
		Minecraft.getInstance().player.setHeldItem(hand, filter);
		CrystalModNetwork.sendToServer(new PacketUpdateItemNBT(filter, hand));
	}
	
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		if(!textBoxName.isFocused())return false;
		if (this.textBoxName.charTyped(p_charTyped_1_, p_charTyped_2_)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		String name = ItemNBTHelper.getString(filter, "FilterName", "");
		if(!Objects.equals(name, textBoxName.getText())){
			ItemNBTHelper.setString(filter, "FilterName", textBoxName.getText());
			syncStack();
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		this.textBoxName.drawTextField(mouseX, mouseY, partialTicks);
	}

}
