package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.gui.ButtonIcon;
import alec_wam.CrystalMod.client.gui.ButtonIconTooltip;
import alec_wam.CrystalMod.client.gui.ButtonTooltip;
import alec_wam.CrystalMod.client.gui.GuiContainerBase;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packet.PacketUpdateItemNBT;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem.FilterSettings;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class GuiPipeFilter extends GuiContainerBase<ContainerPipeFilter> {

	private final ItemStack filter;
	private final Hand hand;
	private TextFieldWidget textBoxName;
	private ButtonIcon buttonWhitelist;
	private ButtonIcon buttonMeta;
	private Button buttonNBT;
	private Button buttonTag;
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/pipe/filter.png");
	public GuiPipeFilter(int windowId, PlayerEntity player, ItemStack filter, Hand hand) {
		super(new ContainerPipeFilter(windowId, player, filter, hand), player.inventory, new StringTextComponent("PipeFilter"), TEXTURE);
		this.filter = filter;
		this.hand = hand;
		this.ySize = 144;
	}
	
	@Override
	public void init(){
		super.init();
		this.textBoxName = new TextFieldWidget(this.font, this.guiLeft + 62, this.guiTop + 6, 80, this.font.FONT_HEIGHT, "");
        this.textBoxName.setMaxStringLength(50);
        this.textBoxName.setFocused2(false);
        this.textBoxName.setEnableBackgroundDrawing(false);
        this.textBoxName.setTextColor(16777215);
        this.textBoxName.setText(ItemNBTHelper.getString(filter, "FilterName", ""));
        this.children.add(this.textBoxName);
		FilterSettings settings = new FilterSettings(filter);
		setupButtons(settings);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.isPointInRegion(textBoxName.x, textBoxName.y, textBoxName.getWidth(), textBoxName.getHeight(), mouseX, mouseY)){
			this.textBoxName.setFocused2(true);
		} else {
			this.textBoxName.setFocused2(false);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	public void setupButtons(FilterSettings settings){
		this.buttons.clear();
		int wLIconX = 16;
		int wLIconY = settings.isBlacklist() ? 16 : 0;
		String wLTooltip = settings.isBlacklist() ? Lang.localize("gui.filter.blacklist") : Lang.localize("gui.filter.whitelist");
		buttonWhitelist = new ButtonIconTooltip(guiLeft + 11, guiTop + 14, wLIconX, wLIconY, wLTooltip, new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				boolean blacklist = settings.isBlacklist();
				settings.setBlackList(!blacklist);
				syncStack(settings);
				setupButtons(settings);
			}
		});
		
		int dIconX = 16;
		int dIconY = settings.isDamage() ? 32 : 48;
		buttonMeta = new ButtonIconTooltip(guiLeft + 34, guiTop + 14, dIconX, dIconY, Lang.localize("gui.filter.damage"), new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				boolean meta = settings.isDamage();
				settings.setIsDamage(!meta);
				syncStack(settings);
				setupButtons(settings);				
			}
		});
		
		String nButton = settings.isNBT() ? TextFormatting.GREEN + "N" : TextFormatting.RED + "N";
		buttonNBT = new ButtonTooltip(guiLeft + 11, guiTop + 36, 20, 20, nButton, Arrays.asList(Lang.localize("gui.filter.nbt")), new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				boolean nbt = settings.isNBT();
				settings.setIsNBT(!nbt);
				syncStack(settings);
				setupButtons(settings);				
			}
		});
		
		String tButton = settings.useTag() ? TextFormatting.GREEN + "T" : TextFormatting.RED + "T";
		List<String> tLines = Lists.newArrayList();
		tLines.add(Lang.localize("gui.filter.tag1"));
		tLines.add(Lang.localize("gui.filter.tag2"));
		buttonTag = new ButtonTooltip(guiLeft + 34, guiTop + 36, 20, 20, tButton, tLines, new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				boolean tag = settings.useTag();
				settings.setUseTag(!tag);
				syncStack(settings);
				setupButtons(settings);				
			}
		});
		
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
	public void onClose() {
		super.onClose();
		String name = ItemNBTHelper.getString(filter, "FilterName", "");
		if(!Objects.equals(name, textBoxName.getText())){
			ItemNBTHelper.putString(filter, "FilterName", textBoxName.getText());
			syncStack(null);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		this.textBoxName.render(mouseX, mouseY, partialTicks);
	}

}
