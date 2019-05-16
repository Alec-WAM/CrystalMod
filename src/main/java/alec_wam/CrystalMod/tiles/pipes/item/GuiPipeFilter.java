package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.gui.GuiButtonIcon;
import alec_wam.CrystalMod.client.gui.GuiButtonIconTooltip;
import alec_wam.CrystalMod.client.gui.GuiButtonTooltip;
import alec_wam.CrystalMod.client.gui.GuiContainerBase;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packet.PacketUpdateItemNBT;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem.FilterSettings;
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
	private GuiButton buttonTag;
	
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
		this.textBoxName = new GuiTextField(0, this.fontRenderer, this.guiLeft + 62, this.guiTop + 6, 80, this.fontRenderer.FONT_HEIGHT);
        this.textBoxName.setMaxStringLength(50);
        this.textBoxName.setFocused(false);
        this.textBoxName.setEnableBackgroundDrawing(false);
        this.textBoxName.setTextColor(16777215);
        this.textBoxName.setText(ItemNBTHelper.getString(filter, "FilterName", ""));
        this.children.add(this.textBoxName);
		FilterSettings settings = new FilterSettings(filter);
		setupButtons(settings);
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
	
	public void setupButtons(FilterSettings settings){
		this.buttons.clear();
		int wLIconX = 16;
		int wLIconY = settings.isBlacklist() ? 16 : 0;
		String wLTooltip = settings.isBlacklist() ? Lang.localize("gui.filter.blacklist") : Lang.localize("gui.filter.whitelist");
		buttonWhitelist = new GuiButtonIconTooltip(0, guiLeft + 11, guiTop + 14, wLIconX, wLIconY, wLTooltip) {
			@Override
			public void onClick(double mouseX, double mouseY){
				boolean blacklist = settings.isBlacklist();
				settings.setBlackList(!blacklist);
				syncStack(settings);
				setupButtons(settings);
			}
		};
		
		int dIconX = 16;
		int dIconY = settings.isDamage() ? 32 : 48;
		buttonMeta = new GuiButtonIconTooltip(1, guiLeft + 34, guiTop + 14, dIconX, dIconY, Lang.localize("gui.filter.damage")) {
			@Override
			public void onClick(double mouseX, double mouseY){
				boolean meta = settings.isDamage();
				settings.setIsDamage(!meta);
				syncStack(settings);
				setupButtons(settings);				
			}
		};
		
		String nButton = settings.isNBT() ? TextFormatting.GREEN + "N" : TextFormatting.RED + "N";
		buttonNBT = new GuiButtonTooltip(2, guiLeft + 11, guiTop + 36, 20, 20, nButton, Arrays.asList(Lang.localize("gui.filter.nbt"))) {
			@Override
			public void onClick(double mouseX, double mouseY){
				boolean nbt = settings.isNBT();
				settings.setIsNBT(!nbt);
				syncStack(settings);
				setupButtons(settings);				
			}
		};
		
		String tButton = settings.useTag() ? TextFormatting.GREEN + "T" : TextFormatting.RED + "T";
		List<String> tLines = Lists.newArrayList();
		tLines.add(Lang.localize("gui.filter.tag1"));
		tLines.add(Lang.localize("gui.filter.tag2"));
		buttonTag = new GuiButtonTooltip(3, guiLeft + 34, guiTop + 36, 20, 20, tButton, tLines) {
			@Override
			public void onClick(double mouseX, double mouseY){
				boolean tag = settings.useTag();
				settings.setUseTag(!tag);
				syncStack(settings);
				setupButtons(settings);				
			}
		};
		
		this.addButton(buttonWhitelist);
		this.addButton(buttonMeta);
		this.addButton(buttonNBT);
		this.addButton(buttonTag);
	}
	
	public void syncStack(FilterSettings settings){
		if(settings !=null)settings.saveToItem(filter);
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
			syncStack(null);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		this.textBoxName.drawTextField(mouseX, mouseY, partialTicks);
	}

}
